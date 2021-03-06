package rdfmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import util.FileSearch;
import xbrlreader.Account;
import xbrlreader.Context;
import xbrlreader.SimpleNamespaceContext;
import xbrlreader.Unit;
import xbrlreader.XbrlReader;
import namespace.XBRLOWL;

import javax.xml.namespace.NamespaceContext;


/**
 * RDFを出力するためのクラス
 * 
 * 生成したxbrlのrdfモデルを構築するためのクラス
 * データベースへの挿入や、fileへのアウトプットは外部のクラスに任せる
 * Xbrlからのデータ取得には@class XbrlReaderを利用する。
 * 将来的にはデータベースからの取得にも対応する。
 * 
 * @author suzuken
 */
public class RDFMakerV2 implements Maker {

	private static final ExampleMode ALL = null;
	public XbrlReader x;
	public String dest;
	public Model model;

	private String nsOwl;

	private String nsFoaf;
	private String tdbloc;

	private String outputRDFPath;

	@Option(name="-o",usage="Set a directory for output RDF files")
	public String outputRDFDir;

	@Option(name="-r",usage="recursively parsing XBRL, and generate RDF files")
	private static boolean recursive=true;

	private String[] enableContextRef;

	@Argument
	private List<String> arguments = new ArrayList<String>();
	private String path;


	//for testing
	// 0: xbrl's url
	// 1: destination folder or output filename's path
	/**
	 * -xbrl: XBRLファイルのパスを指定
	 * -output: 出力先のパスを指定（デフォルトは./output）
	 * 
	 * @param args
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void main(String[] args) throws TransformerException, SAXException, IOException, ParserConfigurationException, XPathExpressionException{
		long start = System.currentTimeMillis();
		System.out.println("処理開始しました。");
		FileSearch search = new FileSearch();
		//		Integer last_slash = args[0].lastIndexOf('/');
		//		String directoryPath = args[0].substring(0, last_slash);
		//		String filePath = args[0].substring(last_slash + 1, args[0].length());
		File[] files = search.listFiles(args[0], args[1], 2, recursive, 0);
		//TODO アスタリスクじゃない場合も考える。絶対パスの場合はどうなるか検証する。
		for (Integer i = 0, ii=files.length; i<ii; i++){
			RDFMakerV2 r = new RDFMakerV2(files[i].getAbsolutePath());
			r.doMain(args);
		}
		long stop = System.currentTimeMillis();

		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
	}

	public void doMain(String[] args) throws XPathExpressionException{
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
			if(arguments.isEmpty()){
				throw new CmdLineException("No argument is given");
			}
		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("java SampleMain [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			// print option sample. This is useful some time
			System.err.println(" Example: java SampleMain"+parser.printExample(ALL));
			return;
		}

		//全部作成
		this.createModel();
		
		//labelだけアップデート
		//this.createCompanyJaNameModel();
		
		//contextTypeだけアップデート
		//this.createContextTypeModel();
		this.outputRDF(this.model);
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
	 * @param path XBRLファイルのパス
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public RDFMakerV2(String path) throws TransformerException, SAXException, IOException, ParserConfigurationException{
		super();
		this.x = new XbrlReader(path);
		this.path = path;


		this.x.prepare();
		this.dest = this.outputRDFDir;

		String configFile = "app.conf"; // for default
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(configFile));
			//prop.list(System.out);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String[] buffer = prop.getProperty("enableContextRef").split("\\,");
		String[] contexts = new String[buffer.length];
		for(Integer i =0; i < buffer.length; i++){
			contexts[i] = buffer[i].trim();	//先頭及び最後の空白を取り除く
		}
		this.setEnableContextRef(contexts);
		this.setNsOwl(prop.getProperty("nsOwl"));
		this.setNsFoaf(prop.getProperty("nsFoaf"));
		this.setTdbloc(prop.getProperty("tdbFactoryLoc"));
		this.setOutputRDFDir(prop.getProperty("outputRDFDir"));
		//		this.setOutputRDFPath(prop.getProperty("outputRDFDir") + getFileName(args[0]) + ".rdf");
		this.setOutputRDFPath(prop.getProperty("outputRDFDir") + getFileName(path) + ".rdf");
	}

	public void createCompanyJaNameModel(){
		try{
			this.model = TDBFactory.createModel();
			this.model.setNsPrefix("xbrlowl", XBRLOWL.getURI());
			this.model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");

			SimpleNamespaceContext snc = (SimpleNamespaceContext) this.x.nsc;
			HashMap<String, String> map = snc.getPrefixToUri();
			Set<String> keySet = map.keySet();
			Iterator<String> keyIte = keySet.iterator();
			while(keyIte.hasNext()){
				String key = keyIte.next();
				String value = map.get(key);
				if(key != "xml" && key != "xmlns"){
					String vend = value.substring(value.length());
					if(vend != "/" && vend != "#"){
						value = value + "#";
					}
					this.model.setNsPrefix(key, value);
				}
			}

			String companyName = 
					this.x.getDocumentInfo("EntityNameJaEntityInformation").getValue();
			companyName = companyName.replaceAll("\\[", "［");
			companyName = companyName.replaceAll("\\]", "］");

			Resource company = this.model.createResource(XBRLOWL.getURI()
					+ companyName);
			company.addProperty(RDFS.label, companyName);
		} catch (Exception e){
			System.out.println("modelの出力でエラー");
			e.printStackTrace();
		}


	}
	
	//contextにタイプをつけ忘れたので、付け加える。
	public void createContextTypeModel(){
		try {

			this.model = TDBFactory.createModel();
			this.model.setNsPrefix("xbrlowl", XBRLOWL.getURI());
			this.model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
			
			SimpleNamespaceContext snc = (SimpleNamespaceContext) this.x.nsc;
			HashMap<String, String> map = snc.getPrefixToUri();
			Set<String> keySet = map.keySet();
			Iterator<String> keyIte = keySet.iterator();
			while(keyIte.hasNext()){
				String key = keyIte.next();
				String value = map.get(key);
				if(key != "xml" && key != "xmlns"){
					String vend = value.substring(value.length());
					if(vend != "/" && vend != "#"){
						value = value + "#";
					}
					this.model.setNsPrefix(key, value);
				}
			}

			//ContextごとにRDF modelを生成
			for(Integer j = 0; j < this.getEnableContextRef().length; j++){
				String contextRef = this.getEnableContextRef()[j];
				System.out.println(contextRef);
				if(contextRef != null){

					//これがContextのElement。これにtype付けする。
					Resource contextElement = this.model.createResource(
							this.getNsOwl() + getFileName(this.path)
							.substring(1, getFileName(this.path).length())
							+ "-" + contextRef);
					
					//xbrlowl:Prior2YearNonConsolidatedとか。
					Resource contextType = this.model.createResource(
							XBRLOWL.getURI() + contextRef
							);
					contextElement.addProperty(RDF.type, contextType);

				}
			}
		} catch (Exception e){
			System.out.println("modelの出力でエラー");
			e.printStackTrace();
		}

	}

	/**
	 *　modelを作成するメソッド 
	 *  TODO contextのノードを作成して、hasContextプロパティで各勘定科目のノードをつなげてあげれば、より使いやすくなりそう。
	 *  TODO 文書情報についてもちゃんとノードを作成して情報を埋め込む。基本的にはxbrlの表示を買えないように作る。
	 * @throws XPathExpressionException 
	 */
	public void createModel(){
		System.out.println();
		try {

			this.model = TDBFactory.createModel();
			this.model.setNsPrefix("xbrlowl", XBRLOWL.getURI());
			this.model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
			
			SimpleNamespaceContext snc = (SimpleNamespaceContext) this.x.nsc;
			HashMap<String, String> map = snc.getPrefixToUri();
			Set<String> keySet = map.keySet();
			Iterator<String> keyIte = keySet.iterator();
			while(keyIte.hasNext()){
				String key = keyIte.next();
				String value = map.get(key);
				if(key != "xml" && key != "xmlns"){
					String vend = value.substring(value.length());
					if(vend != "/" && vend != "#"){
						value = value + "#";
					}
					this.model.setNsPrefix(key, value);
				}
			}
			
			String companyName = 
					this.x.getDocumentInfo("EntityNameJaEntityInformation").getValue();
			companyName = companyName.replaceAll("\\[", "［");
			companyName = companyName.replaceAll("\\]", "］");

			Resource company = this.model.createResource(XBRLOWL.getURI()
					+ companyName);
			company.addProperty(RDF.type, FOAF.Organization);
			company.addProperty(RDFS.label, companyName);

			Resource companyType = this.model.createResource(XBRLOWL.company);
			company.addProperty(RDF.type, companyType);

			// 次に、company hasEdicode [EDINETCODE] (ex. E01777-000)をつける。
			Resource edicode = this.model.createResource(XBRLOWL.getURI()
					+ this.x.getContext("DocumentInfo").getIdentifier());
			company.addProperty(XBRLOWL.hasEDINETCode, edicode);

			//report要素をつける
			//例: xbrl-owl: jpfr-asr-E01777-000-2009-03-31-01-2009-06-19
			Resource report = this.model.createResource(XBRLOWL.getURI()
					+ getFileName(path));
			company.addProperty(XBRLOWL.hasReport, report);

			//inversePropertyであるhasCompanyをつける
			report.addProperty(XBRLOWL.hasCompany, report);

			//reportをxbrl-owl:Reportにtypeづけする。
			report.addProperty(RDF.type, XBRLOWL.report);

			//publishDateを付与する
			Literal pDate = this.model.createTypedLiteral(
					this.x.getContext("DocumentInfo").getPeriodInstant(),
					XSDDateType.XSDdate);
			report.addProperty(XBRLOWL.publishDate, pDate);

			// jpfr-t-<?> hasAccountをつくる
			Resource itemElement= null;

			//ContextごとにRDF modelを生成
			for(Integer j = 0; j < this.getEnableContextRef().length; j++){
				String contextRef = this.getEnableContextRef()[j];
				if(contextRef != null){

					//ContextElementの生成
					Context c = this.x.getContext(contextRef);

					Resource contextElement = this.model.createResource(
							this.getNsOwl() + getFileName(this.path)
							.substring(1, getFileName(this.path).length())
							+ "-" + contextRef);
					report.addProperty(XBRLOWL.hasContext, contextElement);

					//scenario情報の付与
					if(c.getScenario() != null){
						Resource scenario = this.model.createResource(
								c.getScenarioNamespaceURI() + "#" + c.getScenarioLocalName());
						contextElement.addProperty(XBRLOWL.scenario, scenario);
					}

					// 日付情報を付属させる。そのエレメントが属するクラスによって変わってくる。
					if(c.getPeriodInstant() != null && c.getPeriodInstant() != ""){
						Literal Instant = this.model.createTypedLiteral(c.getPeriodInstant(), 
								XSDDateType.XSDdate);
						contextElement.addLiteral(XBRLOWL.hasInstant, Instant);
					}
					else if(c.getPeriodStartDate() != null && c.getPeriodStartDate() != ""
							&& c.getPeriodEndDate() != null && c.getPeriodEndDate() != ""){
						Literal startDate = this.model.createTypedLiteral(c.getPeriodStartDate(),
								XSDDateType.XSDdate);
						Literal endDate = this.model.createTypedLiteral(c.getPeriodEndDate(),
								XSDDateType.XSDdate);
						contextElement.addLiteral(XBRLOWL.startDate, startDate);
						contextElement.addLiteral(XBRLOWL.endDate, endDate);
					}
					
					//xbrlowl:Prior2YearNonConsolidatedとか。
					//contextのタイプ付け(2012/01/24)
					Resource contextType = this.model.createResource(
							XBRLOWL.getURI() + contextRef
							);
					contextElement.addProperty(RDF.type, contextType);


					ArrayList<Account> a = this.x.getAccountsByContext(contextRef);
					if(!(a.isEmpty())){
						//勘定科目ごとのループ
						for (Account item : a){ 
							//reportリソースに勘定科目情報を対応付ける
							//itemの名前が重複していたので変更 2012/01/24
							itemElement = this.model.createResource(item.getNamespaceURI() + "#"
							+ getFileName(this.path).substring(1, getFileName(this.path).length())
							+ "-"+item.getLocalName() + "-" + contextRef);
							company.addProperty(XBRLOWL.hasItem, itemElement);

							//itemをxbrl-owl:itemにタイプづけ
							itemElement.addProperty(RDF.type, XBRLOWL.item);

							//contextをitemに関連付ける
							itemElement.addProperty(XBRLOWL.context, contextElement);

							//itemに値を付与
							if(item.getValue() != null){
								Literal valueOfAccount = this.model.createTypedLiteral(
										(item.getValue() != null) ? item.getValue() : 0,
												XSD.integer.getURI());
								itemElement.addProperty(RDF.value, valueOfAccount);
							}

							//itemに表示のための単位を付与
							if(item.getDecimals() != null && item.getDecimals() != ""){
								Literal decimal = this.model.createTypedLiteral(
										(item.getDecimals() != null) ? item.getDecimals() : 0,
												XSD.integer.getURI());
								itemElement.addProperty(XBRLOWL.decimal, decimal);
							}

							// 金額情報の通貨単位をつける iso4217:JPYとか。
							Unit unit = this.x.getUnit(item.getUnitRef());
							Resource unitOfAccount = this.model.createResource(
									unit.getMeasureNamespaceURI() + "#" + unit.getMeasureLocalName());
							itemElement.addProperty(XBRLOWL.unit, unitOfAccount);

							//itemを勘定科目オントロジーと対応付ける
							Resource accountHeading = this.model.createResource(
									item.getNamespaceURI() + "#" + item.getLocalName());
							itemElement.addProperty(RDF.type, accountHeading);
							
							//itemに簡易的な勘定科目の名前空間に対応させる
							Resource accountHeadingType = this.model.createResource(
									XBRLOWL.getURI() + item.getLocalName());
							itemElement.addProperty(RDF.type, accountHeadingType);
						}
					}
				}
			}
		} catch (Exception e){
			System.out.println("modelの出力でエラー");
			e.printStackTrace();
		}
	}

	public Model getModel(){
		return this.model;
	}

	public String getNsFoaf() {
		return nsFoaf;
	}

	public void setNsFoaf(String nsFoaf) {
		this.nsFoaf = nsFoaf;
	}

	public String getTdbloc() {
		return tdbloc;
	}

	public void setTdbloc(String tdbloc) {
		this.tdbloc = tdbloc;
	}

	public String getOutputRDFPath() {
		return outputRDFPath;
	}

	public void setOutputRDFPath(String outputRDFPath) {
		this.outputRDFPath = outputRDFPath;
	}

	public String getOutputRDFDir() {
		return outputRDFDir;
	}

	public void setOutputRDFDir(String outputRDFDir) {
		this.outputRDFDir = outputRDFDir;
	}
	public String[] getEnableContextRef() {
		return enableContextRef;
	}

	public void setEnableContextRef(String[] enableContextRef) {
		this.enableContextRef = enableContextRef;
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
	public String getNsOwl() {
		return nsOwl;
	}

	public void setNsOwl(String nsOwl) {
		this.nsOwl = nsOwl;
	}


}