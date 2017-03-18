package fr.sf.once.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Level;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.javaparser.ast.stmt.ForeachStmt;

import fr.sf.once.model.CodeAsATokenList;
import fr.sf.once.model.Location;
import fr.sf.once.model.MethodLocation;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import fr.sf.once.test.StringWriterLogRule;
import fr.sf.once.test.UtilsToken;

public class ReportingImplTest {

    private CodeAsATokenList code;
    Reporting reporting = new ReportingImpl();

    @Rule
    public StringWriterLogRule logRule = new StringWriterLogRule(Reporting.LOG_RESULT, Level.INFO);

    @Before
    public void initCode() {
        code = UtilsToken.createCode(
                "A B C D E F G H I J",
                "K L M N O P Q R S T",
                "U V W X Y Z");
    }

    @Test
    public void should_display_token_number_and_2_duplication_number() throws Exception {
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(2, 8));
        reporting.displayRedundantCode(code, redundancy);

        Assertions.assertThat(logRule.getLog())
                .contains("Tokens number:3")
                .contains("Duplications number:2");
    }

    @Test
    public void should_display_token_number_and_4_duplication_number() throws Exception {
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(2, 5, 8, 10));
        reporting.displayRedundantCode(code, redundancy);

        Assertions.assertThat(logRule.getLog())
                .contains("Tokens number:3")
                .contains("Duplications number:4");
    }

    @Test
    public void should_show_subsitutions() throws Exception {
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(2, 6, 10));
        reporting.displayRedundantCode(code, redundancy);

        Assertions.assertThat(logRule.getLog())
                .contains("3 values: C, G, K")
                .contains("3 values: D, H, L")
                .contains("3 values: E, I, M");
    }

    @Test
    public void should_show_method() throws Exception {
        CodeAsATokenList code =  new CodeAsATokenList(
                UtilsToken.createTokenList(
                    "A B C D E F G H I J",
                    "K L M N O P Q R S T",
                    "U V W X Y Z"), 
                Arrays.asList(
                    UtilsToken.createMethod("methodA", 1, 51, new IntRange(0, 50))));

        Redundancy redundancy = new Redundancy(code, 2, Arrays.asList(2, 10));
        reporting.displayRedundantCode(code, redundancy);
        
        Assertions.assertThat(logRule.getLog())
                .contains("2% (1 of 50 lines)methodA from line 2 to 4 (method from line 1 to 51)")
                .contains("2% (1 of 50 lines)methodA from line 10 to 12 (method from line 1 to 51)");
    }

    @Test
    public void should_show_redundancy_greater_than_5() throws Exception {
        Redundancy redundancy = new Redundancy(code, 6, Arrays.asList(2, 6, 10));
        reporting.displayRedundancy(code, 5, Arrays.asList(redundancy));

        Assertions.assertThat(logRule.getLog())
                .contains("Tokens number:6");
    }

}
