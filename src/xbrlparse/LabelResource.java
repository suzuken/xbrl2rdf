package xbrlparse;

public class LabelResource extends Resource{

	private String lang;
	private String id;
	private String labelText;

	@Override
	public String toString() {
		return "LabelResource [id=" + id + ", labelText=" + labelText
				+ ", lang=" + lang + ", toString()=" + super.toString() + "]";
	}

	public LabelResource(String label, String role, String type) {
		super(label, role, type);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public LabelResource(String label, String role, String type, String lang,
			String id, String labelText) {
		super(label, role, type);
		this.lang = lang;
		this.id = id;
		this.labelText = labelText;
	}


	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

}
