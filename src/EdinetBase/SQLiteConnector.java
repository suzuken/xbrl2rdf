package EdinetBase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


public class SQLiteConnector {

	private Map<Integer, EdinetData> resultmapping;


	public Map<Integer, EdinetData> getResultmapping() {
		return resultmapping;
	}


	public void setResultmapping(Map<Integer, EdinetData> resultmapping) {
		this.resultmapping = resultmapping;
	}

	/*
	 * for test
	 */
	public static void main(String[] args) throws ClassNotFoundException{
		SQLiteConnector sc = new SQLiteConnector();
		sc.start(args);
	}

	/**
	 * constructor
	 */
	public SQLiteConnector(){};


	/**
	 * @param args
	 */
	public void start(String[] args) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		this.resultmapping = new HashMap<Integer, EdinetData>();

		Connection connection = null;
		try
	    {
	      // create a database connection
	      connection = DriverManager.getConnection("jdbc:sqlite:C:\\Program Files\\TeCAPro_1_0_5\\TeCAPro\\copyTeCAPro.db");
	      Statement statement = connection.createStatement();
	      statement.setQueryTimeout(30);  // set timeout to 30 sec.

	      //TODO 動的クエリ
	      //とりあえずトヨタ
	      ResultSet rs = statement.executeQuery("select * from EDINETDATA WHERE EDINETCODE='E02144'");
	      while(rs.next())
	      {
	    	  int id;
	    	  String pdfurl;
	    	  String xbrlurl;
	    	  String edinetcode;
	    	  String name;
	    	  String name2;
	    	  String date;
	    	  String time;
	    	  String doctypecode;
	    	  String doctypename;
	    	  String docid;
	    	  String read;
	    	  String tag;
	    	  String note;
	    	  String del;
	    	  String createDatetime;

	    	  id=rs.getInt("id");
	    	  pdfurl=rs.getString("pdfurl");
	    	  /*
	    	   * xbrlurlは2つ以上ある場合がある。
	    	   * "|"で区切られているので、新しい方（後ろ）を取得。
	    	   */
	    	  xbrlurl=rs.getString("xbrlurl");
	    	  if(xbrlurl.contains("|")){
	    		  String s[]=xbrlurl.split("|");
	    		  Integer i = s.length;
	    		  xbrlurl=s[i];	//一番新しいもの
	    	  }

	    	  edinetcode=rs.getString("edinetcode");
	    	  name=rs.getString("name");
	    	  name2=rs.getString("name2");
	    	  date=rs.getString("date");
	    	  time=rs.getString("time");
	    	  doctypecode=rs.getString("doctypecode");
	    	  doctypename=rs.getString("doctypename");
	    	  docid=rs.getString("docid");
	    	  read=rs.getString("read");
	    	  tag=rs.getString("tag");
	    	  note=rs.getString("note");
	    	  del=rs.getString("del");
	    	  createDatetime=rs.getString("create_datetime");

	    	  EdinetData ed = new EdinetData(id, pdfurl, xbrlurl, edinetcode, name, name2, date, time, doctypecode, doctypename, docid, read, tag, note, del, createDatetime);

	    	  System.out.println(ed);
	    	  this.resultmapping.put(id, ed);
	      }
	    }
	    catch(SQLException e)
	    {
	      // if the error message is "out of memory",
	      // it probably means no database file is found
	      System.err.println(e.getMessage());
	    }
	    finally
	    {
	      try
	      {
	        if(connection != null)
	          connection.close();
	      }
	      catch(SQLException e)
	      {
	        // connection close failed.
	        System.err.println(e);
	      }
	    }
	}


}
