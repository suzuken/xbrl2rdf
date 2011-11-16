package namespace;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class XBRLOWL {
	private static Model m_model = ModelFactory.createDefaultModel();
	public static final String NS = "http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/owl#";
	public static final Resource NAMESPACE = m_model.createResource(NS);
	
	public static String getURI(){
		return NS;
	}
	
	//各種プロパティをstaticで記述
	public static final Property company = 
			m_model.createProperty(NS + "company");
	public static final Property hasEDINETCode = 
			m_model.createProperty(NS + "hasEDINETCode");
	public static final Property hasReport = 
			m_model.createProperty(NS + "hasReport");
	public static final Property hasCompany = 
			m_model.createProperty(NS + "hasCompany");
	public static final Resource report = 
			m_model.createResource(NS + "report");
	public static final Property publishDate = 
			m_model.createProperty(NS + "publishDate");
	public static final Property hasContext = 
			m_model.createProperty(NS + "hasContext");
	public static final Property scenario = 
			m_model.createProperty(NS + "scenario");
	public static final Property hasInstant = 
			m_model.createProperty(NS + "hasInstant");
	public static final Property startDate = 
			m_model.createProperty(NS + "startDate");
	public static final Property endDate = 
			m_model.createProperty(NS + "endDate");
	public static final Property hasItem = 
			m_model.createProperty(NS + "hasItem");
	public static final Resource item = 
			m_model.createResource(NS + "item");
	public static final Property context = 
			m_model.createProperty(NS + "context");
	public static final Property decimal = 
			m_model.createProperty(NS + "decimal");
	public static final Property unit = 
			m_model.createProperty(NS + "unit");
}
