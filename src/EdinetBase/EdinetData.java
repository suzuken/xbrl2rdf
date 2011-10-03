package EdinetBase;

public class EdinetData {

/*	ID ID（主キー）
	PDFURL PDFファイルの保存場所（フルパス指定）
	XBRLURL XBRLファイルの保存場所（フルパス指定）１レコードに複数のXBRLが存在する場合、縦棒『|』 で区切る
	EDINETCODE EDINETコード
	NAME 企業名
	NAME2 対象企業名等
	DATE データ開示日
	TIME データ開示時刻（未使用）
	DOCTYPECODE 文書種別コード
	DOCTYPENAME 文書名
	DOCID 書類識別番号
	READ 読み込み済みフラグ
	TAG タグ
	NOTE メモ（未使用）
	DEL 削除フラグ（キャッシュをクリアした場合にTrueが設定されます）
	CREATE_DATETIME データ作成日時
*/
	private int id;
	private String pdfurl;
	private String xbrlurl;
	private String edinetcode;
	private String name;
	private String name2;
	private String date;
	private String time;
	/**
	 * documentのタイプ
	 * 030 有価証券届出書
	 * 040 訂正有価証券届出書
	 * 120 有価証券報告書
	 * 140 四半期報告書
	 * 150 訂正四半期報告書
	 * 160 半期報告書
	 * 170 訂正半期報告書
	 */
	private String doctypecode;
	/**
	 * documentのタイプ
	 */
	private String doctype;

	private String doctypename;
	private String docid;
	private String read;
	private String tag;
	private String note;
	private String del;
	private String createDatetime;

	public EdinetData(int id, String pdfurl, String xbrlurl, String edinetcode,
			String name, String name2, String date, String time,
			String doctypecode, String doctypename, String docid, String read,
			String tag, String note, String del, String createDatetime) {
		super();
		this.id = id;
		this.pdfurl = pdfurl;
		this.xbrlurl = xbrlurl;
		this.edinetcode = edinetcode;
		this.name = name;
		this.name2 = name2;
		this.date = date;
		this.time = time;
		this.doctypecode = doctypecode;
		setDocTypeByCode(doctypecode);
		this.doctypename = doctypename;
		this.docid = docid;
		this.read = read;
		this.tag = tag;
		this.note = note;
		this.del = del;
		this.createDatetime = createDatetime;
	}

	public EdinetData() {
		super();
	}

	/**
	 * doctypecodeからdoctypeを導出
	 * @param doctypecode
	 */
	private void setDocTypeByCode(String dc){
		/**
		 * documentのタイプ
		 * 030 有価証券届出書
		 * 040 訂正有価証券届出書
		 * 120 有価証券報告書
		 * 140 四半期報告書
		 * 150 訂正四半期報告書
		 * 160 半期報告書
		 * 170 訂正半期報告書
		 */
		if(dc=="030"){
			setDoctype("有価証券報告書");
		}else if(dc=="040"){
			setDoctype("訂正有価証券報告書");
		}else if(dc=="120"){
			setDoctype("有価証券報告書");
		}else if(dc=="140"){
			setDoctype("四半期報告書");
		}else if(dc=="150"){
			setDoctype("訂正四半期報告書");
		}else if(dc=="160"){
			setDoctype("半期報告書");
		}else if(dc=="170"){
			setDoctype("訂正半期報告書");
		}else{}
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPdfurl() {
		return pdfurl;
	}
	public void setPdfurl(String pdfurl) {
		this.pdfurl = pdfurl;
	}
	public String getXbrlurl() {
		return xbrlurl;
	}
	public void setXbrlurl(String xbrlurl) {
		this.xbrlurl = xbrlurl;
	}
	public String getEdinetcode() {
		return edinetcode;
	}
	public void setEdinetcode(String edinetcode) {
		this.edinetcode = edinetcode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDoctypecode() {
		return doctypecode;
	}
	/**
	 * doctypeのセッター。同時にsetDocTypeByCodeも走る。
	 * @param doctypecode
	 */
	public void setDoctypecode(String doctypecode) {
		this.doctypecode = doctypecode;
		setDocTypeByCode(doctypecode);
	}
	public String getDoctype() {
		return doctype;
	}
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}
	public String getDoctypename() {
		return doctypename;
	}
	public void setDoctypename(String doctypename) {
		this.doctypename = doctypename;
	}
	public String getDocid() {
		return docid;
	}
	public void setDocid(String docid) {
		this.docid = docid;
	}
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	public String getCreate_datetime() {
		return createDatetime;
	}
	public void setCreate_datetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	@Override
	public String toString() {
		return "EdinetData [create_datetime=" + createDatetime + ", date="
				+ date + ", del=" + del + ", docid=" + docid + ", doctypecode="
				+ doctypecode + ", doctypename=" + doctypename
				+ ", edinetcode=" + edinetcode + ", id=" + id + ", name="
				+ name + ", name2=" + name2 + ", note=" + note + ", pdfurl="
				+ pdfurl + ", read=" + read + ", tag=" + tag + ", time=" + time
				+ ", xbrlurl=" + xbrlurl + "]";
	}

}
