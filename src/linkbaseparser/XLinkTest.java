package linkbaseparser;
import com.fujitsu.xml.xlink.dataset.*;
import com.fujitsu.xml.xlink.processor.*;

public class XLinkTest {

	public static void main(String[] args){
		XLinkProcessor xp = new XLinkProcessor();
		xp.createXLinkDataSet(args[0]);
		XLinkDataSet xds = xp.getDataSet();
		
		// リンク開始リソースを保持するロケータ情報リスト
	    // テーブルを取得します。
	    LocatorHashtable table = xds.getResourceObjects();
	    
	    LocatorList locatorlist = table.get(null);

	}
}
