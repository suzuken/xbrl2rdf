package xbrlparse;

/**
 * Arcの情報を格納するクラス
 * @author suzuken
 *各リンクベースパーサごとに拡張もある。
 *提供する変数は、type,arcrole,from,to。
 */
public class Arc {
	private String type;
	private String arcrole;
	private String from;
	private String to;
	public Arc(String type, String arcrole, String from, String to) {
		super();
		this.type = type;
		this.arcrole = arcrole;
		this.from = from;
		this.to = to;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getArcrole() {
		return arcrole;
	}
	public void setArcrole(String arcrole) {
		this.arcrole = arcrole;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	@Override
	public String toString() {
		return "LabelArc [arcrole=" + arcrole + ", from=" + from + ", to=" + to
		+ ", type=" + type + "]";
	}




}
