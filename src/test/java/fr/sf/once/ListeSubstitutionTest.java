package fr.sf.once;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ListeSubstitutionTest {
    
    @Test
    public void testAjouterUnElementSurListeVide() {
        
        ListeSubstitution listeSubstitution = new ListeSubstitution();
        assertEquals(0, listeSubstitution.getPosition("B"));
    }

    @Test
    public void testAjouterDeuxElementsSurListe() {
        ListeSubstitution listeSubstitution = new ListeSubstitution();
        listeSubstitution.getPosition("A");

        assertEquals(1, listeSubstitution.getPosition("B"));
    }


    @Test
    public void testAjouterElementDejaPresent() {
        ListeSubstitution listeSubstitution = new ListeSubstitution();
        listeSubstitution.getPosition("A");
        listeSubstitution.getPosition("B");
        listeSubstitution.getPosition("C");

        assertEquals(1, listeSubstitution.getPosition("B"));
    }
}
