package rdfmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.net.URL;

import org.openrdf.Sesame;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;

import org.apache.commons.httpclient.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import util.FileSearch;

/**
 * @author suzuken
 *
 */
public class RDFSesameInputor {

	private static boolean recursive = false;
	public String baseURI = "http://yamaguti.comp.ae.keio.ac.jp/xbrl_ontology";
	public RepositoryConnection con;
	
	//引数にディレクトリを渡し、ディレクトリ内のすべてのRDFファイルを結合し、
	//一つのRDFファイルとして出力する。
	public static void main(String args[]) throws URISyntaxException{
		long start = System.currentTimeMillis();
		
		FileSearch search = new FileSearch();
		
		File[] files = search.listFiles(args[0], ".*.rdf", 2, recursive, 0);
		
		RDFSesameInputor r = new RDFSesameInputor();
		for (Integer i = 0, ii=files.length; i<ii; i++){
			r.addModel(files[i].getAbsoluteFile());
		}
		
		long stop = System.currentTimeMillis();
		
		System.out.println("実行時間は" + (stop - start) + "ミリ秒です。");
	}
	
	public RDFSesameInputor() throws URISyntaxException{
//		this.model = ModelFactory.createDefaultModel();
//		java.net.URI sesameServerURI = 
//				new java.net.URI("http://localhost:8081/openrdf-workbench");
		String sesameServer = "http://localhost:8081/openrdf-workbench";
		String repositoryID = "XBRLLOD";
		Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
		try {
			this.con = myRepository.getConnection();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//rdfファイルを受け取り、modelを取得し、挿入する。
	public void addModel(File file){
		try{
//			InputStream in = FileManager.get().open(path);
//			if(in == null){
//				throw new IllegalArgumentException(
//						"File: " + path + " not found");
//			}
			this.con.add(file, this.baseURI, RDFFormat.RDFXML);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
