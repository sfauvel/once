package fr.sf.once.launcher;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Before;
import org.junit.Test;

import fr.sf.once.comparator.ComparateurAvecSubstitutionEtType;
import fr.sf.once.report.Reporting;

public class LauncherITest {
    private StringWriter writer = new StringWriter();

    @Before
    public void setReporter() {
        Reporting.LOG_RESULTAT.addAppender(new WriterAppender(new PatternLayout("%m\n"), writer));
        Reporting.LOG_RESULTAT.setLevel(Level.INFO);
    }
    
    @Test
    public void make_a_full_execution() throws Exception {

        Launcher launcher = new Launcher()
                .withSource("src/test/resources/exemple", "UTF-8")
                .withComparator(ComparateurAvecSubstitutionEtType.class)
                .withMinimalSize(10);
        
        launcher.execute();

        assertThat(writer.toString().split("\n")).isEqualTo(new String[] {
                "Tokens number:63 Duplications number:2 Substitutions number:0",
                "  100% 7/7 lines fr.sf.once.CopieMethode.firstMethod 7:51 <-> 14:6",
                "  100% 7/7 lines fr.sf.once.CopieMethode.secondMethod 16:53 <-> 23:6" });
    }
    
}