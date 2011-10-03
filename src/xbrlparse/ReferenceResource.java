package xbrlparse;

public class ReferenceResource extends Resource {

	private String publisher;
	private String name;
	private String number;
	private String issueDate;
	private String appendix;
	private String chapter;
	private String article;
	private String paragraph;
	private String subparagraph;

	public ReferenceResource(String label, String role, String type) {
		super(label, role, type);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ReferenceResource(String label, String role, String type,
			String publisher, String name, String number, String issueDate,
			String appendix, String chapter, String article, String paragraph,
			String subparagraph) {
		super(label, role, type);
		this.publisher = publisher;
		this.name = name;
		this.number = number;
		this.issueDate = issueDate;
		this.appendix = appendix;
		this.chapter = chapter;
		this.article = article;
		this.paragraph = paragraph;
		this.subparagraph = subparagraph;
	}

	@Override
	public String toString() {
		return "ReferenceResource [appendix=" + appendix + ", article="
				+ article + ", chapter=" + chapter + ", issueDate=" + issueDate
				+ ", name=" + name + ", number=" + number + ", paragraph="
				+ paragraph + ", publisher=" + publisher + ", subparagraph="
				+ subparagraph + ", toString()=" + super.toString() + "]";
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getAppendix() {
		return appendix;
	}

	public void setAppendix(String appendix) {
		this.appendix = appendix;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public String getParagraph() {
		return paragraph;
	}

	public void setParagraph(String paragraph) {
		this.paragraph = paragraph;
	}

	public String getSubparagraph() {
		return subparagraph;
	}

	public void setSubparagraph(String subparagraph) {
		this.subparagraph = subparagraph;
	}



}
