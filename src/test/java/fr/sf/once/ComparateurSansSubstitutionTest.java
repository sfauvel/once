package fr.sf.once;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.sf.once.test.UtilsToken;

public class ComparateurSansSubstitutionTest extends AbstractComparateurTest {

    @Before
    public void init() {
    }

    @Test
    public void testCompareSimple() throws Exception {
        Code code = creerCode("a", "b", "a");
        ComparateurSansSubstitution comparateur = new ComparateurSansSubstitution(code);

        assertTrue(comparateur.compare(0, 1) < 0);
        assertTrue(comparateur.compare(0, 2) > 0);
        assertTrue(comparateur.compare(1, 2) > 0);
    }

    @Test
    public void testCompareHorsLimite() throws Exception {
        Code code = creerCode("a", "b");
        ComparateurSansSubstitution comparateur = new ComparateurSansSubstitution(code);
        assertTrue(comparateur.compare(0, 2) > 0);
        assertTrue(comparateur.compare(5, 8) > 0);
        assertTrue(comparateur.compare(8, 5) < 0);

        boolean isException = false;
        try {
            assertTrue(comparateur.compare(-1, 0) > 0);
        } catch (Exception e) {
            isException = true;
        }
        assertTrue(isException);
    }
    
    @Test
    public void testCompareWithBreak() throws Exception {
        List<Token> tokenList = creerListeTokenListe("a", "a", "X", "a", "a", "a", "X", "z");
        tokenList.set(2, new Token(new Localisation("", 0, 0), "X", Type.BREAK));
        tokenList.set(6, new Token(new Localisation("", 0, 0), "X", Type.BREAK));
        Comparateur comparateur = new ComparateurSansSubstitution(new Code(tokenList));

        // Le break est au même niveau.
        assertEquals(0, comparateur.compare(0, 4));
        // La position 0 voit le Break en premier. Il est donc inférieur.
        assertEquals(-1, comparateur.compare(0, 3));
        assertEquals(1, comparateur.compare(3, 0));
    }

    @Test
    public void testComparePlusieursValeurs() throws Exception {
        List<Token> listeToken = creerListeTokenListe("a", "b", "x", "a", "b", "t");
        ComparateurSansSubstitution comparateur = new ComparateurSansSubstitution(new Code(listeToken));

        assertTrue(comparateur.compare(0, 3) > 0);
    }

    @Test
    public void testGetSortedToken() {
        checkSortedToken(Arrays.asList(0, 1, 2));
        checkSortedToken(Arrays.asList(1, 2, 0));
        checkSortedToken(Arrays.asList(2, 1, 0));
        checkSortedToken(Arrays.asList(1, 0, 2));
    }

    private void checkSortedToken(List<Integer> positionList) {
        Code code = creerCode("A", "B", "C");
        Comparateur comparateur = new ComparateurSansSubstitution(code);
        comparateur.sortList(positionList);
        assertEquals(0, positionList.get(0).intValue());
        assertEquals(1, positionList.get(1).intValue());
        assertEquals(2, positionList.get(2).intValue());
    }

    @Test
    public void testRedundancySizeAllDifferent() {

        Code code = creerCode("A", "B", "D", "E", "F", "G", "H");
        Comparateur comparator = new ComparateurSansSubstitution(code);
        int[] redundancySize = comparator.getRedundancySize(Arrays.asList(0, 1, 2, 5));
        assertEquals(3, redundancySize.length);
        assertEquals(0, redundancySize[0]);
        assertEquals(0, redundancySize[1]);
        assertEquals(0, redundancySize[2]);
    }

    @Test
    public void testRedundancySizeWithSameValue() {

        Code code = creerCode("A", "B", "C", "A", "B", "D", "E");
        Comparateur comparator = new ComparateurSansSubstitution(code);
        int[] redundancySize = comparator.getRedundancySize(Arrays.asList(0, 3, 1, 4));
        assertEquals(3, redundancySize.length);
        assertEquals(2, redundancySize[0]);
        assertEquals(0, redundancySize[1]);
        assertEquals(1, redundancySize[2]);
    }
    
    // @Test
    // public void testGetListeTriee() throws Exception {
    // List<Token> listeToken = creerListeTokenListe("a", "b", "a");
    // ComparateurSansSubstitution comparateur = new
    // ComparateurSansSubstitution(listeToken);
    //
    // assertListeTriee(comparateur, "1 2 3", "abc");
    // assertListeTriee(comparateur, "2 1 3", "bac");
    // assertListeTriee(comparateur, "3 2 1", "cba");
    // }
    //
    // @Test
    // public void testGetListeTrieePosition() throws Exception {
    // List<Token> listeToken = new ArrayList<Token>();
    // listeToken.add(new Token(new Localisation("", 11, 0), "a", Type.VALEUR));
    // listeToken.add(new Token(new Localisation("", 12, 0), "d",
    // Type.NON_SIGNIFICATIF));
    // listeToken.add(new Token(new Localisation("", 13, 0), "f",
    // Type.NON_SIGNIFICATIF));
    // listeToken.add(new Token(new Localisation("", 14, 0), "b", Type.VALEUR));
    // listeToken.add(new Token(new Localisation("", 15, 0), "e",
    // Type.NON_SIGNIFICATIF));
    // listeToken.add(new Token(new Localisation("", 16, 0), "c", Type.VALEUR));
    //
    // ComparateurSansSubstitution comparateur = new
    // ComparateurSansSubstitution(listeToken);
    // // comparateur.setListeTokens(listeToken);
    //
    // List<TokenAvecPosition> listeTriee = new
    // ArrayList<TokenAvecPosition>(comparateur.getListeTriee());
    // {
    // TokenAvecPosition tokenAvecPosition = listeTriee.get(0);
    // assertEquals("a", tokenAvecPosition.getValeurToken());
    // assertEquals(11, tokenAvecPosition.getLigneDebut().intValue());
    // }
    // {
    // TokenAvecPosition tokenAvecPosition = listeTriee.get(1);
    // assertEquals("b", tokenAvecPosition.getValeurToken());
    // assertEquals(14, tokenAvecPosition.getLigneDebut().intValue());
    // }
    //
    // {
    // TokenAvecPosition tokenAvecPosition = listeTriee.get(2);
    // assertEquals("c", tokenAvecPosition.getValeurToken());
    // assertEquals(16, tokenAvecPosition.getLigneDebut().intValue());
    // }
    // }
    //
    // @Test
    // public void testGetTailleRedondance() throws Exception {
    //
    // List<Token> listeToken = new ArrayList<Token>();
    // listeToken.add(new Token(new Localisation("", 11, 0), "a", Type.VALEUR));
    // listeToken.add(new Token(new Localisation("", 12, 0), "d",
    // Type.NON_SIGNIFICATIF));
    // listeToken.add(new Token(new Localisation("", 13, 0), "f",
    // Type.NON_SIGNIFICATIF));
    // listeToken.add(new Token(new Localisation("", 14, 0), "a", Type.VALEUR));
    // listeToken.add(new Token(new Localisation("", 15, 0), "d",
    // Type.NON_SIGNIFICATIF));
    // listeToken.add(new Token(new Localisation("", 16, 0), "c", Type.VALEUR));
    //
    // ComparateurSansSubstitution comparateur = new
    // ComparateurSansSubstitution(listeToken);
    // // comparateur.setListeTokens(listeToken);
    //
    // List<TokenAvecPosition> listeTriee = new
    // ArrayList<TokenAvecPosition>(comparateur.getListeTriee());
    // int[] tailleRedondance = comparateur.getTailleRedondance(listeTriee);
    // assertEquals(2, tailleRedondance[0]);
    // assertEquals(0, tailleRedondance[1]);
    // assertEquals(0, tailleRedondance[2]);
    // assertEquals(1, tailleRedondance[3]);
    // assertEquals(0, tailleRedondance[4]);
    // }
}
