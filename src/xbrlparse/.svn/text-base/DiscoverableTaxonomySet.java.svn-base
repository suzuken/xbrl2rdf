package xbrlparse;

import java.util.ArrayList;

/**
 * DiscoverableTaxonomySet(DTS)を格納するクラスの抽象クラス。
 * @author suzuken
 *
 *Compositeモデルを使用。この抽象クラスは、クライアントに対して、Linkbase及びスキーマファイルのURI情報を提供する。
 *また、DTSは階層関係をもつ。実際に階層を作るのは、スキーマファイルによるインポート及びリンクベースへの参照である。
 *<xsd:import>によってインポート対象となったURIは、DiscoverableXSDとして親DTSにaddされ、
 *<link:linkbaseRef>のxlink:href属性によって指定されたuriはDiscoverableLinkBaseとして、同様にaddされる。
 */
//透明クラス
public abstract class DiscoverableTaxonomySet{
	protected String URI_;

	public DiscoverableTaxonomySet(){}
	public DiscoverableTaxonomySet(String uri){this.URI_ = uri;}

	public String getURI_() {
		return URI_;
	}
	public void setURI_(String uRI) {
		URI_ = uRI;
	}
	public abstract String getValue();
	public void add(DiscoverableTaxonomySet DTS){throw new RuntimeException();}
	public abstract ArrayList<DiscoverableTaxonomySet> getChildren();
	public abstract Boolean hasChild();
}