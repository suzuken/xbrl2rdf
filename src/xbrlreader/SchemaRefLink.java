package xbrlreader;

//schemarefのためのクラス
public class SchemaRefLink implements XLink {
	
	public String href;
	public String type;

	public SchemaRefLink(String href, String type) {
		super();
		this.href = href;
		this.type = type;
	}

	@Override
	public String getHref() {
		return this.href;
	}

	@Override
	public String getType() {
		return this.type;
	}
	@Override
	public String toString() {
		return "SchemaRefLink [href=" + href + ", type=" + type + "]";
	}
}
