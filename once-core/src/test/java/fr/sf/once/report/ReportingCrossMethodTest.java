package fr.sf.once.report;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Level;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.test.StringWriterLogRule;
import fr.sf.once.test.UtilsToken;

public class ReportingCrossMethodTest {

    private ReportingCrossMethod reporting = new ReportingCrossMethod();

    @Rule
    public StringWriterLogRule logRule = new StringWriterLogRule(Reporting.LOG_RESULT, Level.INFO);
    @Test
    public void testName() throws Exception {
        ArrayList<Integer> arrayList = new ArrayList<Integer>(Arrays.asList(5, 93, 45, 2, 34));
        System.out.println(arrayList);
        Collections.sort(arrayList);
        System.out.println(arrayList);
    }
    
    @Test
    public void should_merge_2_redundancies_in_2_same_methods() throws Exception {
        //                                       1234567890123456789012345678901234567890123456789
        Code code = UtilsToken.initCode("methodA:........................",
                                        "methodB:....................................");
        
        reporting.displayRedundancy(code, 1, Arrays.asList(
                new Redundancy(code, 6, Arrays.asList(2, 28)),
                new Redundancy(code, 6, Arrays.asList(12, 38))));
        
        Assertions.assertThat(logRule.getLog())
                .contains("Tokens number:12 Parts number:2")
                .contains("50% (12 of 24 tokens) methodA")
                .contains("[2-8], [12-18]")
                .contains("33% (12 of 36 tokens) methodB")
                .contains("[28-34], [38-44]")
                ;
    }

    @Test
    public void should_not_merge_2_redundancies_in_2_same_methods_when_overlap() throws Exception {
        //                                       1234567890123456789012345678901234567890123456789
        Code code = UtilsToken.initCode("methodA:........................",
                                        "methodB:....................................");
        
        reporting.displayRedundancy(code, 1, Arrays.asList(
                new Redundancy(code, 6, Arrays.asList(10, 32)),
                new Redundancy(code, 6, Arrays.asList(12, 38))));
        
        Assertions.assertThat(logRule.getLog())
                .doesNotContain("Token number:12 Parts number:2")
                ;
    }
    
    @Test
    public void should_merge_only_range_that_not_overlap() throws Exception {
        //                                       1234567890123456789012345678901234567890123456789
        Code code = UtilsToken.initCode("methodA:........................",
                                        "methodB:....................................",
                                        "methodC:....................................");
        
        reporting.displayRedundancy(code, 1, Arrays.asList(
                new Redundancy(code, 6, Arrays.asList(2, 28)),
                new Redundancy(code, 6, Arrays.asList(12, 38)),
                new Redundancy(code, 6, Arrays.asList(14, 42))));
        
        Assertions.assertThat(logRule.getLog())
                .contains("Tokens number:12 Parts number:2")
                .contains("50% (12 of 24 tokens) methodA")
                .contains("33% (12 of 36 tokens) methodB")
                .doesNotContain("methodC")
                ;
    }
    
    @Test
    public void should_display_redundancies_in_order() throws Exception {
        //                                       1234567890123456789012345678901234567890123456789
        Code code = UtilsToken.initCode("methodA:........................",
                                        "methodB:....................................",
                                        "methodC:....................................",
                                        "methodD:..............................");
        
        reporting.displayRedundancy(code, 1, Arrays.asList(
                new Redundancy(code, 6, Arrays.asList(2, 28)),
                new Redundancy(code, 6, Arrays.asList(12, 38)),
                new Redundancy(code, 7, Arrays.asList(62, 100)),
                new Redundancy(code, 8, Arrays.asList(72, 110))));
        
        Assertions.assertThat(logRule.getLog())
                .containsSequence(
                        "Tokens number:15 Parts number:2",
                        "Tokens number:12 Parts number:2")
        ;
    }
    
    @Test
    public void should_remove_overlap_when_redundancy_do_not_have_same_token_number() throws Exception {
        //                            1234567890123456789012345678901234567890123456789
        Code code = UtilsToken.initCode("methodA:........................................",
                                        "methodB:........................................");
        
        reporting.displayRedundancy(code, 1, Arrays.asList(
                new Redundancy(code, 10, Arrays.asList(10, 50)),
                new Redundancy(code, 4, Arrays.asList(17, 70))));
        
        Assertions.assertThat(logRule.getLog())
                .doesNotContain("Parts number:2")
                ;
    }
    
   
}
