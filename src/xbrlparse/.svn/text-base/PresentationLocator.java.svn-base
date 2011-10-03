/**
 *
 */
package xbrlparse;

import java.util.ArrayList;

/**表示リンク用のロケータ。子ロケータをもつことができる。
 * @author suzuken
 *表示リンク用のロケータは、子ロケータを持ちます。
 *getChildrenで、ArrayList<PresentationLocator>を返します。
 *hrefはLocator型で解決されており、uriとfragmentにわかれています。多くの場合、uriはschemaの場所を示し、fragmentは該当するschemaのelement要素になっています。
 */
public class PresentationLocator extends Locator {

	private ArrayList<PresentationLocator> children = new ArrayList<PresentationLocator>();


	public PresentationLocator(String label, String address, String type,
			String uri, String fragment) {
		super(label, address, type, uri, fragment);
	}

	public void add(PresentationLocator p){
		children.add(p);
	}
	public ArrayList<PresentationLocator> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PresentationLocator> children) {
		this.children = children;
	}

}
