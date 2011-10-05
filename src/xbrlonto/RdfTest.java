package xbrlonto;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

public class RdfTest {

	public static void main(String args[]){
		// some definitions
        String personURI    = "http://somewhere/JohnSmith";
        String givenName    = "John";
        String familyName   = "Smith";
        String fullName     = givenName + " " + familyName;
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // create the resource
        //   and add the properties cascading style
        Resource johnSmith 
          = model.createResource(personURI)
                 .addProperty(VCARD.FN, fullName)
                 .addProperty(VCARD.N, 
                              model.createResource()
                                   .addProperty(VCARD.Given, givenName)
                                   .addProperty(VCARD.Family, familyName));
        
        // now write the model in XML form to a file
        model.write(System.out);
	
	}
}
