/**
 *
 */
package xbrlparse;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import EdinetBase.EdinetData;
import EdinetBase.SQLiteConnector;

/**
 * XBRL解析の基点となるプロセッサ
 * @author suzuken
 *
 */
public class XbrlProcessor {
	/**
	 * 発見可能なXMLスキーマを格納するメンバ変数
	 */
	private static Map <String, DiscoverableXSD> xsdmap = new HashMap<String, DiscoverableXSD>();
	/**
	 * 発見可能なリンクベースを格納するメンバ変数
	 */
	private static Map <String, DiscoverableLinkBase> lbmap = new HashMap<String, DiscoverableLinkBase>();
	/**
	 * xbrlファイルをリストするメンバ変数
	 */
	private static List<String> xbrlList = new ArrayList<String>();

	public static Map<String, DiscoverableXSD> getXsdmap() {return xsdmap;}
	public static void setXsdmap(Map<String, DiscoverableXSD> DiscoverableXSD) {xsdmap = DiscoverableXSD;}
	public static Map<String, DiscoverableLinkBase> getLbmap() {return lbmap;}
	public static void setLbmap(Map<String, DiscoverableLinkBase> DiscoverableLinkBase) {lbmap = DiscoverableLinkBase;}

	public static void main(String[] args){
		try {
			//特定のフォルダ以下のすべてのxbrlファイルに付いてパースさせる。
			//File targetDir = null;
			//targetDir = new File(args[0]);
			//LoopDirectory(targetDir);

			SQLiteConnector sc = new SQLiteConnector();
			sc.start(args);

			/*
			 * TODO SQLiteConnectorで抽出したxbrlurlに対してパースを開始する
			 * イテレーターで回す。
			 */
			/*			Set<String> ContextKeys = xbrlparser.getContextInfoMapping().keySet();
			Iterator<String> ContextIte = ContextKeys.iterator();
			while(ContextIte.hasNext()){
*/
			String xbrlurl =null;
			Set<Integer> resultKeys = sc.getResultmapping().keySet();
			Iterator<Integer> resultIte = resultKeys.iterator();
			while(resultIte.hasNext()){
				Integer i = resultIte.next();
				//EdinetData ed = sc.getResultmapping().get(i);
				xbrlurl = sc.getResultmapping().get(i).getXbrlurl();


			//xbrlListのすべてのxbrlについて、パース開始。
			//for (Iterator<String> i = xbrlList.iterator(); i.hasNext();) {
				//スレッド型の、XbrlParserインスタンスを起動。
				//Thread xp = new XbrlParser(i.next());
				System.out.println("スレッドを開始します");
				//Thread xp = new XbrlParser(args[0]);
				Thread xp = new XbrlParser(xbrlurl);
				xp.start();

				//xpというインスタンスが管理するスレッドが終了するのをまつ。
				xp.join();

				//終了した。
				System.out.println("スレッド終了しました");

				//

				//ここでスキーマのパーサを起動する。
				// TODO スキーマパーサから取得した各種リンクベースのデータをXBRLインスタンスの要素にマッピングする
//				SchemaParser sp = SchemaParserStarter(((XbrlParser) xp).getSchemaURI());
//
//				ArrayList<DiscoverableTaxonomySet> list = sp.getDXSD().getChildren();
//
//				ListParseStarter(list);


				/*
				 * rdfMakerの起動。
				 */
				xbrlonto.RDFMaker rdfmaker = new xbrlonto.RDFMaker();
				rdfmaker.setXBRLPARSER((XbrlParser) xp);
				rdfmaker.start();
				

			}

			//}

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void LoopDirectory(File targetDir){
		if (targetDir.exists() && targetDir.isDirectory()) {
			File[] fileList = targetDir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if(fileList[i].getAbsolutePath().substring(fileList[i].getAbsolutePath().length()-5, fileList[i].getAbsolutePath().length()).equals(".xbrl")){
					xbrlList.add(fileList[i].getAbsolutePath());
				}
				LoopDirectory(new File(fileList[i].getAbsolutePath()));
			}
		}
	}

	public static void ListParseStarter(ArrayList<DiscoverableTaxonomySet> list) throws URISyntaxException, IOException{
		//印刷テスト。この部分、ループにする。
		for (Object obj : list) {
			String URI = ((DiscoverableTaxonomySet) obj).URI_;
            if (obj instanceof DiscoverableXSD) {    //スキーマの場合
            	//インスタンスを生成し、パースをスタートさせる！
            	SchemaParser sp2 = SchemaParserStarter(URI);
            	if (sp2.getDXSD().hasChild()){
            		ArrayList<DiscoverableTaxonomySet> sp2list = sp2.getDXSD().getChildren();
            		ListParseStarter(sp2list);
            	}
            } else if (obj instanceof DiscoverableLinkBase) {    //リンクベースの場合
            	LinkBaseParseStarter((DiscoverableLinkBase) obj, URI);
            }
        }
	}

	public static SchemaParser SchemaParserStarter(String URI) throws URISyntaxException, IOException{
		SchemaParser sp = new SchemaParser(URI);
		sp.startProcess();
		getXsdmap().put(sp.getDXSD().getURI_(), sp.getDXSD());
		return sp;
	}


	/**
	 * リンクベースのパースをスタートさせるためのメソッド。種別ごとに振り分ける。
	 * @param obj
	 * @param URI
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void LinkBaseParseStarter(DiscoverableLinkBase obj, String URI) throws MalformedURLException, IOException{
		//インスタンスを生成したいが、これは不便。どうしよう。
    	//factoryにすればよかったかなぁ
    	//とりあえず、ここで分岐つくる。かっこ悪いけど。
    	if(((DiscoverableLinkBase) obj).getRoleType().equals("PRESENTATION_LINKBASE")){
    		LinkBaseParser lp = new PresentationLinkParser(URI);
        	lp.startProcess();
        	getLbmap().put(lp.getDLB().getURI_(), lp.getDLB());
    	}else if(((DiscoverableLinkBase) obj).getRoleType().equals("DEFINITION_LINKBASE")){
    		LinkBaseParser lp = new DefinitionLinkParser(URI);
        	lp.startProcess();
        	getLbmap().put(lp.getDLB().getURI_(), lp.getDLB());
    	}else if(((DiscoverableLinkBase) obj).getRoleType().equals("REFERENCE_LINKBASE")){
    		LinkBaseParser lp = new ReferenceLinkParser(URI);
        	lp.startProcess();
        	getLbmap().put(lp.getDLB().getURI_(), lp.getDLB());
    	}else if(((DiscoverableLinkBase) obj).getRoleType().equals("LABEL_LINKBASE")){
    		LinkBaseParser lp = new LabelLinkParser(URI);
        	lp.startProcess();
        	getLbmap().put(lp.getDLB().getURI_(), lp.getDLB());
    	}else if(((DiscoverableLinkBase) obj).getRoleType().equals("CALCULATION_LINKBASE")){
    		LinkBaseParser lp = new CalculationLinkParser(URI);
        	lp.startProcess();
        	getLbmap().put(lp.getDLB().getURI_(), lp.getDLB());
    	}
	}
}
