package xbrlonto;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

public class DatabaseConnectionTest {
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String URL = "jdbc:mysql://localhost/mydb";
			String USER = "root";
			String PASSWORD = "hoge";
			String TYPE = "MySQL";

			DBConnection conn = new DBConnection(
					URL, USER, PASSWORD, TYPE); 

			ModelMaker maker =
				ModelFactory.createModelRDBMaker(conn);

			//testモデルをつくる
			//Model model = maker.createModel("test");//---4---
			//testモデルがある場合、開く。
			//Model model = maker.openModel("test", true);//---4---
/*
			model.add(model.createResource("http://tech.4dd.co.jp/")
					, DC.creator, "四次元データ");//---5---
			model.write(System.out, "RDF/XML");
*/

			ModelRDB rdbmodel=(ModelRDB)maker.openModel("test");

			rdbmodel.remove();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}