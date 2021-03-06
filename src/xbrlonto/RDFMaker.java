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
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.db.DBConnection;
import java.io.FileInputStream;
import java.net.URISyntaxException;

import org.openjena.riot.system.PrefixMap;

//xbrlインスタンスから得られたデータを受け取り、
//rdfを生成し、rdfデータをデータベースに格納するために渡すクラス
//TODO 一般的に。すべてのRDF出力に対応できるように。(2010/01/31)

/**
 * RDFを生成するためのクラス。スレッド。
 */
public class RDFMaker extends Thread {

	XbrlParser XBRLPARSER;
	// EdinetData EDINETDATA;
	private String nsCls;
	private String nsIns;
	private String nsPrp;
	private String nsFoaf;
	private String tdbloc;
	private String outputRDFPath;
	private String jdbcUrl;
	private String DBUser;
	private String DBPassword;
	private String DBType;
	private String outputRDFDir;

	public void setXBRLPARSER(XbrlParser parser) {
		XBRLPARSER = parser;
	}

	public void run() {
		RDFbinding(XBRLPARSER);
	}

	// xbrlインスタンスから生成したデータを受け取る
	public void RDFbinding(XbrlParser xbrlparser) {

		// in the first time, you should create default model.
		// Model model = ModelFactory.createDefaultModel();
		/*
		 * locationを指定. 公開するディレクトリに構築するのが良い
		 */
		Model model = TDBFactory.createModel(this.getTdbloc());
		
		/*
		 * 以下、RDFモデルに関する記述
		 */
		

//		Map<String, String> testmap = xbrlparser.getNamespaceMapping();
		
//		model.setNsPrefixes(xbrlparser.getNamespaceMapping());
//		model.setNsPrefixes(nsmap);

		// まず、会社名のresource作成。foaf:Organizationにタイプ付け。
		System.out.println(this.getNsIns()
				+ xbrlparser.getDocumentInfoMapping().get(
						"EntityNameJaEntityInformation").getValue());
		Resource company = model.createResource(this.getNsIns()
				+ xbrlparser.getDocumentInfoMapping().get(
						"EntityNameJaEntityInformation").getValue());
		Resource foafOrg = model.createResource(this.getNsFoaf()
				+ "Organization");
		company.addProperty(RDF.type, foafOrg);

		// 次に、company hasEdicode [EDINETCODE]をつける。
		System.out.println(this.getNsIns()
				+ xbrlparser.getContextInfoMapping().get("DocumentInfo")
						.getEdinetCode());
		Resource edicode = model.createResource(this.getNsIns()
				+ xbrlparser.getContextInfoMapping().get("DocumentInfo")
						.getEdinetCode());
		Property hasEdicode = model.createProperty(this.getNsPrp(),
				"hasEDINETCode");
		company.addProperty(hasEdicode, edicode);

		// jpfr-t-<?> hasAccountをつくる
		// とりあえずjpfr-t-cte→jpfr-t-cteである必要はないかもしれない。ない。
		// Resource accountElement =
		// model.createResource(xbrlparser.getNamespaceMapping().get("jpfr-t-cte")
		// + "SurplusDeficitFND");

		// iteratorで回す。対象はInstanceInfoMappingが対象。１つ１つの勘定科目に対するループ。
		Resource accountElement = null;
		Set<String> InstanceKeys = xbrlparser.getInstanceInfoMapping().keySet();
		Iterator<String> InstanceIte = InstanceKeys.iterator();
		while (InstanceIte.hasNext()) {
			String Istr = InstanceIte.next();
			System.out.println("今の項目は→"
					+ xbrlparser.getInstanceInfoMapping().get(Istr)
							.getLocalName() + " 値は："
					+ xbrlparser.getInstanceInfoMapping().get(Istr).getValue());
			// jpfr-t-cte:ローカル名的なのをリソースにする。ちょっと調整必要かも
			accountElement = model.createResource(xbrlparser
					.getInstanceInfoMapping().get(Istr).getNamespaceURI()
					+ "#"
					+ xbrlparser.getInstanceInfoMapping().get(Istr)
							.getLocalName());
			Property hasAccount = model.createProperty(this.getNsIns(),
					"hasAccount");
			edicode.addProperty(hasAccount, accountElement);

			// accountElementにvalueをつける。hasAmountプロパティで。
			Resource blankValue = model.createResource();
			Property hasAmount = model.createProperty(this.getNsIns(),
					"hasAmount");
			accountElement.addProperty(hasAmount, blankValue);

			// 空白ノードに金額情報をつける。数値はリテラルで、型はxsd:integer
			if (!(xbrlparser.getInstanceInfoMapping().get(Istr).getValue() == null)) {
				Literal valueOfAccount = model.createTypedLiteral(xbrlparser
						.getInstanceInfoMapping().get(Istr).getValue(),
						XSD.integer.getURI());
				blankValue.addLiteral(RDF.value, valueOfAccount);
			} else {
				Literal valueOfAccount = model.createTypedLiteral(0,
						XSD.integer.getURI());
				blankValue.addLiteral(RDF.value, valueOfAccount);
			}

			// 金額情報の通貨単位をつける iso4217:JPYとか。 ちゃんとURI解決させる。
			Resource unitOfAccount = model.createResource(xbrlparser
					.getNamespaceMapping().get(
							xbrlparser.getUnitInfoMapping().get(
									xbrlparser.getInstanceInfoMapping().get(
											Istr).getUnitRef())
									.getMeasurePrefix())
					+ xbrlparser.getUnitInfoMapping().get(
							xbrlparser.getInstanceInfoMapping().get(Istr)
									.getUnitRef()).getMeasureLocalName());
			Property hasUnit = model.createProperty(this.getNsIns(), "hasUnit");
			blankValue.addProperty(hasUnit, unitOfAccount);

			// 日付情報を付属させる。そのエレメントが属するクラスによって変わってくる。
			// contextInfoからとる。contextRefから参照できるcontextInfoの、periodInstantまたはstartDate,endDateをとってくる。
			// contextInfoのidによってクラスをInstantとDurationに分けてあるので、それぞれからとるようにする。
			if (xbrlparser.getContextInfoMapping().get(
					xbrlparser.getInstanceInfoMapping().get(Istr)
							.getContextRef()) instanceof xbrlparse.InstantDocumentInfo) {
				Literal Instant = model.createTypedLiteral(xbrlparser
						.getContextInfoMapping().get(
								xbrlparser.getInstanceInfoMapping().get(Istr)
										.getContextRef()).getPeriodInstant(),
						XSDDateType.XSDdate);
				Property hasInstant = model.createProperty(this.getNsIns(),
						"hasInstant");
				accountElement.addLiteral(hasInstant, Instant);
			} else if (xbrlparser.getContextInfoMapping().get(
					xbrlparser.getInstanceInfoMapping().get(Istr)
							.getContextRef()) instanceof xbrlparse.DurationDocumentInfo) {
				Literal startDate = model.createTypedLiteral(xbrlparser
						.getContextInfoMapping().get(
								xbrlparser.getInstanceInfoMapping().get(Istr)
										.getContextRef()).getStartDate(),
						XSDDateType.XSDdate);
				Literal endDate = model.createTypedLiteral(xbrlparser
						.getContextInfoMapping().get(
								xbrlparser.getInstanceInfoMapping().get(Istr)
										.getContextRef()).getEndDate(),
						XSDDateType.XSDdate);
				Property hasStartDate = model.createProperty(this.getNsIns(),
						"hasStartDate");
				Property hasEndDate = model.createProperty(this.getNsIns(),
						"hasEndDate");
				accountElement.addLiteral(hasStartDate, startDate);
				accountElement.addLiteral(hasEndDate, endDate);
			} else {
			}
		}

//		model.setNsPrefix("xbrlontclass", this.getNsCls());
//		model.setNsPrefix("xbrlontins", this.getNsIns());
//		model.setNsPrefix("xbrlontproperty", this.getNsPrp());
//		model.setNsPrefix("foaf", this.getNsFoaf());
		
		// 抽出してきた名前空間をここでも認識させる。
		// ただし、valueの末尾に#をつける。
		Set<String> keys = xbrlparser.getNamespaceMapping().keySet();
		Iterator<String> ite = keys.iterator();
		while (ite.hasNext()) { // ループ
			String str = ite.next(); // 該当オブジェクト取得
			// 勘定科目に関するprefix
			String value = xbrlparser.getNamespaceMapping().get(str);
			if (!value.substring(value.length()).equals("#")) {
				value = value + "#";
			}
			// valueを入れなおす。
//			xbrlparser.getNamespaceMapping().put(str, value);
//			model.setNsPrefix(str, value);
		}
//		try {
//			connectDB(model);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		outputRDF(model);
	}

	/**
	 * 単一のファイルを読み込むメソッド
	 * 
	 * @param inputFileName
	 * @deprecated
	 */
	public void readModelTest(String inputFileName) {
		Model model = TDBFactory.createModel(this.getTdbloc());
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName
					+ " not found");
		}
		// read the RDF/XML file
		model.read(in, null);

		// write it to standard out
		model.write(System.out);
	}

	/**
	 * ローカルファイルにrdfを出力する関数。
	 * 
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

		System.out.println("出力完了しました。");
		model.write(out);
	}

	/**
	 * relational databaseに格納するためのメソッド
	 * 
	 * @param model
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void insertModelDB(Model model) throws ClassNotFoundException,
			SQLException {
		// JDBC

		Class.forName("com.mysql.jdbc.Driver");

		String URL = this.getJdbcUrl();
		String USER = this.getDBUser();
		String PASSWORD = this.getDBPassword();
		String TYPE = this.getDBType();

		DBConnection conn = new DBConnection(URL, USER, PASSWORD, TYPE);

		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);

		Model xbrlDBmodel = maker.createModel("xbrlInstance");

		xbrlDBmodel.add(model);

		// outputの形式はN-TRIPLEでなければならない。RDF/XMLだとBadURIのエラーが出る。
		xbrlDBmodel.write(System.out, "N-TRIPLE");

		conn.close();

		// list the statements in the Model
		// StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		/*
		 * while (iter.hasNext()) { Statement stmt = iter.nextStatement(); //
		 * get next statement Resource subject = stmt.getSubject(); // get the
		 * subject Property predicate = stmt.getPredicate(); // get the
		 * predicate RDFNode object = stmt.getObject(); // get the object
		 * 
		 * System.out.print(subject.toString()); System.out.print(" " +
		 * predicate.toString() + " "); if (object instanceof Resource) {
		 * System.out.print(object.toString()); } else { // object is a literal
		 * System.out.print(" \"" + object.toString() + "\""); }
		 * 
		 * System.out.println(" ."); }
		 */
		model.write(System.out, "N-TRIPLE");
	}

	public void createRDF(String[] args) throws URISyntaxException, IOException{
		//XbrlParserにxbrlファイルをセット
		XbrlParser xp = new XbrlParser(args[0]);
		xp.parseStart();
		this.RDFbinding(xp);
	}
	/**
	 * ランチャーメソッド。引数にxbrlファイルを指定することで、RDFを生成する。
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, URISyntaxException, IOException {
		String configFile = "app.conf"; // for default
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
		maker.setOutputRDFDir(prop.getProperty("outputRDFDir"));
		maker.setOutputRDFPath(prop.getProperty("outputRDFDir") + getFileName(args[0]) + ".rdf");

		maker.createRDF(args);
	}
	
	/**
	 * ファイル名から拡張子を取り除く
	 * @param filename
	 * @return
	 */
	public static String getFileName(String filename){
		if(filename == null){
			return null;
		}
		int point = filename.lastIndexOf(".");
		int slash = filename.lastIndexOf("/");
		if(point != -1){
			return filename.substring(slash, point);
		}
		return filename;
	}

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

	public void setOutputRDFDir(String outputRDFDir) {
		this.outputRDFDir = outputRDFDir;
	}

	public String getOutputRDFDir() {
		return outputRDFDir;
	}

}
