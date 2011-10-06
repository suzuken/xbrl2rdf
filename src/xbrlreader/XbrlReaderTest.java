/**
 * 
 */
package xbrlreader;


import static org.junit.Assert.*;

import javax.xml.xpath.XPathExpressionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;
import org.junit.Test;

/**
 * @author suzuken
 *
 */
public class XbrlReaderTest {
	
	public static String xbrlurl;
	public static XbrlReader reader;

	public static void main(String[] args){
		JUnitCore.main(XbrlReaderTest.class.getName());
		xbrlurl = "./tests/data/xml/S0004PT1/jpfr-asr-G03727-000-2009-09-02-01-2009-12-01.xbrl";
		reader = new XbrlReader(xbrlurl);
	}
	
	@Test
	public void getSchemaRef() throws XPathExpressionException{
		XbrlReader xr = new XbrlReader(xbrlurl);
		XLink s = xr.getSchemaRef();
		String t = "jpfr-asr-G03727-000-2009-09-02-01-2009-12-01.xsd";
		assertEquals(s, t);
	}
	
	@Test
	public void getContext() throws XPathExpressionException{
		XbrlReader xr = new XbrlReader(xbrlurl);
		String di = "DocumentInfo";
		Context c = xr.getContext(di);
	}
	
	@Test
	public void getUnit(){
		
	}
	
	@Test
	public void getValue(){
		
	}
	
	@Test
	public void isExistElement(){
		
	}
	
	@Test
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
