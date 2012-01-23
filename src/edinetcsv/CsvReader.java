package edinetcsv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringTokenizer;

import namespace.XBRLOWL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class CsvReader {
	
	public Model model;
	private String outputRDFPath;
	private Resource companyType;


	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			System.out.println("処理開始しました。");

			// File csv = new File("EdinetCdDlInfo.csv"); // CSVデータファイル
			CsvReader c = new CsvReader();
			c.model = TDBFactory.createModel();
			c.model.setNsPrefix("xbrlowl", XBRLOWL.getURI());
			c.model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
			
			c.setOutputRDFPath("EdinetCdDlInfo-2012-01-23.rdf");
			
			c.companyType = c.model.createResource(XBRLOWL.company);



			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					"EdinetCdDlInfo.csv"), "Shift-JIS");
			BufferedReader br = new BufferedReader(isr);
			// 最終行まで読み込む
			String line = "";
			while ((line = br.readLine()) != null) {

				// 1行をデータの要素に分割
				//StringTokenizer st = new StringTokenizer(line, ",");
				String[] arr = line.split(",");
				// 0: EdinetCode, 1: 会社名, 2: 住所
				// xbrlowl:会社名->edinetproperty->edinetdcode
				// xbrlowl:会社名->locatedIn->住所
				if(arr.length == 3){
					c.createModel(arr[0], arr[1], arr[2]);
				}
			}
			br.close();
			
			//RDFファイル出力
			c.outputRDF(c.model);
			long stop = System.currentTimeMillis();

			System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");


		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedReaderオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}
	}
	//モデルを作成
	public void createModel(String edinetCode, String companyName, String address){
		
		//前処理
		companyName = companyName.replaceAll("\\[", "［");
		companyName = companyName.replaceAll("\\]", "］");

		//会社リソース
		Resource company = this.model.createResource(XBRLOWL.getURI()
				+ companyName);
		company.addProperty(RDFS.label, companyName);

		//会社プロパティ付け
		company.addProperty(RDF.type, this.companyType);
		
		company.addProperty(XBRLOWL.locatedIn, address);
		company.addProperty(XBRLOWL.hasEDINETCode, edinetCode);
	}
	
	public void outputRDF(Model model) {
		OutputStream out = null;

		File outFile = new File(this.getOutputRDFPath());
		try {
			outFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("出力完了しました。");
		model.write(out);
	}
	public String getOutputRDFPath() {
		return outputRDFPath;
	}

	
	public void setOutputRDFPath(String outputRDFPath) {
		this.outputRDFPath = outputRDFPath;
	}

}
