package xbrlonto;

//XbrlParseをインポート
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import xbrlparse.XbrlParser;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.db.DBConnection;
import java.io.FileInputStream;


//xbrlインスタンスから得られたデータを受け取り、
//rdfを生成し、rdfデータをデータベースに格納するために渡すクラス
//TODO 一般的に。すべてのRDF出力に対応できるように。(2010/01/31)

/**
 * RDFを生成するためのクラス。スレッド。
 */
public class RDFMaker extends Thread {

	XbrlParser XBRLPARSER;
	//EdinetData EDINETDATA;
	private String nsCls;
	private String nsIns;
	private String nsPrp;
	private String nsFoaf;
	private String tdbloc;
	private String outputRDFPath;
	private String jdbcUrl;
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getDBUser() {
		return DBUser;
	}

	public void setDBUser(String dBUser) {
		DBUser = dBUser;
	}

	public String getDBPassword() {
		return DBPassword;
	}

	public void setDBPassword(String dBPassword) {
		DBPassword = dBPassword;
	}

	public String getDBType() {
		return DBType;
	}

	public void setDBType(String dBType) {
		DBType = dBType;
	}

	private String DBUser;
	private String DBPassword;
	private String DBType;

	public void setXBRLPARSER(XbrlParser parser){
		XBRLPARSER=parser;
	}

	public void run(){
		RDFbinding(XBRLPARSER);
	}

	//xbrlインスタンスから生成したデータを受け取る
	public void RDFbinding(XbrlParser xbrlparser){
		
		Map<String, String> nsmap = new HashMap<String, String>();
		nsmap.put("xbrlont_class", this.getNsCls());
		nsmap.put("xbrlont_ins", this.getNsIns());
		nsmap.put("xbrlont_property", this.getNsPrp());
		nsmap.put("foaf", this.getNsFoaf());

		// in the first time, you should create default model.
		//Model model = ModelFactory.createDefaultModel();
		/*
		 * locationを指定.
		 * 公開するディレクトリに構築するのが良い
		 */
		Model model = TDBFactory.createModel(this.getTdbloc());

		/*
		 * 以下、RDFモデルに関する記述
		 */
		//抽出してきた名前空間をここでも認識させる。
		//ただし、valueの末尾に#をつける。
		Set<String> keys = xbrlparser.getNamespaceMapping().keySet();
		Iterator<String> ite = keys.iterator();
		while(ite.hasNext()) {              //ループ
			String str = ite.next();        //該当オブジェクト取得
			//勘定科目に関するprefix
			String value = xbrlparser.getNamespaceMapping().get(str);
			if(!value.substring(value.length()).equals("#")){
				value = value + "#";
			}
			//valueを入れなおす。
			xbrlparser.getNamespaceMapping().put(str, value);
		}


		model.setNsPrefixes(xbrlparser.getNamespaceMapping());
		model.setNsPrefixes(nsmap);

		//まず、会社名のresource作成。foaf:Organizationにタイプ付け。
		System.out.println(nsIns + xbrlparser.getDocumentInfoMapping().get("EntityNameJaEntityInformation").getValue());
		Resource company = model.createResource(nsIns + xbrlparser.getDocumentInfoMapping().get("EntityNameJaEntityInformation").getValue());
		Resource foafOrg = model.createResource(nsFoaf + "Organization");
		company.addProperty(RDF.type, foafOrg);

		//次に、company hasEdicode [EDINETCODE]をつける。
		System.out.println(nsIns + xbrlparser.getContextInfoMapping().get("DocumentInfo").getEdinetCode());
		Resource edicode = model.createResource(nsIns + xbrlparser.getContextInfoMapping().get("DocumentInfo").getEdinetCode());
		Property hasEdicode = model.createProperty(nsPrp, "hasEDINETCode");
		company.addProperty(hasEdicode, edicode);

		//jpfr-t-<?> hasAccountをつくる
		//とりあえずjpfr-t-cte→jpfr-t-cteである必要はないかもしれない。ない。
		//Resource accountElement = model.createResource(xbrlparser.getNamespaceMapping().get("jpfr-t-cte") + "SurplusDeficitFND");

		//iteratorで回す。対象はInstanceInfoMappingが対象。１つ１つの勘定科目に対するループ。
		Resource accountElement = null;
		Set<String> InstanceKeys = xbrlparser.getInstanceInfoMapping().keySet();
		Iterator<String> InstanceIte = InstanceKeys.iterator();
		while(InstanceIte.hasNext()){
			String Istr = InstanceIte.next();
			System.out.println("今の項目は→" + xbrlparser.getInstanceInfoMapping().get(Istr).getLocalName() + " 値は：" + xbrlparser.getInstanceInfoMapping().get(Istr).getValue());
			//jpfr-t-cte:ローカル名的なのをリソースにする。ちょっと調整必要かも
			accountElement = model.createResource(xbrlparser.getInstanceInfoMapping().get(Istr).getNamespaceURI() + "#" + xbrlparser.getInstanceInfoMapping().get(Istr).getLocalName());
			Property hasAccount = model.createProperty(nsIns, "hasAccount");
			edicode.addProperty(hasAccount, accountElement);

			//accountElementにvalueをつける。hasAmountプロパティで。
			Resource blankValue = model.createResource();
			Property hasAmount = model.createProperty(nsIns, "hasAmount");
			accountElement.addProperty(hasAmount, blankValue);

			//空白ノードに金額情報をつける。数値はリテラルで、型はxsd:integer
			if(!(xbrlparser.getInstanceInfoMapping().get(Istr).getValue()==null)){
				Literal valueOfAccount = model.createTypedLiteral(xbrlparser.getInstanceInfoMapping().get(Istr).getValue(), XSD.integer.getURI());
				blankValue.addLiteral(RDF.value, valueOfAccount);
			}else{
				Literal valueOfAccount = model.createTypedLiteral(0, XSD.integer.getURI());
				blankValue.addLiteral(RDF.value, valueOfAccount);
			}

			//金額情報の通貨単位をつける iso4217:JPYとか。 ちゃんとURI解決させる。
			Resource unitOfAccount = model.createResource(xbrlparser.getNamespaceMapping().get(xbrlparser.getUnitInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getUnitRef()).getMeasurePrefix()) + xbrlparser.getUnitInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getUnitRef()).getMeasureLocalName());
			Property hasUnit = model.createProperty(nsIns, "hasUnit");
			blankValue.addProperty(hasUnit, unitOfAccount);

			//日付情報を付属させる。そのエレメントが属するクラスによって変わってくる。
			//contextInfoからとる。contextRefから参照できるcontextInfoの、periodInstantまたはstartDate,endDateをとってくる。
			//contextInfoのidによってクラスをInstantとDurationに分けてあるので、それぞれからとるようにする。
			if(xbrlparser.getContextInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getContextRef()) instanceof xbrlparse.InstantDocumentInfo){
				Literal Instant = model.createTypedLiteral(xbrlparser.getContextInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getContextRef()).getPeriodInstant(), XSDDateType.XSDdate);
				Property hasInstant = model.createProperty(nsIns, "hasInstant");
				accountElement.addLiteral(hasInstant, Instant);
			}
			else if(xbrlparser.getContextInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getContextRef()) instanceof xbrlparse.DurationDocumentInfo){
				Literal startDate = model.createTypedLiteral(xbrlparser.getContextInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getContextRef()).getStartDate(), XSDDateType.XSDdate);
				Literal endDate = model.createTypedLiteral(xbrlparser.getContextInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getContextRef()).getEndDate(),XSDDateType.XSDdate);
				Property hasStartDate = model.createProperty(nsIns, "hasStartDate");
				Property hasEndDate = model.createProperty(nsIns, "hasEndDate");
				accountElement.addLiteral(hasStartDate, startDate);
				accountElement.addLiteral(hasEndDate, endDate);
			}
			else{}
		}

		try {
			connectDB(model);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 単一のファイルを読み込むメソッド
	 * @param inputFileName
	 */
	public void readModelTest(String inputFileName) {
		Model model=TDBFactory.createModel(this.getTdbloc());
		InputStream in = FileManager.get().open(inputFileName);
		if(in == null){
			throw new IllegalArgumentException("File: " + inputFileName + " not found");
		}
		// read the RDF/XML file
		model.read(in, null);

		// write it to standard out
		model.write(System.out);
	}
	
	/**
	 * ローカルファイルにrdfを出力する関数。
	 * @param model
	 */
	public void outputRDF(Model model) {
		OutputStream out = null;

		File outFile = new File(this.getOutputRDFPath());
		try {
			outFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		model.write(out);
	}

	public void connectDB(Model model) throws ClassNotFoundException, SQLException{
		//JDBC

		Class.forName("com.mysql.jdbc.Driver");

		String URL = this.getJdbcUrl();
		String USER = this.getDBUser();
		String PASSWORD = this.getDBPassword();
		String TYPE = this.getDBType();

		DBConnection conn = new DBConnection(URL, USER, PASSWORD, TYPE);

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);

		Model xbrlDBmodel = maker.createModel("xbrlInstance");

		xbrlDBmodel.add(model);

		//outputの形式はN-TRIPLEでなければならない。RDF/XMLだとBadURIのエラーが出る。
		xbrlDBmodel.write(System.out, "N-TRIPLE");

		conn.close();

		// list the statements in the Model
//		StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		/*while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object

		    System.out.print(subject.toString());
		    System.out.print(" " + predicate.toString() + " ");
		    if (object instanceof Resource) {
		       System.out.print(object.toString());
		    } else {
		        // object is a literal
		        System.out.print(" \"" + object.toString() + "\"");
		    }

		    System.out.println(" .");
		}*/
		model.write(System.out, "N-TRIPLE");
	}


	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String configFile = "prop.conf";	//for default
		Properties prop = new Properties();
		try {
		   prop.load(new FileInputStream(configFile));
		} catch (IOException e) {
		   e.printStackTrace();
		   return;
		}
		
		RDFMaker maker = new RDFMaker();

		maker.setNsCls(prop.getProperty("nsClass"));
		maker.setNsIns(prop.getProperty("nsInstance"));
		maker.setNsPrp(prop.getProperty("nsProperty"));
		maker.setNsFoaf(prop.getProperty("nsFoaf"));
		maker.setTdbloc(prop.getProperty("tdbFactoryLoc"));
		maker.setOutputRDFPath(prop.getProperty("outputRDFPath"));

		maker.readModelTest(args[0]);
	}

	public void setNsCls(String nsCls) {
		this.nsCls = nsCls;
	}

	public String getNsCls() {
		return nsCls;
	}

	public void setNsIns(String nsIns) {
		this.nsIns = nsIns;
	}

	public String getNsIns() {
		return nsIns;
	}

	public void setNsPrp(String nsPrp) {
		this.nsPrp = nsPrp;
	}

	public String getNsPrp() {
		return nsPrp;
	}
	
	public String getNsFoaf() {
		return nsFoaf;
	}

	public void setNsFoaf(String nsFoaf) {
		this.nsFoaf = nsFoaf;
	}

	public void setTdbloc(String tdbloc) {
		this.tdbloc = tdbloc;
	}

	public String getTdbloc() {
		return tdbloc;
	}

	public void setOutputRDFPath(String outputRDFPath) {
		this.outputRDFPath = outputRDFPath;
	}

	public String getOutputRDFPath() {
		return outputRDFPath;
	}
	
}

