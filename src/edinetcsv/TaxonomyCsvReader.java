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

import rdfmaker.RDFMakerV2;

import util.FileSearch;

import namespace.XBRLOWL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class TaxonomyCsvReader {

	public Model model;
	private String outputRDFPath;
	private Resource companyType;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("処理開始しました。");
	
		// File csv = new File("EdinetCdDlInfo.csv"); // CSVデータファイル
		// model生成
	
		FileSearch search = new FileSearch();
	
		File[] files = search.listFiles(
				"/home/suzuken/Documents/taxonomy/2011", ".*.csv", 2, false, 0);
		// TODO アスタリスクじゃない場合も考える。絶対パスの場合はどうなるか検証する。
		for (Integer i = 0, ii = files.length; i < ii; i++) {
			TaxonomyCsvReader r = new TaxonomyCsvReader(
					files[i].getAbsolutePath());
			r.doMain(files[i].getAbsolutePath());
		}
	
		long stop = System.currentTimeMillis();
	
		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
	
	}

	public void doMain(String path) {
		try {
			
			System.out.println(path);

			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					path), "UTF-8");

			BufferedReader br = new BufferedReader(isr);
			// 最終行まで読み込む
			String line = "";
			Integer skip = 0;
			while ((line = br.readLine()) != null) {
				skip++;
				// 一行目はスルーして２行目から
				if (skip == 1) {
					continue;
				}

				/*
				 * 0 科目分類 1 標準ラベル（日本語） 2 冗長ラベル（日本語） 3 用途別ラベル（日本語） 4 業種ラベル（日本語） 5
				 * 連結 6 四半期個別 7 四半期連結 8 中間個別 9 中間連結 10 標準ラベル（英語） 11 用途別ラベル（英語）
				 * 12 業種ラベル（英語） 13 名前空間プレフィックス 14 要素名 15 優先ラベル 16 type 17
				 * enumerations 18 substitutionGroup 19 periodType 20 balance 21
				 * abstract 22 nillable 23 depth
				 */

				// 1行をデータの要素に分割
				String[] a = line.split(",");

				if (a.length == 24) {
					this.createModel(a[1], a[10], a[13], a[14], a[16], a[19],
							a[20], Boolean.valueOf(a[21]),
							Boolean.valueOf(a[22]), Integer.valueOf(a[23]));
				}
			}
			br.close();

			// RDFファイル出力
			this.outputRDF(this.model);

		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedReaderオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}

	}

	// コンストラクタ
	public TaxonomyCsvReader(String path) {
		super();
		this.model = TDBFactory.createModel();
		this.model.setNsPrefix("xbrlowl", XBRLOWL.getURI());
		this.setOutputRDFPath("/home/suzuken/workspace/xbrl2rdf/output/taxonomy/2011" + getFileName(path) + ".rdf");
	}

	// モデルを作成
	// 日本語名称、英語名称、名前空間prefix、要素名、type、periodType(instant,duration)、
	// balance(debit,credit等)、abstract(boolean)、nillable(boolean)、depth(int)
	public void createModel(String labelJa, String labelEn, String prefix,
			String name, String type, String periodType, String balance,
			Boolean abstractBoolean, Boolean nillable, Integer depth) {

		// 前処理 必要であれば行う。(RDFは半角括弧で崩れる。)
		// companyName = companyName.replaceAll("\\[", "［");
		// companyName = companyName.replaceAll("\\]", "］");

		// 勘定科目リソース
		Resource heading = this.model.createResource(XBRLOWL.getURI() + name);
		heading.addProperty(XBRLOWL.abstractBoolean,
				String.valueOf(abstractBoolean));
		heading.addProperty(XBRLOWL.nillable, String.valueOf(nillable));
		heading.addProperty(XBRLOWL.type, type);
		heading.addProperty(XBRLOWL.labelJa, labelJa);
		heading.addProperty(XBRLOWL.labelEn, labelEn);
		heading.addProperty(RDFS.label, labelJa);
		heading.addProperty(XBRLOWL.balance, balance);
		heading.addProperty(XBRLOWL.prefix, prefix);
		heading.addProperty(XBRLOWL.depth, String.valueOf(depth));
		heading.addProperty(XBRLOWL.periodType, periodType);
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

	public static String getFileName(String filename){
		if(filename == null){
			return null;
		}
		int point = filename.lastIndexOf(".");
		int slash = filename.lastIndexOf("/");
		if(point != -1){
			return filename.substring(slash, point);
		}
		return filename;
	}

	public String getOutputRDFPath() {
		return outputRDFPath;
	}

	public void setOutputRDFPath(String outputRDFPath) {
		this.outputRDFPath = outputRDFPath;
	}

}
