package fr.sf.once.core;

import static fr.sf.once.test.UtilsToken.createUnmodifiableTokenList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.security.auth.login.Configuration;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Level;
import org.assertj.core.api.AbstractAssert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import fr.sf.once.comparator.BasicComparator;
import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.model.CodeAsATokenList;
import fr.sf.once.model.MethodLocation;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import fr.sf.once.test.LogRule;
import fr.sf.once.test.UtilsToken;

public class RedundancyFinderTest {

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();


    private RedundancyFinderConfiguration getConfigurationWithMinimalTokenNumber(final int number) {
        return new RedundancyFinderConfiguration() {
             public int getMinimalTokenNumberDetection() { return number; }
            @Override public Class<? extends CodeComparator> getCodeComparatorClass() { return BasicComparator.class; };        
        };   
    }
    
    @Test
    public void should_not_find_redundancywhen_there_is_not() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F");

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies).isEmpty();
    }

    @Test
    public void should_find_a_single_redundancy_when_a_sequence_is_duplicated() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken(
                "A", "B", "C",
                "A", "B", "C");

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies).hasSize(1);

        assertThat(redundancies.get(0).getRedundancyNumber()).isEqualTo(2);
        assertThat(redundancies.get(0).getDuplicatedTokenNumber()).isEqualTo(3);
        assertThat(redundancies.get(0).getStartRedundancyList()).containsOnly(0, 3);
    }

    @Test
    public void should_find_a_single_redundancy_when_a_sequence_greater_than_minimal_size_is_duplicated() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken(
                "A", "B", "C", "D", "E",
                "A", "B", "C", "D", "E");

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies).hasSize(1);

        assertThat(redundancies.get(0).getRedundancyNumber()).isEqualTo(2);
        assertThat(redundancies.get(0).getDuplicatedTokenNumber()).isEqualTo(5);
        assertThat(redundancies.get(0).getStartRedundancyList()).containsOnly(0, 5);
    }

    @Test
    public void should_not_find_redundancy_when_size_less_than_the_minima_size() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken(
                "A", "B", "C",
                "A", "B", "C");

        assertThat(managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3))).hasSize(1);
        assertThat(managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(4))).isEmpty();

    }

    @Test
    public void should_find_2_redundancies_when_there_is_2_separated_duplications() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken(
                "A", "B", "C",
                "A", "B", "C",
                "D", "E", "F", "G",
                "D", "E", "F", "G");

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies).hasSize(2);

        assertThat(redundancies).usingElementComparator(new RedundancyComparator())
                .containsOnly(
                        new Redundancy(null, 3, Arrays.asList(0, 3)),
                        new Redundancy(null, 4, Arrays.asList(6, 10)));
    }

    @Test
    public void should_find_2_redundancies_when_there_is_one_deeper_into_one_with_more_redundancies() {

        RedundancyFinder managerToken = UtilsToken.createManagerToken(UtilsToken.createTokenList(
                "A", "B", "C", "BREAK",
                "A", "B", "C", "BREAK",
                "A", "B", "C", "D", "BREAK",
                "A", "B", "C", "D"));

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies).usingElementComparator(new RedundancyComparator())
                .as("%s", redundanciesToString(redundancies))
                .containsOnly(
                        new Redundancy(null, 3, Arrays.asList(0, 4, 8, 13)),
                        new Redundancy(null, 4, Arrays.asList(8, 13)));
    }

    @Test
    public void should_find_2_redundancies_when_there_is_one_deeper_into_one_with_more_redundancies_before_and_after() {

        RedundancyFinder managerToken = UtilsToken.createManagerToken(UtilsToken.createTokenList(
                "A", "B", "C", "BREAK",
                "A", "B", "C", "BREAK",
                "A", "B", "C", "D", "BREAK",
                "A", "B", "C", "D", "BREAK",
                "A", "B", "C", "E", "BREAK"));

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies).usingElementComparator(new RedundancyComparator())
                .as(redundanciesToString(redundancies))
                .containsOnly(
                        new Redundancy(null, 3, Arrays.asList(0, 4, 8, 13, 18)),
                        new Redundancy(null, 4, Arrays.asList(8, 13)));
    }

    private String redundanciesToString(List<Redundancy> redundancies) {
        String result = "";
        String separator = "";
        for (Redundancy redundancy : redundancies) {
            result += separator + redundancy.getDuplicatedTokenNumber() + " => " + redundancy.getStartRedundancyList().toString();
            separator = ", ";
        }
        return result;
    }

    @Test
    public void should_remove_redundancy_when_there_is_only_one_position_after_removing_overlap() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken(
                "A", "B", "C",
                "A", "B", "C",
                "A", "B", "C");

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(5));

        assertThat(redundancies).hasSize(0);
    }

    @Test
    public void should_find_2_redundancy_when_sequence_is_repeated_4_times() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken(
                "A", "B", "C",
                "A", "B", "C",
                "A", "B", "C",
                "A", "B", "C");

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies)
                .usingElementComparator(new RedundancyComparator())
                .as(redundanciesToString(redundancies))
                .containsOnly(
                        new Redundancy(null, 6, Arrays.asList(0, 6)), // A B C A B C
                        new Redundancy(null, 3, Arrays.asList(0, 3, 6, 9))); // A B C
    }

    @Test
    public void should_not_find_redundancies_outside_of_methods() {
        List<Token> tokenList = createUnmodifiableTokenList(
                "A", "B", "C", "D", "E", "BREAK",
                "A", "B", "C", "D", "E", "BREAK");

        List<MethodLocation> methodList = Arrays.asList(
                new MethodLocation("method", new IntRange(2, 5)),
                new MethodLocation("method", new IntRange(6, 11)));
        RedundancyFinder managerToken = new RedundancyFinder(new CodeAsATokenList(tokenList, methodList));
        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(3));

        assertThat(redundancies)
                .usingElementComparator(new RedundancyComparator())
                .as(redundanciesToString(redundancies))
                .containsOnly(
                        new Redundancy(null, 4, Arrays.asList(2, 8)));
    }

    /**
     * A A B B 0:1 1 2 2-> 2 1: 1 2 2-> 3 2: 1 1-> 1 3: 1-> 0 B : 1 B B : 1 1 A
     * A B B: 1 1 2 2 A B B : 1 2 2
     */
    @Test
    public void testAfficherRedondance() {

        LOG_RULE.setTrace(Level.DEBUG);

        RedundancyFinder manager = UtilsToken.createManagerToken("A", "A", "B", "B");

        /// 3 2 0 1
        // B 1
        // B B 1 1
        // A A B B 1 1 2 2
        // A B B 1 2 1
        /// 1 2 1
        RedundancyFinderConfiguration configuration =  new RedundancyFinderConfiguration() {
           @Override public int getMinimalTokenNumberDetection() { return 0; }
           @Override public Class<? extends CodeComparator> getCodeComparatorClass() { return ComparatorWithSubstitution.class; };        
       };   
        
        List<Redundancy> listeRedondance = manager.findRedundancies(configuration);

        assertEquals(2, listeRedondance.size());

        // A A = B B
        assertThatRedundancy(listeRedondance.get(0))
                .hasTokenNumber(2)
                .containsOnly(0, 2);

        // A = B = C = D
        assertThatRedundancy(listeRedondance.get(1))
                .hasTokenNumber(1)
                .containsOnly(0, 1, 2, 3);
    }

    @Test
    public void testAjouterRedondance() throws Exception {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        // List<Integer> positionList = UtilsToken.createPositionList(3);
        List<Integer> positionList = Arrays.asList(1, 8, 16);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 3, 5 },
                0);

        assertEquals(2, listeRedondance.size());
        assertRedondance(3, listeRedondance.get(0));
        assertRedondance(5, listeRedondance.get(1));
    }

    @Test
    @Ignore
    public void testAjouterRedondanceToujoursPlusGrand() throws Exception {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(6);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 2, 4, 6, 7, 8 },
                0);

        assertRedondance(2, listeRedondance.get(0));
        assertRedondance(4, listeRedondance.get(1));
        assertRedondance(6, listeRedondance.get(2));
        assertRedondance(7, listeRedondance.get(3));
        assertRedondance(8, listeRedondance.get(4));
        assertEquals(5, listeRedondance.size());
    }

    @Test
    @Ignore
    public void testAjouterRedondanceToujoursEgal() throws Exception {
        // RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        RedundancyFinder managerToken =
                UtilsToken.createManagerToken("A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A");
        List<Integer> positionList = UtilsToken.createPositionList(6);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 5, 5, 5, 5 },
                0);

        assertThat(listeRedondance.get(0).getDuplicatedTokenNumber()).isEqualTo(5);
        assertThat(listeRedondance).hasSize(1);
    }

    @Test
    @Ignore
    public void testAjouterRedondanceToujoursPlusPetit() throws Exception {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(3);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 8, 5 },
                0);

        assertRedondance(8, listeRedondance.get(0));
        assertRedondance(5, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
    @Ignore
    public void testAjouterRedondancePlusGrandPuisPlusPetit() throws Exception {

        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(4);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 2, 5, 3 },
                0);

        assertRedondance(2, listeRedondance.get(0));
        assertRedondance(5, listeRedondance.get(1));
        assertRedondance(3, listeRedondance.get(2));
        assertEquals(3, listeRedondance.size());
    }

    private void assertRedondance(int tailleAttendu, Redundancy redondance) {
        assertEquals(tailleAttendu, redondance.getDuplicatedTokenNumber());
    }

    @Test
    public void testRemoveRedundancyIncludedInAnotherOneListeVide() {
        RedundancyFinder managerToken = new RedundancyFinder(new CodeAsATokenList());
        List<Redundancy> listeRedondance = new ArrayList<Redundancy>();
        List<Redundancy> listeObtenue = managerToken.removeRedundancyIncludedInAnotherOne(listeRedondance);
        assertEquals(true, listeObtenue.isEmpty());
    }

    @Test
    public void should_remove_a_position_that_overlap_with_another_one_into_a_redundancy() {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "A", "B", "C", "A", "B", "C", "X", "A", "B", "C", "A", "B", "C");

        List<Redundancy> redundancies = managerToken.findRedundancies(getConfigurationWithMinimalTokenNumber(5));

        assertThat(redundancies).hasSize(1);

        assertThat(redundancies.get(0).getRedundancyNumber()).isEqualTo(2);
        // Redundancy on position 0 is removed because it overlaps with one on position 3.
        assertThat(redundancies.get(0).getStartRedundancyList()).containsOnly(0, 10);
    }

    // private Code createCodeWith(String... tokenValues) {
    // List<Token> tokenList = createUnmodifiableTokenList(tokenValues);
    // return new Code(tokenList, Arrays.asList(new MethodLocation("", new IntRange(0, tokenList.size()-1))));
    // }

    private final class RedundancyComparator implements Comparator<Redundancy> {
        @Override
        public int compare(Redundancy redundancyA, Redundancy redundancyB) {

            String s = redundancyA.getStartRedundancyList().toString();

            int tokenNumberCompared = redundancyA.getDuplicatedTokenNumber() - redundancyB.getDuplicatedTokenNumber();
            if (tokenNumberCompared != 0) {
                return tokenNumberCompared;
            }

            int redundancyNumberCompared = redundancyA.getRedundancyNumber() - redundancyB.getRedundancyNumber();
            if (redundancyNumberCompared != 0) {
                return redundancyNumberCompared;
            }

            int startRedundancyCompared = redundancyA.getStartRedundancyList().toString().compareTo(redundancyB.getStartRedundancyList().toString());
            return startRedundancyCompared;
        }
    }

    public static class AbstractRedundancyAssert<S extends AbstractRedundancyAssert<S>> extends AbstractAssert<S, Redundancy> {

        protected AbstractRedundancyAssert(Redundancy actual) {
            super(actual, AbstractRedundancyAssert.class);
        }

        public AbstractRedundancyAssert<S> hasTokenNumber(int number) {
            assertThat(actual.getDuplicatedTokenNumber()).isEqualTo(number);
            return this;
        }

        public AbstractRedundancyAssert<S> containsOnly(Integer... values) {
            assertThat(actual.getStartRedundancyList()).contains(values);
            assertThat(actual.getRedundancyNumber()).isEqualTo(values.length);
            return this;
        }
    }

    public static AbstractRedundancyAssert<?> assertThatRedundancy(Redundancy actual) {
        return new AbstractRedundancyAssert<>(actual);
    }

}
