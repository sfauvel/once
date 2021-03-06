package fr.sf.once.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.util.Comparator;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Before;
import org.junit.Test;

import fr.sf.once.comparator.ComparatorWithSubstitutionAndType;
import fr.sf.once.launcher.Launcher;
import fr.sf.once.report.Reporting;

public class EndToEndTest {
    private StringWriter writer = new StringWriter();

    @Before
    public void setReporter() {
        Reporting.LOG_RESULT.addAppender(new WriterAppender(new PatternLayout("%m\n"), writer));
        Reporting.LOG_RESULT.setLevel(Level.INFO);
    }
    
    @Test
    public void should_detect_separate_duplication() throws Exception {

        Launcher launcher = new Launcher()
                .withSource("src/test/resources/exempleSeparateDuplication", "UTF-8")
                .withComparator(ComparatorWithSubstitutionAndType.class)
                .withMinimalSize(10);
        
        launcher.execute();

        Comparator<? super String> comparator = new Comparator<String> () {

            @Override
            public int compare(String o1, String o2) {
                return o1.startsWith(o2)?0:1;
            }
            
        };
        assertThat(writer.toString().split("\n")).usingElementComparator(comparator).contains(
                "Tokens number:60 Duplications number:2 Substitutions number:1",
                "  50% (7 of 14 lines)fr.sf.once.CopieMethode.firstMethod from line ",
                "  43% (7 of 16 lines)fr.sf.once.CopieMethode.secondMethod from line "
                );
        
        // What we want at the end
//        assertThat(writer.toString().split("\n")).usingElementComparator(comparator).contains(
//                "Tokens number:101 Duplications number:2 Substitutions number:0",
//                "  100% (14 of 14 lines)fr.sf.once.CopieMethode.firstMethod from line ",
//                "  100% (14 of 14 lines)fr.sf.once.CopieMethode.secondMethod from line "
//                );
                
    }

    
}
