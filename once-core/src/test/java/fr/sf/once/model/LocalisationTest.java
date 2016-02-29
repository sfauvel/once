package fr.sf.once.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.sf.once.model.Localisation;


public class LocalisationTest {
    @Test
    public void testAppendLocalisation() {
        StringBuffer buffer = new StringBuffer();
        Localisation localisation = new Localisation("fichier.java", 12, 3);
        localisation.appendLocalisation(buffer);
        assertEquals("(fichier.java:12/3)", buffer.toString());

    }
}