package fr.sf.once;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class LocalisationTest {
    @Test
    public void testAppendLocalisation() {
        StringBuffer buffer = new StringBuffer();
        Localisation localisation = new Localisation("fichier.java", 12, 3);
        localisation.appendLocalisation(buffer);
        assertEquals("(fichier.java:12)", buffer.toString());

    }
}
