package fr.sf.once;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReportingImplTest {
    /**
 *
 */
    @Test
    public void testAppendFile() {
        ReportingImpl reportingImpl = new ReportingImpl(null);
        StringBuffer buffer = new StringBuffer();
        reportingImpl.appendFile(buffer, "fichier.java", 12);
        assertEquals("(fichier.java:12)", buffer.toString());

    }
}
