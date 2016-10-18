package fr.sf.once.comparator;

import static fr.sf.once.test.UtilsToken.createPositionArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import fr.sf.once.AbstractComparatorTest;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.test.UtilsToken;

public class ComparatorWithSubstitutionTest extends AbstractComparatorTest {

    @Test
    public void should_find_the_position_1_is_greater_than_the_position_2_when_no_substitution_never_mind_the_values() {
        // 0: abc, 1:bc, 2:c => 0:123, 1:12, 2:1
        assertThat(createComparator("a b c").compare(1, 2)).isGreaterThan(0);
        assertThat(createComparator("a b c").compare(2, 1)).isLessThan(0);
        
        // 0: bac, 1:ac, 2:c => 0:123, 1:12, 2:1 
        assertThat(createComparator("b a c").compare(1, 2)).isGreaterThan(0);
        assertThat(createComparator("b a c").compare(2, 1)).isLessThan(0);
    }
   
    @Test
    public void should_find_positions_are_equals_when_its_the_same_postion() {
        assertThat(createComparator("a b c").compare(1, 1)).isEqualTo(0);
    }

    @Test
    public void should_find_the_position_0_is_greater_than_position_5_when_code_are_exactly_the_same_from_those_positions() {
        // 0:abc__abc__, 5:abc__ => 0:1234412344, 5:12344 
        assertThat(createComparator("a b c _ _ a b c _ _").compare(0, 5)).isGreaterThan(0);
        assertThat(createComparator("a b c _ _ a b c _ _").compare(5, 0)).isLessThan(0);
    }

    @Test
    public void should_find_the_position_4_is_greater_than_position_0_when_there_is_more_substitution_form_the_position_4() {
        // 0:aabbccdX, 4:ccdX => 0:11223345, 4:1123
        assertThat(createComparator("a a b b c c d X").compare(0, 4)).isLessThan(0);
        assertThat(createComparator("a a b b c c d X").compare(4, 0)).isGreaterThan(0);
    }

    @Test
    public void should_find_the_position_0_is_greater_than_position_4_when_there_is_more_substitution_form_the_position_0() {
        // 0:aabXccdd, 4:ccdd => 0:11233344, 4:1122
        assertThat(createComparator("a a b X c c d d").compare(0, 4)).isGreaterThan(0);
        assertThat(createComparator("a a b X c c d d").compare(4, 0)).isLessThan(0);
    }

    @Test
    public void testGetRedundancySizeParPosition() throws Exception {
        CodeComparator comparateur = new ComparatorWithSubstitution(createCode("a a b b c c d e"));
        assertEquals(8, comparateur.getRedundancySize(0, 0));
        assertEquals(3, comparateur.getRedundancySize(0, 4));
        assertEquals(1, comparateur.getRedundancySize(0, 3));
    }

    @Test
    public void shoul_return_a_reverse_order_list_when_no_substitution() throws Exception {
        // Initial values
        // 0:abc, 1:bc, 2:c
        // After substitution
        // 0:123, 1:12, 2:1
        // Order List
        // 2:1, 1:12, 0:123
        CodeComparator comparator = new ComparatorWithSubstitution(createCode("A B C"));
        List<Integer> positionList = UtilsToken.createPositionList(3);

        comparator.sortList(positionList);

        assertThat(positionList).containsExactly(2, 1, 0);
    }

    @Test
    public void shoul_return_a_reverse_order_list_when_no_substitution_never_mind_the_initial_order() throws Exception {
        // Initial values
        // 0:bac, 1:ac, 2:c
        // After substitution
        // 0:123, 1:12, 2:1
        // Order List
        // 2:1, 1:12, 0:123
        CodeComparator comparator = new ComparatorWithSubstitution(createCode("B A C"));
        List<Integer> positionList = UtilsToken.createPositionList(3);

        comparator.sortList(positionList);

        assertThat(positionList).containsExactly(2, 1, 0);
    }

    @Test
    public void should_sort_substituing_values_to_compare() throws Exception {
        // Initial values
        // 0:AABB, 1:ABB, 2:BB, 3:B
        // After substitution
        // 0:1122, 1:122, 2:11, 3:1
        // Order List
        // 3:1, 2:11, 0:1122, 1:122
        CodeComparator comparator = new ComparatorWithSubstitution(createCode("A A B B"));
        List<Integer> positionList = UtilsToken.createPositionList(4);

        comparator.sortList(positionList);

        assertThat(positionList).containsExactly(3, 2, 0, 1);
    }

    @Test
    public void testGetRedundancySizeWithSpace() throws Exception {
        final Code CODE = createCode(
                "a b c d c b b a ;",
                "A B C A C A A B ;");

        assertEquals(3, new ComparatorWithSubstitution(CODE).getRedundancySize(0, 9));
        assertEquals(5, new ComparatorWithSubstitution(CODE).getRedundancySize(4, 13));

        CodeComparator comparateur = new ComparatorWithSubstitution(CODE);
        assertEquals(3, comparateur.getRedundancySizeWithPreviousSubstitution(0, 9));
        // Only 'c' with 'C'. 'b' doesn't match with 'A'.
        assertEquals(1, comparateur.getRedundancySizeWithPreviousSubstitution(4, 13));
    }

    @Test
    public void testGetRedundancySizeCaractereNonInterchangeable() throws Exception {

        String[] tokenList = { "a a b x c c d z" };
        assertEquals(4, new ComparatorWithSubstitution(createCode(tokenList)).getRedundancySize(0, 4));
        String[] tokenList1 = { "a a : x c c d z" };
        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList1)).getRedundancySize(0, 4));
        String[] tokenList2 = { "a a b x c c : z" };
        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList2)).getRedundancySize(0, 4));
        String[] tokenList3 = { "a a ( x c c d z" };

        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList3)).getRedundancySize(0, 4));
        String[] tokenList4 = { "a a { x c c d z" };
        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList4)).getRedundancySize(0, 4));
        String[] tokenList5 = { "a a } x c c d z" };
        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList5)).getRedundancySize(0, 4));
        String[] tokenList6 = { "a a [ x c c d z" };
        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList6)).getRedundancySize(0, 4));
        String[] tokenList7 = { "a a ] x c c d z" };
        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList7)).getRedundancySize(0, 4));
        String[] tokenList8 = { "a a ; x c c d z" };
        assertEquals(2, new ComparatorWithSubstitution(createCode(tokenList8)).getRedundancySize(0, 4));
        String[] tokenList9 = { "a a : x c c : z" };

        assertEquals(4, new ComparatorWithSubstitution(createCode(tokenList9)).getRedundancySize(0, 4));

    }

    @Test
    public void testGetRedundancySize() throws Exception {

        CodeComparator comparator = new ComparatorWithSubstitution(createCode("a a b x c c d z"));
        assertEquals(4, comparator.getRedundancySize(0, 4));
    }

    @Test
    public void testGetRedundancySizeWithDifference() throws Exception {

        CodeComparator comparator = new ComparatorWithSubstitution(createCode("a b a x c d e z"));
        assertEquals(2, comparator.getRedundancySize(0, 4));
    }

    /**
     * Test que les token de type BREAK stop la redondance.
     * 
     * @throws Exception
     */
    @Test
    public void testGetRedundancySizeWithBreak() throws Exception {

        List<Token> tokenList = UtilsToken.createTokenList("a a X c c c X z");
        changeTokenType(tokenList, 2, Type.BREAK);
        changeTokenType(tokenList, 6, Type.BREAK);
        CodeComparator comparateur = new ComparatorWithSubstitution(new Code(tokenList));

        // On bloque au niveau de caracactère spécifique de fin.
        assertEquals(2, comparateur.getRedundancySize(0, 4));
    }

    @Test
    public void testCompareWithBreak() throws Exception {
        List<Token> tokenList = UtilsToken.createTokenList("a a X c c c X z");
        changeTokenType(tokenList, 2, Type.BREAK);
        changeTokenType(tokenList, 6, Type.BREAK);
        CodeComparator comparateur = new ComparatorWithSubstitution(new Code(tokenList));

        // Le break est au même niveau.
        assertEquals(0, comparateur.compare(0, 4));
        // La position 0 voit le Break en premier. Il est donc inférieur.
        assertEquals(-1, comparateur.compare(0, 3));
        assertEquals(1, comparateur.compare(3, 0));
    }

    /**
     * A B A C E G E F 0:1 2 1 3 4 5 4 6 1: 1 2 3 4 5 4 6 2: 1 2 3 4 3 5 3: 1 2
     * 3 2 4 4: 1 2 1 3 5: 1 2 3 6: 1 2 7: 1
     */
    @Test
    public void testComparateurAvecSubstitution() {

        CodeComparator comparator = new ComparatorWithSubstitution(createCode("A B A C E G E F"));
        assertTrue(comparator.compare(0, 1) < 0); // A B A - B A C | 1 2 1 - 1
                                                  // 2 3
        assertTrue(comparator.compare(2, 3) > 0); // A C E G - C E G E | 1 2 3
                                                  // 4 - 1 2 3 2

    }

    /**
     * 0 1 2
     * A A B
     * 0: 1 1 2 -> 1
     * 1: 1 2 -> 2
     * 2: 1 -> 0
     */
    @Test
    public void testTrierListeTokenSansModifierListeOrigine() {
        Code code = createCode("A A B");

        List<Integer> positionList = range(code.getSize());
        Collections.sort(positionList, new ComparatorWithSubstitution(code));

        assertThat(positionList).containsExactly(2, 0, 1);
        assertThat(tokensMapToPosition(createCode("A A B"), positionList)).containsExactly("B", "A", "A");
    }

    /**
     * 0 1 2 3 4 5
     * A E A B A C
     * 0: 1 2 1 3 1 4 -> 3
     * 1: 1 2 3 2 4 -> 5
     * 2: 1 2 1 3 -> 2
     * 3: 1 2 3 -> 4
     * 4: 1 2 -> 1
     * 5: 1 -> 0
     */
    @Test
    public void testTrierSurPlusieursTokens() {
        Code code = createCode("A E A B A C");
        List<Integer> positionList = range(code.getSize());

        Collections.sort(positionList, new ComparatorWithSubstitution(code));

        assertThat(positionList).containsExactly(5, 4, 2, 0, 3, 1);
        assertThat(tokensMapToPosition(code, positionList)).containsExactly("C", "A", "A", "A", "B", "E");
    }

    private Stream<String> tokensMapToPosition(Code code, List<Integer> positionList) {
        return positionList.stream().map(p -> code.getToken(p).getValeurToken());
    }

    private List<Integer> range(int endExclusive) {
        return IntStream.range(0, endExclusive).boxed().collect(Collectors.toList());
    }

    private ComparatorWithSubstitution createComparator(String... code) {
        return new ComparatorWithSubstitution(createCode(code));
    }

}
