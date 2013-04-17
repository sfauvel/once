package fr.sf.once.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Before;
import org.junit.Test;

import fr.sf.once.model.Code;
import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Redondance;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;


public class ReportingImplBetaTest {
    ReportingImplBeta report;
    StringWriter writer;
    
    @Before 
    public void setUp() {
        List<MethodLocalisation> methodList = Collections.emptyList();
        report = new ReportingImplBeta(methodList);
        
        Logger tokenLogger = report.getTokenLogger();
        writer = new StringWriter();
        WriterAppender writerAppender = new WriterAppender(new PatternLayout("%m;"), writer);
        tokenLogger.addAppender(writerAppender);
    }
    @Test
    public void testDisplayToken() {
        Token token = createTokenWithFormatReturn("mon token");
        report.display(token);
        assertEquals("mon token;", writer.toString());
    }
     
    @Test
    public void testDisplayTokenList() {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        tokenList.add(createTokenWithFormatReturn("token A"));
        tokenList.add(createTokenWithFormatReturn("token B"));
        
        report.display(tokenList);
        assertEquals("token A;token B;", writer.toString());
    }
   
    @Test
    public void testDisplayCode() {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        tokenList.add(createTokenWithFormatReturn("token A"));
        tokenList.add(createTokenWithFormatReturn("token B"));
        
        report.display(new Code(tokenList));
        assertEquals("token A;token B;", writer.toString());
    }
    
    @Test
    public void testDisplayDuplicationBetweenTwoMethod() {
        MethodLocalisation methodA = new MethodLocalisation("A", new IntRange(0, 6));
        MethodLocalisation methodB = new MethodLocalisation("B", new IntRange(10, 20));
        
        List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        methodList.add(methodA);
        methodList.add(methodB);
        ReportingImplBeta report = new ReportingImplBeta(methodList);
        
        Redondance redondance = new Redondance(2);
        redondance.getFirstTokenList().addAll(Arrays.asList(2, 10));
        
        methodA.getRedondanceList().add(redondance);
        methodB.getRedondanceList().add(redondance);
        
        List<IntRange> duplicationZone = new ArrayList<IntRange>(report.getDuplicationWithMethod(methodA).get(methodB));
        // La méthode A est redondante entre 2 et 3 avec la méthode B.
        assertEquals(new IntRange(2, 3), duplicationZone.get(0));
    }
    
    @Test
    public void testDisplayDuplicationBetweenTwoMethodWithSeveralRedondantZone() {
        MethodLocalisation methodA = new MethodLocalisation("A", new IntRange(0, 20));
        MethodLocalisation methodB = new MethodLocalisation("B", new IntRange(30, 50));
        
        List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        methodList.add(methodA);
        methodList.add(methodB);
        ReportingImplBeta report = new ReportingImplBeta(methodList);
        
        Redondance redondance1 = new Redondance(3);
        redondance1.getFirstTokenList().addAll(Arrays.asList(2, 35));
        
        Redondance redondance2 = new Redondance(5);
        redondance2.getFirstTokenList().addAll(Arrays.asList(12, 40));
        
        methodA.getRedondanceList().add(redondance1);
        methodA.getRedondanceList().add(redondance2);
        methodB.getRedondanceList().add(redondance1);
        methodB.getRedondanceList().add(redondance2);
        
        List<IntRange> duplicationZone = new ArrayList<IntRange>(report.getDuplicationWithMethod(methodA).get(methodB));
        Collections.sort(duplicationZone, new RangeOrder());
        assertEquals(new IntRange(2, 4), duplicationZone.get(0));
        assertEquals(new IntRange(12, 16), duplicationZone.get(1));
        
    }
 
    @Test
    public void testDisplayDuplicationBetweenSMethod() {
        MethodLocalisation methodA = new MethodLocalisation("A", new IntRange(0, 9));
        MethodLocalisation methodB = new MethodLocalisation("B", new IntRange(10, 20));
        MethodLocalisation methodC = new MethodLocalisation("C", new IntRange(40, 60));
        
        List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        methodList.add(methodA);
        methodList.add(methodB);
        methodList.add(methodC);
        ReportingImplBeta report = new ReportingImplBeta(methodList);
                
        Redondance redondance = new Redondance(2);
        redondance.getFirstTokenList().addAll(Arrays.asList(2, 10, 45));
        
        methodA.getRedondanceList().add(redondance);
        methodB.getRedondanceList().add(redondance);
        
        Map<MethodLocalisation, Set<IntRange>> duplicationWithMethod = report.getDuplicationWithMethod(methodA);
        assertEquals(new IntRange(2, 3), duplicationWithMethod.get(methodB).iterator().next());
        assertEquals(new IntRange(2, 3), duplicationWithMethod.get(methodC).iterator().next());
    }
 
    
    /**
     *
     */
    @Test
    public void testFindDuplicatedRange() {

        ArrayList<Redondance> redundancyList = new ArrayList<Redondance>();
        Redondance redondance = new Redondance(2);
        redondance.getFirstTokenList().addAll(Arrays.asList(5, 15));
        redundancyList.add(redondance);
        
        MethodLocalisation methodA = new MethodLocalisation("A", new IntRange(3, 7));
        MethodLocalisation methodB = new MethodLocalisation("B", new IntRange(8, 12));
        
        assertEquals(new IntRange(5, 6), report.findDuplicatedRange(methodA, redondance));
        assertNull(report.findDuplicatedRange(methodB, redondance));

    }
    /**
     * Retourne un token dont la méthode format retourne la chaîne en paramètre.
     * @param format
     * @return
     */
    private Token createTokenWithFormatReturn(final String format) {
        Token token = new Token(null, "maVariable", Type.VALEUR) {
            @Override
            public String format() {
                return format;
            }
        };
        return token;
    }
    
    /**
     *
     */
    @Test
    public void testMaximumDuplicationOneRange() {
        Set<IntRange> listRange = new HashSet<IntRange>();
        listRange.add(new IntRange(4, 10));
        List<IntRange> resultList = report.sortRangeByMinimum(listRange);
        assertEquals(new IntRange(4, 10), resultList.get(0));
    }
    
    @Test
    public void testMaximumDuplicationTwoRangeWithoutOverlap() {
        Set<IntRange> listRange = new HashSet<IntRange>();
        listRange.add(new IntRange(4, 10));
        listRange.add(new IntRange(34, 56));
        List<IntRange> resultList = report.sortRangeByMinimum(listRange);
        assertEquals(new IntRange(4, 10), resultList.get(0));
        assertEquals(new IntRange(34, 56), resultList.get(1));
        assertEquals(2, resultList.size());
    }
    
    @Test
    public void testMaximumDuplicationTwoRangeWithOverlap() {
        Set<IntRange> listRange = new HashSet<IntRange>();
        listRange.add(new IntRange(10, 30));
        listRange.add(new IntRange(20, 32));
        List<IntRange> resultList = report.sortRangeByMinimum(listRange);
        assertEquals(new IntRange(10, 30), resultList.get(0));
        assertEquals(1, resultList.size());
    }

    
    @Test
    public void testMaximumDuplicationThreeRangeWithOneOverlap() {
        Set<IntRange> listRange = new HashSet<IntRange>();
        listRange.add(new IntRange(10, 30));
        listRange.add(new IntRange(20, 36));
        listRange.add(new IntRange(32, 40));
        List<IntRange> resultList = report.sortRangeByMinimum(listRange);
        assertEquals(new IntRange(10, 30), resultList.get(0));
        assertEquals(new IntRange(32, 40), resultList.get(1));
        assertEquals(2, resultList.size());
    }
}
 