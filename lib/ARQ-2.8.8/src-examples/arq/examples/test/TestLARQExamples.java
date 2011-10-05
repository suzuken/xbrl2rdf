/*
 * (c) Copyright 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package arq.examples.test;

import java.io.ByteArrayOutputStream ;
import java.io.PrintStream ;

import junit.framework.JUnit4TestAdapter ;
import org.junit.Test ;
import arq.examples.larq.ExLucene1 ;
import arq.examples.larq.ExLucene2 ;
import arq.examples.larq.ExLucene3 ;
import arq.examples.larq.ExLucene4 ;
import arq.examples.larq.ExLucene5 ;

public class TestLARQExamples
{
    // Check the LARQ examples at least run without problems.
    
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(TestLARQExamples.class) ;
    }

    
    @Test public void larq_example_1() throws Exception
    { 
        PrintStream pOut = System.out ;
        PrintStream pNull = new PrintStream(new ByteArrayOutputStream()) ;
        System.setOut(pNull) ;
        try {
            ExLucene1.main(null) ;
        } finally { System.setOut(pOut) ; }
    }

    @Test public void larq_example_2() throws Exception
    {
        PrintStream pOut = System.out ;
        PrintStream pNull = new PrintStream(new ByteArrayOutputStream()) ;
        System.setOut(pNull) ;
        try {
            ExLucene2.main(null) ;
        } finally { System.setOut(pOut) ; }
    }

    @Test public void larq_example_3() throws Exception
    {
        PrintStream pOut = System.out ;
        PrintStream pNull = new PrintStream(new ByteArrayOutputStream()) ;
        System.setOut(pNull) ;
        try {
            ExLucene3.main(null) ;
        } finally { System.setOut(pOut) ; }
    }

    @Test public void larq_example_4() throws Exception
    {
        PrintStream pOut = System.out ;
        PrintStream pNull = new PrintStream(new ByteArrayOutputStream()) ;
        System.setOut(pNull) ;
        try {
            ExLucene4.main(null) ;
        } finally { System.setOut(pOut) ; }
    }
    @Test public void larq_example_5() throws Exception
    {
        PrintStream pOut = System.out ;
        PrintStream pNull = new PrintStream(new ByteArrayOutputStream()) ;
        System.setOut(pNull) ;
        try {
            ExLucene5.main(null) ;
        } finally { System.setOut(pOut) ; }
    }
}

/*
 * (c) Copyright 2009 Hewlett-Packard Development Company, LP
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