package fr.sf.once.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Before;
import org.junit.Test;

import fr.sf.once.comparator.ComparatorWithSubstitutionAndType;
import fr.sf.once.report.Reporting;

public class LauncherITest {
    private StringWriter writer = new StringWriter();
    private Launcher launcher = new Launcher();

    @Before
    public void setReporter() {
        Reporting.LOG_RESULT.addAppender(new WriterAppender(new PatternLayout("%m\n"), writer));
        Reporting.LOG_RESULT.setLevel(Level.INFO);
    }

    @Test
    public void make_a_full_execution() throws Exception {
        launcher.execute(new OnceConfiguration()
                .withSource("src/test/resources/exemple", "UTF-8")
                .withCodeComparatorClass(ComparatorWithSubstitutionAndType.class)
                .withMinimalTokenNumberDetection(10)
                );

        assertThat(writer.toString().split("\n")).contains(new String[] {
                "Tokens number:63 Duplications number:2 Substitutions number:0",
                "  100% (7 of 7 lines)fr.sf.once.CopieMethode.firstMethod from line 7 to 14 (method from line 7 to 14)",
                "  100% (7 of 7 lines)fr.sf.once.CopieMethode.secondMethod from line 16 to 23 (method from line 16 to 23)"});
        
    }
    
    @Test
    public void should_detect_separate_duplication() throws Exception {
        
        launcher.execute(new OnceConfiguration()
                .withSource("src/test/resources/exemple", "UTF-8")
                .withCodeComparatorClass(ComparatorWithSubstitutionAndType.class)
                .withMinimalTokenNumberDetection(10)
                );

        assertThat(writer.toString().split("\n")).contains(new String[] {
                "Tokens number:63 Duplications number:2 Substitutions number:0",
                "  100% (7 of 7 lines)fr.sf.once.CopieMethode.firstMethod from line 7 to 14 (method from line 7 to 14)",
                "  100% (7 of 7 lines)fr.sf.once.CopieMethode.secondMethod from line 16 to 23 (method from line 16 to 23)"});
        
    }
        
}
