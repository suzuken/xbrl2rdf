package linkbaseparser;

public class ReferenceLink {

	private String publisher;
	private String name;
	private String issuedate;
	private String appendix;
	
	private String chapter;
	private String article;
	private String paragraph;
	private String subparagraph;
	public ReferenceLink(String publisher, String name, String issuedate,
			String appendix, String chapter, String article, String paragraph,
			String subparagraph) {
		super();
		this.publisher = publisher;
		this.name = name;
		this.issuedate = issuedate;
		this.appendix = appendix;
		this.chapter = chapter;
		this.article = article;
		this.paragraph = paragraph;
		this.subparagraph = subparagraph;
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
	public String getIssuedate() {
		return issuedate;
	}
	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
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
