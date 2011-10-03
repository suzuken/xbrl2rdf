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
import tecaSQLite.EdinetData;
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
import com.hp.hpl.jena.vocabulary.RDFS;
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
	EdinetData EDINETDATA;
	private String nsCls;
	private String nsIns;
	private String nsPrp;
	private String nsFoaf;
	

	

	public void setXBRLPARSER(XbrlParser parser){
		XBRLPARSER=parser;
	}

	public void setEdinetData(EdinetData ed){
		EDINETDATA = ed;
	}

	public void run(){
		//RDFbinding(XBRLPARSER);
		RDFbinding_2010(XBRLPARSER, EDINETDATA);
	}

	public void RDFbinding_2010(XbrlParser xbrlparser, EdinetData edinetdata){
		//山口研究室用名前空間
//		final String nsCls = "http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/class#";
//		final String nsIns = "http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/instance#";
//		final String nsPrp = "http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/property/";
//		final String nsFoaf = "http://xmlns.com/foaf/0.1/";
		

		Map<String, String> nsmap = new HashMap<String, String>();
		nsmap.put("xbrlont_class", nsCls);
		nsmap.put("xbrlont_ins", nsIns);
		nsmap.put("xbrlont_property", nsPrp);
		nsmap.put("foaf", nsFoaf);

		//Model model = TDBFactory.createModel("DB/xbrlonto");
		//Model model = TDBFactory.createModel("../XbrlOntologySearch/WebContent/WEB-INF/DB/xbrl_ontology_20100728");
		/*
		 * とりあえず作っておく
		 */
		Model model = ModelFactory.createDefaultModel();
		/*
		 * モデルの初期化用。
		 */
		model.removeAll();

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


		/*
		 * 以下、モデルの組み立て
		 */

		//まず、会社名のresource作成。foaf:Organizationにタイプ付け。
		Resource company = model.createResource(nsIns + xbrlparser.getDocumentInfoMapping().get("EntityNameJaEntityInformation").getValue());
		Resource foafOrg = model.createResource(nsFoaf + "Organization");
		company.addProperty(RDF.type, foafOrg);

		Resource edicode = model.createResource(nsIns + xbrlparser.getContextInfoMapping().get("DocumentInfo").getEdinetCode());
		Property hasEdicode = model.createProperty(nsPrp, "hasEDINETCode");
		company.addProperty(hasEdicode, edicode);

		/*
		 * ここから個々の報告書に関する記述
		 */
		Resource documentInfo = model.createResource(nsIns + edinetdata.getDocid());
		//TODO ファイル名を取得
		//Literal xbrlFileName = model.createLiteral("");
		//documentInfo.addProperty(RDFS.label, xbrlFileName);
		//Resource document
		/**
		 * 書類の種類。
		 * ・有価証券報告書
		 * ・四半期報告書
		 * ・半期報告書
		 * ・有価証券届出書
		 * など。
		 */
		Resource documentType = model.createResource(nsCls + edinetdata.getDoctype());
		documentInfo.addProperty(RDF.type, documentType);

		/*
		 * ここからcontextInfo関連の記述
		 */
		Property hasContext = model.createProperty(nsPrp, "hasContext");
		Property period = model.createProperty(nsPrp, "period");
		Property instant = model.createProperty(nsPrp, "instant");
		Property startDate = model.createProperty(nsPrp, "startDate");
		Property endDate = model.createProperty(nsPrp, "endDate");

		//前期個別損益計算書・前々期連結貸借対照表、など
		Resource contextInfo = null;
		Set<String> ContextKeys = xbrlparser.getContextInfoMapping().keySet();
		Iterator<String> ContextIte = ContextKeys.iterator();
		while(ContextIte.hasNext()){
			String Cstr = ContextIte.next();
			contextInfo = model.createResource(nsIns + xbrlparser.getContextInfoMapping().get(Cstr).getId());
			documentInfo.addProperty(hasContext, contextInfo);

			if(xbrlparser.getContextInfoMapping().get(Cstr) instanceof xbrlparse.InstantDocumentInfo){
				Resource instant_class = model.createResource(nsCls + "Instant");
				contextInfo.addProperty(RDF.type, instant_class);

				Resource contextPeriod = model.createResource(nsIns + "ContextPeriod");
				contextInfo.addProperty(period, contextPeriod);

				Literal instant_date = model.createTypedLiteral(xbrlparser.getContextInfoMapping().get(Cstr).getPeriodInstant(), XSDDateType.XSDdate);
				contextPeriod.addProperty(instant, instant_date);
			}
			else if(xbrlparser.getContextInfoMapping().get(Cstr) instanceof xbrlparse.DurationDocumentInfo){
				Resource duration_class = model.createResource(nsCls + "Duration");
				contextInfo.addProperty(RDF.type, duration_class);

				Resource contextPeriod = model.createResource(nsIns + "ContextPeriod");
				contextInfo.addProperty(period, contextPeriod);

				Literal start_date = model.createTypedLiteral(xbrlparser.getContextInfoMapping().get(Cstr).getStartDate(), XSDDateType.XSDdate);
				contextInfo.addProperty(startDate, start_date);

				Literal end_date = model.createTypedLiteral(xbrlparser.getContextInfoMapping().get(Cstr).getEndDate(), XSDDateType.XSDdate);
				contextInfo.addProperty(endDate, end_date);
			}
		}

		/*
		 * ここからインスタンス情報（金額情報）の記述
		 */
		//iteratorで回す。対象はInstanceInfoMappingが対象。１つ１つの勘定科目に対するループ。
		Resource accountElement = null;
		Set<String> InstanceKeys = xbrlparser.getInstanceInfoMapping().keySet();
		Iterator<String> InstanceIte = InstanceKeys.iterator();
		while(InstanceIte.hasNext()){
			String Istr = InstanceIte.next();
			System.out.println("今の項目は→" + xbrlparser.getInstanceInfoMapping().get(Istr).getLocalName() + " 値は：" + xbrlparser.getInstanceInfoMapping().get(Istr).getValue());
			//jpfr-t-cte:ローカル名的なのをリソースにする。ちょっと調整必要かも
			accountElement = model.createResource(xbrlparser.getInstanceInfoMapping().get(Istr).getNamespaceURI() + "#" + xbrlparser.getInstanceInfoMapping().get(Istr).getLocalName());
			Property monetaryItem = model.createProperty(nsPrp, "monetaryItem");
			documentInfo.addProperty(monetaryItem, accountElement);

			//TODO 正式な英語名称を追加できるように
			Literal item_name = model.createLiteral(xbrlparser.getInstanceInfoMapping().get(Istr).getLocalName());
			accountElement.addProperty(RDFS.label, item_name);

			//TODO 日本語名称も追加できるように
			//Literal item_name_ja = model.createLiteral("日本語ラベルここに");
			//Property label_ja = model.createProperty(nsPrp, "label_ja");
			//accountElement.addProperty(label_ja, item_name_ja);

			//空白ノードに金額情報をつける。数値はリテラルで、型はxsd:integer
			if(!(xbrlparser.getInstanceInfoMapping().get(Istr).getValue()==null)){
				Literal valueOfAccount = model.createTypedLiteral(xbrlparser.getInstanceInfoMapping().get(Istr).getValue(), XSD.integer.getURI());
				accountElement.addLiteral(RDF.value, valueOfAccount);
			}else{
				Literal valueOfAccount = model.createTypedLiteral(0, XSD.integer.getURI());
				accountElement.addLiteral(RDF.value, valueOfAccount);
			}

			//金額情報の通貨単位をつける iso4217:JPYとか。 ちゃんとURI解決させる。
			Resource unitOfAccount = model.createResource(xbrlparser.getNamespaceMapping().get(xbrlparser.getUnitInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getUnitRef()).getMeasurePrefix()) + xbrlparser.getUnitInfoMapping().get(xbrlparser.getInstanceInfoMapping().get(Istr).getUnitRef()).getMeasureLocalName());
			Property unitRef = model.createProperty(nsPrp, "unitRef");
			accountElement.addProperty(unitRef, unitOfAccount);

			//勘定科目タイプの作成及びrdf:type
			Resource class_account = model.createResource(nsCls + xbrlparser.getInstanceInfoMapping().get(Istr).getLocalName());
			accountElement.addProperty(RDF.type, class_account);

			//表示単位
			Literal decimals = model.createTypedLiteral(xbrlparser.getInstanceInfoMapping().get(Istr).getDecimals(), XSD.integer.getURI());
			Property xbrli_decimals = model.createProperty(nsPrp, "decimals");
			accountElement.addProperty(xbrli_decimals, decimals);

			Resource contextRef = model.createResource(nsIns + xbrlparser.getInstanceInfoMapping().get(Istr).getContextRef());
			Property context = model.createProperty(nsPrp, "context");
			accountElement.addProperty(context, contextRef);
		}

		//ローカルフォルダへ書き出し。
		OutputStream out = null;


		File outFile = new File("../XbrlOntologySearch/WebContent/WEB-INF/DB/xbrl_ontology_20100728.rdf");
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

		model.close();
	}

	//xbrlインスタンスから生成したデータを受け取る
	public void RDFbinding(XbrlParser xbrlparser){
		//山口研究室用名前空間
		final String nsCls = "http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/class#";
		final String nsIns = "http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/instance#";
		final String nsPrp = "http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/property/";
		final String nsFoaf = "http://xmlns.com/foaf/0.1/";

		Map<String, String> nsmap = new HashMap<String, String>();
		nsmap.put("xbrlont_class", nsCls);
		nsmap.put("xbrlont_ins", nsIns);
		nsmap.put("xbrlont_property", nsPrp);
		nsmap.put("foaf", nsFoaf);

		//それぞれRDFモデルを作成する。
		//Model model = ModelFactory.createDefaultModel();
		/*
		 * locationを指定.
		 * 公開するディレクトリに構築するのが良い
		 */
		Model model = TDBFactory.createModel("../xbrl_ontology/DB/xbrl_ontology_20100728");
		//Model model = TDBFactory.createModel("DB/xbrlonto");


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
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	/**
	 * 単一のファイルを読み込むメソッド
	 * @param inputFileName
	 */
	public void readModelTest(String inputFileName) {
		//Model model = ModelFactory.createDefaultModel();
		Model model=TDBFactory.createModel("C:\\workspace_suzuken\\xbrl2rdf\\tdb");
		InputStream in = FileManager.get().open(inputFileName);
		if(in == null){
			throw new IllegalArgumentException("File: " + inputFileName + " not found");
		}
		// read the RDF/XML file
		model.read(in, null);

		// write it to standard out
		model.write(System.out);
	}

	public void connectDB(Model model) throws ClassNotFoundException, SQLException{
		//JDBC

		Class.forName("com.mysql.jdbc.Driver");

		String URL = "jdbc:mysql://localhost/mydb";
		String USER = "root";
		String PASSWORD = "hoge";
		String TYPE = "MySQL";

		DBConnection conn = new DBConnection(URL, USER, PASSWORD, TYPE);
//		conn.getDriver().setDoCompressURI(true);



		ModelMaker maker = ModelFactory.createModelRDBMaker(conn);

		Model xbrlDBmodel = maker.createModel("xbrlInstance");

		//モデルになんか加える。他のメソッドからもってくる。
		xbrlDBmodel.add(model);

		//ModelRDB rdbmodel=(ModelRDB)maker.openModel("test");

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


		//ローカルフォルダへ書き出し。
		OutputStream out = null;


		File outFile = new File("./output/test.rdf");
		try {
			outFile.createNewFile();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			out = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		model.write(out);

	}


	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String configFile = "prop.conf";
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
	
}

