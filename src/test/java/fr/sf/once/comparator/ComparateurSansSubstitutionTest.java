package fr.sf.once.comparator;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.fest.assertions.api.ListAssert;
import org.junit.Test;

import fr.sf.once.AbstractComparateurTest;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;

public class ComparateurSansSubstitutionTest extends AbstractComparateurTest {

    /**
     * 0: a b a
     * 1: b a
     * 2: a
     */
    @Test
    public void comparing_token_give_alphabetic_order() throws Exception {
        Comparateur comparator = createComparatorWithCode("a b a");

        assertThat(comparator.compare(0, 1)).isLessThan(0);
        assertThat(comparator.compare(0, 2)).isGreaterThan(0);
        assertThat(comparator.compare(1, 2)).isGreaterThan(0);
    }

    @Test
    public void using_position_greater_than_size_give_position_order() throws Exception {
        Comparateur comparator = createComparatorWithCode("a b");
        
        assertThat(comparator.compare(0, 2)).isGreaterThan(0);
        assertThat(comparator.compare(5, 8)).isGreaterThan(0);
        assertThat(comparator.compare(8, 5)).isLessThan(0);
    }

    @Test
    public void using_position_less_than_0_thrown_exception() throws Exception {
        Comparateur comparator = createComparatorWithCode("a b");
 
        try {
            comparator.compare(-1, 0);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        } catch (IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("-1");
        }
        
        try {
            comparator.compare(0, -1);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        } catch (IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("-1");
        }
    }

    /**
     * 0: a a X a a a X z
     * 1: a X a a a X z
     * 2: X a a a X z
     * 3: a a a X z
     * 4: a a X z
     * 5: a X z
     * 6: X z
     * 7: z
     * @throws Exception
     */
    @Test
    public void when_a_break_token_is_present_the_comparaison_stop() throws Exception {
        List<Token> tokenList = creerListeTokenListe("a a X a a a X z");
        changeTokenType(tokenList, 2, Type.BREAK);
        changeTokenType(tokenList, 6, Type.BREAK);
        Comparateur comparateur = new ComparateurSansSubstitution(new Code(tokenList));

        // Break is at the same position.
        // 0: a a X a a a X z
        // 4: a a X z
        assertThat(comparateur.compare(0, 4)).isEqualTo(0);
    }

    /**
     * 0: a a X a a a X z
     * 1: a X a a a X z
     * 2: X a a a X z
     * 3: a a a X z
     * 4: a a X z
     * 5: a X z
     * 6: X z
     * 7: z
     * @throws Exception
     */
    @Test
    public void a_break_token_is_always_less_than_other_token() throws Exception {
        List<Token> tokenList = creerListeTokenListe("a a X a a a X z");
        changeTokenType(tokenList, 2, Type.BREAK);
        changeTokenType(tokenList, 6, Type.BREAK);
        Comparateur comparateur = new ComparateurSansSubstitution(new Code(tokenList));
        
        // The first break between 0 and 3 is on position 0. 0 is less than 3.
        // 0: a a X a a a X z
        // 3: a a a X z
        assertThat(comparateur.compare(0, 3)).isEqualTo(-1);
        assertThat(comparateur.compare(3, 0)).isEqualTo(1);
    }

    /**
     * @param tokenList List where the token to change is.
     * @param position Token position to change.
     * @param newType New type for the token.
     */
    private void changeTokenType(List<Token> tokenList, int position, Type newType) {
        Token token = tokenList.get(position);
        tokenList.set(position, new Token(token.getlocalisation(), token.getValeurToken(), newType));
    }
    
    /**
     * 0: a b x a b t
     * 1: b x a b t
     * 2: x a b t
     * 3: a b t
     * 4: b t
     * 5: t
     * @throws Exception
     */
    @Test
    public void comparing_two_starting_points_the_less_value_is_the_first_with_a_token_less_than_the_other_at_the_same_postion() throws Exception {
        Comparateur comparator = createComparatorWithCode("a b x a b t");

        assertThat(comparator.compare(0, 3)).isGreaterThan(0);
    }

    @Test
    public void when_we_sort_position_list_we_always_obtain_the_same_result() {
        Comparateur comparator = createComparatorWithCode("A B C");
        assertThatSortedPosition(comparator, 0, 1, 2).containsExactly(0, 1, 2);
        assertThatSortedPosition(comparator, 1, 2, 0).containsExactly(0, 1, 2);
        assertThatSortedPosition(comparator, 2, 1, 0).containsExactly(0, 1, 2);
        assertThatSortedPosition(comparator, 1, 0, 2).containsExactly(0, 1, 2);
    }

    private ListAssert<Integer> assertThatSortedPosition(Comparateur comparateur, Integer... positionArray) {
        List<Integer> positionList = Arrays.asList(positionArray);
        comparateur.sortList(positionList);
        return assertThat(positionList);
    }

    @Test
    public void when_all_token_are_differents_all_redundancy_size_equals_0() {
        Comparateur comparator = createComparatorWithCode("A B D E F G H");
        int[] redundancySizeList = comparator.getRedundancySize(Arrays.asList(0, 1, 2, 5));
        assertThat(redundancySizeList).isEqualTo(new int[]{0, 0, 0});
    }

    @Test
    public void testRedundancySizeWithSameValue() {

        Code code = createCode("A B C A B D E");
        Comparateur comparator = new ComparateurSansSubstitution(code);
        int[] redundancySize = comparator.getRedundancySize(Arrays.asList(0, 3, 1, 4));
        assertEquals(3, redundancySize.length);
        assertEquals(2, redundancySize[0]);
        assertEquals(0, redundancySize[1]);
        assertEquals(1, redundancySize[2]);
    }
    
    private Comparateur createComparatorWithCode(String stringTokenList) {
        Code code = createCode(stringTokenList);
        return new ComparateurSansSubstitution(code);
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
