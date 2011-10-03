package xbrlparse;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestStart {

	public static void main(String args[]) throws MalformedURLException, IOException{
		DefinitionLinkParser lp = new DefinitionLinkParser(args[0]);
		lp.startProcess();
	}
}
