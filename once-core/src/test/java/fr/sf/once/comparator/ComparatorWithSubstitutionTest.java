package fr.sf.once.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import fr.sf.once.AbstractComparatorTest;
import fr.sf.once.model.Code;
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
    public void should_find_the_lower_replacing_all_token_by_a_number() {
        // 0: a b a = 1 2 1, 4: c d d = 1 2 2
        assertThat(createComparator("a b a _ c d d").compare(0, 4)).isLessThan(0);
        assertThat(createComparator("a b a _ c d d").compare(4, 0)).isGreaterThan(0);
        // 0: a b c d = 1 2 3 4, 4: e f g e = 1 2 3 2
        assertThat(createComparator("a b c d _ e f g e").compare(0, 5)).isGreaterThan(0);
        assertThat(createComparator("a b c d _ e f g e").compare(5, 0)).isLessThan(0);
    }
    
    @Test
    public void should_two_positions_are_equal_when_code_is_the_same_until_a_break() throws Exception {
        CodeComparator comparateur = createComparator("a a BREAK c c c BREAK z");
        // 0: a a BREAK, 4: c c BREAK
        assertThat(comparateur.compare(0, 4)).isEqualTo(0);
    }

    @Test
    public void should_find_the_code_with_less_token_before_the_break_lower_than_other() throws Exception {
        CodeComparator comparateur = createComparator("a a BREAK c c c BREAK z");
        // Only 2 tokens before the BREAK on position 0 against 3 tokens on position 3. 
        // The code on 0 is lower than the one on 3.
        // 0: a a BREAK, 3: c c c BREAK
        assertThat(comparateur.compare(0, 3)).isLessThan(0);
        assertThat(comparateur.compare(3, 0)).isGreaterThan(0);
    }

    @Test
    public void should_return_size_of_1_when_only_one_token_for_one_position() {
        assertThat(new ComparatorWithSubstitution(createCode("a b c")).getRedundancySize(0, 2)).isEqualTo(1);
    }

    @Test
    public void should_return_size_of_the_shortest_token_list_when_they_are_duplicated() {
        // 0:aabb, 2:bb
        assertThat(createComparator("a a b b").getRedundancySize(0, 0)).isEqualTo(4);
        assertThat(createComparator("a a b b").getRedundancySize(2, 2)).isEqualTo(2);

        assertThat(createComparator("a a b b").getRedundancySize(0, 2)).isEqualTo(2);
    }

    @Test
    public void should_find_the_longest_token_list_that_could_be_substituable() {
        assertThat(createComparator("a a b b c c d e").getRedundancySize(0, 0)).isEqualTo(8);
        assertThat(createComparator("a a b b c c d e").getRedundancySize(0, 4)).isEqualTo(3);
        assertThat(createComparator("a a b b c c d e").getRedundancySize(0, 3)).isEqualTo(1);
    }

    @Test
    public void should_return_size_of_3_when_the_first_token_not_matching_is_on_the_4th_position() {
        // The second A not matching with d because is map with a.
        assertThat(createComparator("a b c d e A B C A D").getRedundancySize(0, 5)).isEqualTo(3);
    }

    @Test
    public void should_return_size_of_0_when_no_duplication_because_of_a_non_substituable_token() {
        assertThat(createComparator("a b").getRedundancySize(0, 1)).isEqualTo(1);
        assertThat(createComparator("a :").getRedundancySize(0, 1)).isEqualTo(0);
        assertThat(createComparator("a (").getRedundancySize(0, 1)).isEqualTo(0);
        assertThat(createComparator("a )").getRedundancySize(0, 1)).isEqualTo(0);
        assertThat(createComparator("a {").getRedundancySize(0, 1)).isEqualTo(0);
        assertThat(createComparator("a }").getRedundancySize(0, 1)).isEqualTo(0);
        assertThat(createComparator("a [").getRedundancySize(0, 1)).isEqualTo(0);
        assertThat(createComparator("a ]").getRedundancySize(0, 1)).isEqualTo(0);
        assertThat(createComparator("a ;").getRedundancySize(0, 1)).isEqualTo(0);
    }

    @Test
    public void should_return_size_until_different_non_substituable_token_is_found() {
        assertThat(createComparator("a b c d A B C D").getRedundancySize(0, 4)).isEqualTo(4);
        assertThat(createComparator("a b ; d A B , D").getRedundancySize(0, 4)).isEqualTo(2);
    }

    @Test
    public void should_pass_non_substituable_token_when_it_is_the_same() throws Exception {
        assertThat(createComparator("; ;").getRedundancySize(0, 1)).isEqualTo(1);
        assertThat(createComparator("a b ; d A B ; D").getRedundancySize(0, 4)).isEqualTo(4);
    }

    /**
     * When a BREAK is found, the duplication is stop even there is the same BREAK instruction between
     * the two portions of code.
     */
    @Test
    public void should_return_size_until_a_break_token_is_found() {
        assertThat(createComparator("a b c d A B C D").getRedundancySize(0, 4)).isEqualTo(4);
        assertThat(createComparator("a b BREAK d A B BREAK D").getRedundancySize(0, 4)).isEqualTo(2);
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
        return positionList.stream().map(p -> code.getToken(p).getTokenValue());
    }

    private List<Integer> range(int endExclusive) {
        return IntStream.range(0, endExclusive).boxed().collect(Collectors.toList());
    }

    private ComparatorWithSubstitution createComparator(String... code) {
        return new ComparatorWithSubstitution(createCode(code));
    }

}
