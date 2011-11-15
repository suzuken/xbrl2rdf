package rdfmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import util.FileSearch;

/**
 * @author suzuken
 *
 * @deprecated
 * 複数のRDFを結合させる関数
 */
public class RDFConcatenator {
	public Model[] models;
	private static boolean recursive = false;
	public String outputPath = "/home/suzuken/workspace/xbrl2rdf/output/XBRLRDF/dump/all20111115.rdf"; 
	
	//引数にディレクトリを渡し、ディレクトリ内のすべてのRDFファイルを結合し、
	//一つのRDFファイルとして出力する。
	public static void main(String args[]){
		long start = System.currentTimeMillis();
		
		FileSearch search = new FileSearch();
		
		File[] files = search.listFiles(args[0], ".*.rdf", 2, recursive, 0);
		
		RDFConcatenator r = new RDFConcatenator();
		for (Integer i = 0, ii=files.length; i<ii; i++){
			r.addModel(files[i].getAbsolutePath());
		}
		
		//書き出し
		r.outputRDF();
		
		long stop = System.currentTimeMillis();
		
		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
	}
	
	public RDFConcatenator(){
//		this.model = ModelFactory.createDefaultModel();
	}
	
	//rdfファイルを受け取り、modelを取得し、挿入する。
	public void addModel(String path){
		try{
			InputStream in = FileManager.get().open(path);
			Model m = ModelFactory.createDefaultModel();
			if(in == null){
				throw new IllegalArgumentException(
						"File: " + path + " not found");
			}
			m.read(in, null);
			models[models.length-1] = m;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void outputRDF(){
		OutputStream out= null;
		File outFile = new File(this.outputPath);
		try {
			outFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.model.write(out);
	}

}
