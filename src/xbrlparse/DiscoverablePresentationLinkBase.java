/**
 *
 */
package xbrlparse;

/**
 * @author suzuken
 *
 */
public class DiscoverablePresentationLinkBase extends DiscoverableLinkBase {

	/**
	 * @param URI
	 */
	public DiscoverablePresentationLinkBase(String URI) {
		super(URI);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * @param URI
	 * @param type
	 * @param role
	 * @param arcrole
	 */
	public DiscoverablePresentationLinkBase(String URI, String type,
			String role, String arcrole) {
		super(URI, type, role, arcrole);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public String toString() {
		return "DiscoverablePresentationLinkBase [URI_=" + URI_
				+ ", toString()=" + super.toString() + "]";
	}

}
