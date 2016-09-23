package fr.sf.once.core;

import static fr.sf.once.test.OnceAssertions.assertThat;
import static fr.sf.once.test.UtilsToken.createUnmodifiableTokenList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.math.IntRange;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.internal.util.RemoveFirstLine;
import org.w3c.dom.ranges.RangeException;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import fr.sf.once.test.LogRule;
import fr.sf.once.test.UtilsToken;

public class ManagerTokenTest {

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();
    private ManagerToken emptyManager;

    @Before
    public void initManager() {
        emptyManager = new ManagerToken(Collections.<Token> emptyList()) {
            @Override
            protected void registerRedundancyToMethod(List<Integer> subList, Redundancy redundancy) {
            }
        };
    }

    @Test
    public void creating_a_redundancy_between_2_block_i_retrieve_start_position_of_each_block() {
        final int FIRST_POSITION = 34;
        final int SECOND_POSITION = 65;

        Redundancy redundancy = emptyManager.createRedundancy(1, Arrays.asList(FIRST_POSITION, SECOND_POSITION));

        assertThat(redundancy.getStartRedundancyList()).containsOnly(FIRST_POSITION, SECOND_POSITION);
    }

    @Test
    public void creating_a_redundancy_between_3_block_the_redundancy_number_is_3_and_it_s_equal_to_redundancy_size() {
        Redundancy redundancy = emptyManager.createRedundancy(1, Arrays.asList(11, 22, 33));

        assertThat(redundancy.getRedundancyNumber())
                .isEqualTo(3)
                .isEqualTo(redundancy.getStartRedundancyList().size());
    }

    @Test
    public void creating_a_redundancy_on_7_tokens_the_duplicate_token_number_is_7() {
        final int REDUNDANCY_TOKEN_NUMBER = 3;

        Redundancy redundancy = emptyManager.createRedundancy(REDUNDANCY_TOKEN_NUMBER, Arrays.asList(11, 22, 33));

        assertThat(redundancy.getDuplicatedTokenNumber()).isEqualTo(REDUNDANCY_TOKEN_NUMBER);
    }

    @Test
    public void when_i_get_a_token_from_a_position_i_have_the_corresponding_token() {
        ManagerToken manager = new ManagerToken(createUnmodifiableTokenList("A", "B", "C", "D", "E"));
        assertThat(manager.getToken(0)).hasValue("A");
        assertThat(manager.getToken(1)).hasValue("B");
        assertThat(manager.getToken(2)).hasValue("C");
        assertThat(manager.getToken(3)).hasValue("D");
        assertThat(manager.getToken(4)).hasValue("E");
    }

    @Test
    public void when_i_get_a_token_from_a_position_out_of_the_bound_i_have_an_exception() {
        ManagerToken manager = new ManagerToken(createUnmodifiableTokenList("A", "B"));
        try {
            manager.getToken(2);
            fail("IndexOutOfBoundsException expected because manager has only 2 tokens");
        } catch (IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("Index: 2, Size: 2");
        }
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
        Code code = createCodeWith("A", "A", "B");
        List<Integer> positionList = range(code.getSize());

        Collections.sort(positionList, new ComparatorWithSubstitution(code));

        assertThat(positionList).containsExactly(2, 0, 1);
        assertThat(tokensMapToPosition(code, positionList)).containsExactly("B", "A", "A");
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
        Code code = createCodeWith("A", "E", "A", "B", "A", "C");
        List<Integer> positionList = range(code.getSize());

        Collections.sort(positionList, new ComparatorWithSubstitution(code));

        assertThat(positionList).containsExactly(5, 4, 2, 0, 3, 1);
        assertThat(tokensMapToPosition(code, positionList)).containsExactly("C", "A", "A", "A", "B", "E");
    }

    /**
     * A A B B 0:1 1 2 2-> 2 1: 1 2 2-> 3 2: 1 1-> 1 3: 1-> 0 B : 1 B B : 1 1 A
     * A B B: 1 1 2 2 A B B : 1 2 2
     */
    @Test
    public void testAfficherRedondance() {
        ManagerToken manager = new ManagerToken(createUnmodifiableTokenList("A", "A", "B", "B"));

        List<Redundancy> listeRedondance = manager.getRedondance(0);

        Redundancy red = listeRedondance.get(0);

        assertRedundancySize(2, listeRedondance.get(0));
        assertRedundancySize(1, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondanceUneSeuleValeur() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F");
        List<Integer> positionList = UtilsToken.createPositionList(2);
        List<Redundancy> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 3 },
                0);

        assertRedundancySize(3, listeRedondance.get(0));
        assertEquals(1, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondance() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(3);
        List<Redundancy> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 3, 5 },
                0);

        assertRedundancySize(3, listeRedondance.get(0));
        assertRedundancySize(5, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondanceToujoursPlusGrand() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M");
        List<Integer> positionList = UtilsToken.createPositionList(6);
        List<Redundancy> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 2, 4, 6, 7, 8 },
                0);

        assertRedundancySize(2, listeRedondance.get(0));
        assertRedundancySize(4, listeRedondance.get(1));
        assertRedundancySize(6, listeRedondance.get(2));
        assertRedundancySize(7, listeRedondance.get(3));
        assertRedundancySize(8, listeRedondance.get(4));
        assertEquals(5, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondanceToujoursEgal() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K");
        List<Integer> positionList = UtilsToken.createPositionList(6);
        List<Redundancy> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 5, 5, 5, 5 },
                0);

        assertRedundancySize(5, listeRedondance.get(0));
        assertEquals(1, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondanceToujoursPlusPetit() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K");
        List<Integer> positionList = UtilsToken.createPositionList(3);
        List<Redundancy> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 8, 5 },
                0);

        assertRedundancySize(8, listeRedondance.get(0));
        assertRedundancySize(5, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondancePlusGrandPuisPlusPetit() throws Exception {

        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(4);
        List<Redundancy> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 2, 5, 3 },
                0);

        assertRedundancySize(2, listeRedondance.get(0));
        assertRedundancySize(5, listeRedondance.get(1));
        assertRedundancySize(3, listeRedondance.get(2));
        assertEquals(3, listeRedondance.size());
    }

    @Test
    public void testMin() throws Exception {
        ManagerToken managerToken = new ManagerToken(Collections.<Token> emptyList());
        assertEquals(5, managerToken.min(new int[] { 5 }, 0, 0));
        assertEquals(5, managerToken.min(new int[] { 5, 6, 7, 8 }, 0, 3));
        assertEquals(5, managerToken.min(new int[] { 5, 6, 7, 8 }, 0, 1));

        assertEquals(5, managerToken.min(new int[] { 8, 7, 6, 5 }, 0, 3));
        assertEquals(4, managerToken.min(new int[] { 5, 8, 4, 7 }, 0, 3));
    }

    @Test
    public void testSupprimerDoublonListeVide() {
        ManagerToken managerToken = new ManagerToken(Collections.<Token> emptyList());
        List<Redundancy> listeRedondance = new ArrayList<Redundancy>();
        List<Redundancy> listeObtenue = managerToken.supprimerDoublon(listeRedondance);
        assertEquals(true, listeObtenue.isEmpty());
    }
    

    @Test
    public void should_delete_code_between_redundancies_when_2_redundancies() throws Exception {
        
        Code code = new Code(createUnmodifiableTokenList(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                "A", "B", "C", "D", "E", "X", "Y", "F", "G", "H", "I", "Z"));
        ManagerToken managerToken = new ManagerToken(code);
        Configuration configuration = new Configuration()
                .withTailleMin(3);
        List<Redundancy> redundancy = managerToken.getRedondance(configuration);
        
        assertThat(redundancy).hasSize(2);
        Redundancy red1 = redundancy.get(0);
        Redundancy red2 = redundancy.get(1);
        display(red1);
        display(red2);
        // Remove in reverse order to keep same values of token index.
        display(code);
        Code removeFromTo = removeFromCode(code, red1, red2, 1);
        display(removeFromTo);
        removeFromTo = removeFromCode(removeFromTo, red1, red2, 0);
        display(removeFromTo);
        
        ManagerToken managerToken2 = new ManagerToken(removeFromTo);
        List<Redundancy> redundancyList = managerToken2.getRedondance(configuration);
        assertThat(redundancyList).hasSize(1);
        assertThat(redundancyList.get(0).getDuplicatedTokenNumber()).isEqualTo(8);
        assertThat(redundancyList.get(0).getStartRedundancyList()).containsExactly(0, 10);
        
        // We have to reconsitute the orginal code
        fail("to finish");
        
    }

	private void display(Code removeFromTo) {
		System.out.print("Code:");
		removeFromTo.getTokenList().forEach(c -> System.out.print(c.getValeurToken()));
		System.out.println();
	}

	private void display(Redundancy redundancy) {
		System.out.print("Size:" + redundancy.getDuplicatedTokenNumber());
		System.out.print("  Start at:");
		redundancy.getStartRedundancyList().forEach(c -> System.out.print(c.intValue() + " "));
        System.out.println();
	}

    private Code removeFromCode(Code code, Redundancy red1, Redundancy red2, int indexRedundancy) {
        return code.removeFromTo(red1.getStartRedundancyList().get(indexRedundancy)+red1.getDuplicatedTokenNumber(), red2.getStartRedundancyList().get(indexRedundancy)-1);
    }
    
    private void assertRedundancySize(int tailleAttendu, Redundancy redondance) {
        assertEquals(tailleAttendu, redondance.getDuplicatedTokenNumber());
    }

    private Stream<String> tokensMapToPosition(Code code, List<Integer> positionList) {
        return positionList.stream().map(p -> code.getToken(p).getValeurToken());
    }

    private Code createCodeWith(String... tokenValues) {
        return new Code(createUnmodifiableTokenList(tokenValues));
    }

    private List<Integer> range(int endExclusive) {
        return IntStream.range(0, endExclusive).boxed().collect(Collectors.toList());
    }

}
