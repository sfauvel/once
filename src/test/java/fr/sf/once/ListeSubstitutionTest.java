package fr.sf.once;

import static org.junit.Assert.*;
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
    
    final Token tokenA = new Token(null, "A", Type.VALEUR);
    final Token tokenB = new Token(null, "B", Type.VALEUR);
    final Token tokenC = new Token(null, "C", Type.VALEUR);
    
    @Test
    public void testAjouterPremierToken() {
        ListeSubstitution listeSubstitution = new ListeSubstitution();
        assertEquals(0, listeSubstitution.getPosition(tokenA));
        assertEquals(0, listeSubstitution.getPosition(tokenA));
    }

    @Test
    public void testAjouterTokenDifferentMaisEgal() {
        ListeSubstitution listeSubstitution = new ListeSubstitution();
        assertEquals(0, listeSubstitution.getPosition(tokenA));
        assertEquals(0, listeSubstitution.getPosition(new Token(null, tokenA.getValeurToken(), tokenA.getType())));
    }

    
    @Test
    public void testAjouterSecondToken() {
        ListeSubstitution listeSubstitution = new ListeSubstitution();
        assertEquals(0, listeSubstitution.getPosition(tokenA));
        assertEquals(1, listeSubstitution.getPosition(tokenB));
        assertEquals(1, listeSubstitution.getPosition(tokenB));
    }
    
    @Test
    public void testAjouterTokenTypeDifferent() {

        final Token tokenATypeBreak = new Token(null, "A", Type.BREAK);
        assertTrue(tokenATypeBreak.getType() != tokenA.getType());
        
        ListeSubstitution listeSubstitution = new ListeSubstitution();
        assertEquals(0, listeSubstitution.getPosition(tokenA));
        assertEquals(1, listeSubstitution.getPosition(tokenATypeBreak));
    }
}
