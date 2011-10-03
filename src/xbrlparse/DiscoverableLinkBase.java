package xbrlparse;

import java.util.ArrayList;
import java.util.Map;

public class DiscoverableLinkBase extends DiscoverableTaxonomySet {


	public static final String presentationLinkBaseRef = "http://www.xbrl.org/2003/role/presentationLinkbaseRef";
	public static final String labelLinkBaseRef = "http://www.xbrl.org/2003/role/labelLinkbaseRef";
	public static final String definitionLinkBaseRef = "http://www.xbrl.org/2003/role/definitionLinkbaseRef";
	public static final String calculationLinkBaseRef = "http://www.xbrl.org/2003/role/calculationLinkbaseRef";
	public static final String referenceLinkBaseRef = "http://www.xbrl.org/2003/role/referenceLinkbaseRef";

	/**
	 * 名前空間のマッピング
	 */
	private Map<String, String> namespaceMapping;
	/**
	 * roleRefのマッピング
	 */
	private Map<String, RoleRefInfo> roleRefMapping;
	/**
	 * ロケータのマッピング。
	 */
	private Map<String, Locator> locatorMapping;
	/**
	 * リソースのマッピング
	 */
	private Map<String, Resource> resourceMapping;
	/**
	 * Arcのマッピング
	 */
	private Map<String, Arc> arcMapping;

	//この情報持つ必要ある？
	//リンクベース自体を識別する情報。必要。
	/**
	 * リンクベースが何型で指定されたかを示すメンバ変数。通常はsimple。
	 */
	private String type;
	/**
	 * xlink:role属性によって指定された値。
	 * labellinkbaseRef
	 * referenceLinkbaseRef
	 * definitionLinkbaseRef
	 * calculationLinkbaseRef
	 * presentationLinkbaseRef
	 * のいずれかが入る。
	 */
	private String role;
	/**
	 * xlink:arcrole属性によって指定された値。
	 * http://www.w3.org/1999/xlink/properties/linkbase
	 * が指定されるべき値である。
	 */
	private String arcrole;
	/**
	 *リンクベースが、どのタイプかを格納しておく変数。
	 *presentation, calculation, label, definition, reference
	 *のどれか
	 */
	private String roleType = null;


	public DiscoverableLinkBase(String URI){URI_ = URI;}
	public DiscoverableLinkBase(String URI, String type, String role, String arcrole){this.URI_ = URI; this.type =type; this.role = role; this.arcrole=arcrole; identifyRoleType();}

	public Map<String, String> getNamespaceMapping() {
		return namespaceMapping;
	}
	public void setNamespaceMapping(Map<String, String> namespaceMapping) {
		this.namespaceMapping = namespaceMapping;
	}
	public Map<String, RoleRefInfo> getRoleRefMapping() {
		return roleRefMapping;
	}
	public void setRoleRefMapping(Map<String, RoleRefInfo> roleRefMapping) {
		this.roleRefMapping = roleRefMapping;
	}
	public Map<String, Locator> getLocatorMapping() {
		return locatorMapping;
	}
	public void setLocatorMapping(Map<String, Locator> locatorMapping) {
		this.locatorMapping = locatorMapping;
	}
	public Map<String, Resource> getResourceMapping() {
		return resourceMapping;
	}
	public void setResourceMapping(Map<String, Resource> resourceMapping) {
		this.resourceMapping = resourceMapping;
	}
	public Map<String, Arc> getArcMapping() {
		return arcMapping;
	}
	public void setArcMapping(Map<String, Arc> arcMapping) {
		this.arcMapping = arcMapping;
	}

	@Override
	public String getValue() {
		return URI_;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getArcrole() {
		return arcrole;
	}
	public void setArcrole(String arcrole) {
		this.arcrole = arcrole;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public void identifyRoleType(){
		if (this.role.equals(presentationLinkBaseRef)){
			this.roleType = "PRESENTATION_LINKBASE";
		}else if(this.role.equals(definitionLinkBaseRef)){
			this.roleType = "DEFINITION_LINKBASE";
		}else if(this.role.equals(labelLinkBaseRef)){
			this.roleType = "LABEL_LINKBASE";
		}else if(this.role.equals(calculationLinkBaseRef)){
			this.roleType = "CALCULATION_LINKBASE";
		}else if(this.role.equals(referenceLinkBaseRef)){
			this.roleType = "REFERENCE_LINKBASE";
		}
	}

	@Override
	public String toString() {
		return "DiscoverableLinkBase [arcrole=" + arcrole + ", role=" + role
				+ ", type=" + type + ", URI_=" + URI_ + "]";
	}
	@Override
	public ArrayList<DiscoverableTaxonomySet> getChildren() {
		return null;
	}

	/**
	 * 子を探すクラス。子あるならtrue, いなければfalse。
	 */
	@Override
	public Boolean hasChild(){return null;}

}
