/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package arq.examples.larq;


import org.openjena.atlas.lib.StrUtils ;

import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.query.larq.IndexBuilderString ;
import com.hp.hpl.jena.query.larq.IndexLARQ ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.ModelFactory ;
import com.hp.hpl.jena.sparql.util.Utils ;
import com.hp.hpl.jena.util.FileManager ;
import com.hp.hpl.jena.vocabulary.DC ;

/** Example code to load a model from a file, index string literals on the DC title property, 
 * then execute a SPARQL query with a Lucene search in it. */

public class ExLucene3
{
    
    public static void main(String[] a) throws Exception
    {
        System.out.println("ARQ Example: "+Utils.classShortName(ExLucene3.class)) ;
        System.out.println("ARQ: "+ARQ.VERSION) ;
        System.out.println() ;
        
        Model model = ModelFactory.createDefaultModel() ;
        IndexLARQ index = buildTitleIndex(model,  "testing/LARQ/data-1.ttl") ;
        
        // Search for string 
        String searchString = "+document" ;
        
        // This time, find documents with a matching DC title. 
        String queryString = StrUtils.strjoin("\n", 
            "PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>" ,
            "PREFIX :       <http://example/>" ,
            "PREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>",
            "PREFIX  dc:    <http://purl.org/dc/elements/1.1/>",
            "SELECT ?title {" ,
            "    ?title pf:textMatch '"+searchString+"'.",
            "}") ;
        
        // Two of three docuemnts should match. 
        ExLucene1.performQuery(model, index, queryString) ;
        index.close() ;
    }
    
    static IndexLARQ buildTitleIndex(Model model, String datafile)
    {
        // ---- Read and index just the title strings.
        IndexBuilderString larqBuilder = new IndexBuilderString(DC.title) ;
        
        // Index statements as they are added to the model.
        model.register(larqBuilder) ;
        
        // To just build the index, create a model that does not store statements 
        // Model model2 = ModelFactory.createModelForGraph(new GraphSink()) ;
        
        FileManager.get().readModel(model, datafile) ;
        
        // ---- Alternatively build the index after the model has been created. 
        // larqBuilder.indexStatements(model.listStatements()) ;
        
        // ---- Finish indexing
        larqBuilder.closeWriter() ;
        model.unregister(larqBuilder) ;
        
        // ---- Create the access index  
        IndexLARQ index = larqBuilder.getIndex() ;
        return index ; 
    }

}

/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */