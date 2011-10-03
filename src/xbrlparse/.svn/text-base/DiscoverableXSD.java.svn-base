package xbrlparse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 発見可能なスキーマファイルのクラス。
 * @author suzuken
 *スキーマファイルに含まれる情報は、このクラス内に保持される。
 */
public class DiscoverableXSD extends DiscoverableTaxonomySet{
	/**
	 * スキーマの子要素となる、DiscoverableTaxonomySetを格納。スキーマか、リンクベースファイルが格納される。
	 */
	private ArrayList<DiscoverableTaxonomySet> children = new ArrayList<DiscoverableTaxonomySet>();
	/**
	 * スキーマに含まれるElement要素のマッピング。
	 */
	private Map<String, ElementFromXSD> ElementMapping;
	/**
	 * スキーマに含まれる名前空間のマッピング。キーはprefix。
	 */
	private Map<String, String>namespaceMapping = new HashMap<String, String>();
	/**
	 * スキーマに記述されていたroleTypeのマッピング。
	 */
	private Map<String, RoleType>roleTypeMapping = new HashMap<String, RoleType>();

	/**
	 * import要素のnamespace属性。
	 */
	private String namespace;

	/**
	 * コンストラクタ
	 */
	public DiscoverableXSD(){}
	public DiscoverableXSD(String URI){URI_ = URI;}
	public DiscoverableXSD(String URI, String namespace){URI_ = URI; this.setNamespace(namespace);}

	/**
	 * 名前空間を得るメソッド
	 * @return
	 */
	public Map<String, String> getNamespaceMapping() {
		return namespaceMapping;
	}
	public void setNamespaceMapping(Map<String, String> namespaceMapping) {
		this.namespaceMapping = namespaceMapping;
	}
	public Map<String, RoleType> getRoleTypeMapping() {
		return roleTypeMapping;
	}
	public void setRoleTypeMapping(Map<String, RoleType> roleTypeMapping) {
		this.roleTypeMapping = roleTypeMapping;
	}
	public Map<String, ElementFromXSD> getElementMapping() {return ElementMapping;}
	public void setElementMapping(Map<String, ElementFromXSD> elementMapping) {ElementMapping = elementMapping;}

	/**
	 * URIを得るメソッド。このスキーマのURIを返す。
	 */
	@Override
	public String getValue(){
	    return URI_;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getNamespace() {
		return namespace;
	}

	@Override
	public ArrayList<DiscoverableTaxonomySet> getChildren(){
		return children;
	}

	/**
	 * 子となるタクソノミをarraylistに追加するメソッド
	 */
	@Override
	public void add(DiscoverableTaxonomySet dts){
		children.add(dts);
	}

	/**
	 * 子を探すクラス。子あるならtrue, いなければfalse。
	 */
	@Override
	public Boolean hasChild(){
		ArrayList<DiscoverableTaxonomySet> list = getChildren();
		if(list.size() == 0){return false;}
		else{return true;}
	}

	@Override
	public String toString() {
		return "DiscoverableXSD [ElementMapping=" + ElementMapping
				+ ", children=" + children + ", namespace=" + namespace
				+ ", namespaceMapping=" + namespaceMapping
				+ ", roleTypeMapping=" + roleTypeMapping + "]";
	}
}