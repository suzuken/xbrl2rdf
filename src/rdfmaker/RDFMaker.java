package rdfmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

import xbrlreader.Account;
import xbrlreader.AccountImpl;
import xbrlreader.Context;
import xbrlreader.Unit;
import xbrlreader.XbrlReader;

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
public class RDFMaker implements Maker {
	
	public XbrlReader x;
	public String dest;
	public Model model;
	
	private String nsCls;
	private String nsIns;
	private String nsPrp;

	private String nsFoaf;
	private String tdbloc;
	private String outputRDFPath;
	private String outputRDFDir;

	private String[] enableContextRef;
	
	
	//for testing
	// 0: xbrl's url
	// 1: destination folder or output filename's path
	public static void main(String[] args) throws TransformerException, SAXException, IOException, ParserConfigurationException, XPathExpressionException{
		long start = System.currentTimeMillis();
		
		RDFMaker r = new RDFMaker(args);
		r.createModel();
		r.model.write(System.out);
		
		//取得
		long stop = System.currentTimeMillis();
		
		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
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

	
	public RDFMaker(String args[]) throws TransformerException, SAXException, IOException, ParserConfigurationException{
		super();
		this.x = new XbrlReader(args[0]);
		this.x.prepare();
		this.dest = args[1];
		
		String configFile = "app.conf"; // for default
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(configFile));
			prop.list(System.out);
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
		this.setNsCls(prop.getProperty("nsClass"));
		this.setNsIns(prop.getProperty("nsInstance"));
		this.setNsPrp(prop.getProperty("nsProperty"));
		this.setNsFoaf(prop.getProperty("nsFoaf"));
		this.setTdbloc(prop.getProperty("tdbFactoryLoc"));
		this.setOutputRDFDir(prop.getProperty("outputRDFDir"));
		this.setOutputRDFPath(prop.getProperty("outputRDFDir") + getFileName(args[0]) + ".rdf");
	}
	

	
	/**
	 *　modelを作成するメソッド 
	 * @throws XPathExpressionException 
	 */
	public void createModel() throws XPathExpressionException{
		this.model = TDBFactory.createModel();
		
		Resource company = this.model.createResource(this.getNsIns() + this.x.getDocumentInfo("EntityNameJaEntityInformation").getValue());
		Resource foafOrg = this.model.createResource(this.getNsFoaf()
				+ "Organization");
		company.addProperty(RDF.type, foafOrg);

		// 次に、company hasEdicode [EDINETCODE] (ex. E01777-000)をつける。
		Resource edicode = this.model.createResource(this.getNsIns()
				+ this.x.getContext("DocumentInfo").getIdentifier());
		Property hasEdicode = this.model.createProperty(this.getNsPrp(),
				"hasEDINETCode");
		company.addProperty(hasEdicode, edicode);

		// jpfr-t-<?> hasAccountをつくる
		Resource accountElement= null;
		
		//ContextごとにRDF modelを生成
		for(Integer j = 0; j < this.getEnableContextRef().length; j++){
			String contextRef = this.getEnableContextRef()[j];
			if(contextRef != null){

				ArrayList<Account> a = this.x.getAccountsByContext(contextRef);
//				ArrayList<Account> a = this.x.getAccountsByContext("Prior1YearNonConsolidatedDuration");
				if(!(a.isEmpty())){
					for (int i = 0; i < a.size(); i++){
						for (Account account : a){
							accountElement = this.model.createResource(account.getNamespaceURI()
									+ account.getLocalName());
							Property hasAccount = this.model.createProperty(this.getNsIns(), "hasAccount");
							edicode.addProperty(hasAccount, accountElement);
							
							// accountElementにvalueをつける。hasAmountプロパティで。
							Resource blankValue = this.model.createResource();
							Property hasAmount = this.model.createProperty(this.getNsIns(),
									"hasAmount");
							accountElement.addProperty(hasAmount, blankValue);

							// 空白ノードに金額情報をつける。数値はリテラルで、型はxsd:integer
							Literal valueOfAccount = this.model.createTypedLiteral(
									(account.getValue() != null) ? account.getValue() : 0,
									XSD.integer.getURI());
							blankValue.addLiteral(RDF.value, valueOfAccount);
							
							// 金額情報の通貨単位をつける iso4217:JPYとか。
							Unit unit = this.x.getUnit(account.getUnitRef());
							Resource unitOfAccount = this.model.createResource(
									unit.getMeasureNamespaceURI() + "#" + unit.getMeasureLocalName());
							Property hasUnit = this.model.createProperty(this.getNsIns(), "hasUnit");
							blankValue.addProperty(hasUnit, unitOfAccount);

							// 日付情報を付属させる。そのエレメントが属するクラスによって変わってくる。
							// contextInfoからとる。contextRefから参照できるcontextInfoの、periodInstantまたはstartDate,endDateをとってくる。
							// contextInfoのidによってクラスをInstantとDurationに分けてあるので、それぞれからとるようにする。
							Context c = this.x.getContext(contextRef);
							if(c.getPeriodInstant() != null){
								Literal Instant = this.model.createTypedLiteral(c.getPeriodInstant(), 
										XSDDateType.XSDdate);
								Property hasInstant = this.model.createProperty(this.getNsIns(),
										"hasInstant");
								accountElement.addLiteral(hasInstant, Instant);
							}
							else if(c.getPeriodStartDate() != null){
								Literal startDate = this.model.createTypedLiteral(c.getPeriodStartDate(),
										XSDDateType.XSDdate);
								Literal endDate = this.model.createTypedLiteral(c.getPeriodEndDate(),
										XSDDateType.XSDdate);
								Property hasStartDate = this.model.createProperty(this.getNsIns(),
										"hasStartDate");
								Property hasEndDate = this.model.createProperty(this.getNsIns(),
										"hasEndDate");
								accountElement.addLiteral(hasStartDate, startDate);
								accountElement.addLiteral(hasEndDate, endDate);
							}
							else{
								System.out.println("periodの指定が不正です。");
							}

						}
					}
				}
			}
		}
	}
	
	public Model getModel(){
		return this.model;
	}
	
	public String getNsCls() {
		return nsCls;
	}

	public void setNsCls(String nsCls) {
		this.nsCls = nsCls;
	}

	public String getNsIns() {
		return nsIns;
	}

	public void setNsIns(String nsIns) {
		this.nsIns = nsIns;
	}

	public String getNsPrp() {
		return nsPrp;
	}

	public void setNsPrp(String nsPrp) {
		this.nsPrp = nsPrp;
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
	
}