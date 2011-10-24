package tdb;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

//RDFを読み取り、TDBに格納
public class TDBTest {
	public static void main(String[] args){
		String directory = "/home/suzuken/workspace/xbrl2rdf/DB1";
		Dataset dataset = TDBFactory.createDataset(directory);
		Model model = dataset.getDefaultModel();
		
		//読み取るRDFはoutputにある。
		//outputにあるRDFをすべて読み込んで格納してみる
//		model.read(args[0]);
		FileManager.get().readModel(model, args[0]);
		
		model.close();
	}

}
