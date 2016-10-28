package fr.sf.once.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.sf.once.model.Location;


public class LocationTest {
    @Test
        public void testAppendLocation() {
            StringBuffer buffer = new StringBuffer();
            Location localisation = new Location("fichier.java", 12, 3);
            localisation.appendLocation(buffer);
            assertEquals("(fichier.java:12/3)", buffer.toString());
    
        }
}
