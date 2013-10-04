package fr.sf.once.core;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fr.sf.once.model.Token;
import fr.sf.once.model.Type;

public class ListeSubstitutionTest {
    
    
    private final Token tokenA = new Token(null, "A", Type.VALEUR);
    private final Token tokenB = new Token(null, "B", Type.VALEUR);
    private final Token tokenC = new Token(null, "C", Type.VALEUR);
    private ListeSubstitution listeSubstitution;

    @Before
    public void initialiseObjetSousTest() {
        listeSubstitution = new ListeSubstitution();
    }
    
    @Test
    public void testAjouterUnElementSurListeVide() {
        assertThat(listeSubstitution.getPosition("B")).isEqualTo(0);
    }

    @Test
    public void testAjouterDeuxElementsSurListe() {
        listeSubstitution.getPosition("A");
        assertThat(listeSubstitution.getPosition("B")).isEqualTo(1);
    }


    @Test
    public void testAjouterElementDejaPresent() {
        listeSubstitution.getPosition("A");
        listeSubstitution.getPosition("B");
        listeSubstitution.getPosition("C");

        assertThat(listeSubstitution.getPosition("B")).isEqualTo(1);
    }
    
    @Test
    public void testAjouterPremierToken() {
        assertThat(listeSubstitution.getPosition("A")).isEqualTo(0);
        assertThat(listeSubstitution.getPosition("A")).isEqualTo(0);
    }

    @Test
    public void testAjouterTokenDifferentMaisEgal() {
        assertThat(listeSubstitution.getPosition("A")).isEqualTo(0);
        Token otherTokenReferenceWithSameValue = new Token(null, tokenA.getValeurToken(), tokenA.getType());
        assertEquals(0, listeSubstitution.getPosition(otherTokenReferenceWithSameValue));
    }

    
    @Test
    public void testAjouterSecondToken() {
        assertThat(listeSubstitution.getPosition("A")).isEqualTo(0);
        assertThat(listeSubstitution.getPosition("B")).isEqualTo(1);
        assertThat(listeSubstitution.getPosition("B")).isEqualTo(1);
    }
    
    @Test
    public void testAjouterTokenTypeDifferent() {
        final Token tokenATypeBreak = new Token(null, "A", Type.BREAK);
        assertThat(tokenA.getType()).isNotEqualTo(tokenATypeBreak.getType());
        assertThat(listeSubstitution.getPosition(tokenA)).isEqualTo(0);
        assertThat(listeSubstitution.getPosition(tokenATypeBreak)).isEqualTo(1);
    }
}
