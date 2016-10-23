package fr.sf.once.core;

import static fr.sf.once.test.UtilsToken.createUnmodifiableTokenList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.AbstractAssert;
import org.junit.ClassRule;
import org.junit.Test;

import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.Redundancy;
import fr.sf.once.test.LogRule;
import fr.sf.once.test.UtilsToken;

public class RedundancyFinderTest {

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();

    /**
     * A A B B 0:1 1 2 2-> 2 1: 1 2 2-> 3 2: 1 1-> 1 3: 1-> 0 B : 1 B B : 1 1 A
     * A B B: 1 1 2 2 A B B : 1 2 2
     */
    @Test
    public void testAfficherRedondance() {
        RedundancyFinder manager = new RedundancyFinder(createCodeWith("A", "A", "B", "B"));

        List<FunctionalRedundancy> listeRedondance = manager.getRedundancies(0);
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
    public void testAjouterRedondanceUneSeuleValeur() throws Exception {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F");
        List<Integer> positionList = UtilsToken.createPositionList(2);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 3 },
                0);

        assertRedondance(3, listeRedondance.get(0));
        assertEquals(1, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondance() throws Exception {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(3);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 3, 5 },
                0);

        assertRedondance(3, listeRedondance.get(0));
        assertRedondance(5, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
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
    public void testAjouterRedondanceToujoursEgal() throws Exception {
        RedundancyFinder managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(6);
        List<Redundancy> listeRedondance = managerToken.computeRedundancy(
                positionList,
                new int[] { 5, 5, 5, 5 },
                0);

        assertRedondance(5, listeRedondance.get(0));
        assertEquals(1, listeRedondance.size());
    }

    @Test
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
    public void testMin() throws Exception {
        RedundancyFinder managerToken = new RedundancyFinder(new Code());
        assertEquals(5, managerToken.min(new int[] { 5 }, 0, 0));
        assertEquals(5, managerToken.min(new int[] { 5, 6, 7, 8 }, 0, 3));
        assertEquals(5, managerToken.min(new int[] { 5, 6, 7, 8 }, 0, 1));

        assertEquals(5, managerToken.min(new int[] { 8, 7, 6, 5 }, 0, 3));
        assertEquals(4, managerToken.min(new int[] { 5, 8, 4, 7 }, 0, 3));

        assertEquals(5, managerToken.min(new int[] { 5, 8, 4, 7 }, 0, 1));
    }

    @Test
    public void testRemoveRedundancyIncludedInAnotherOneListeVide() {
        RedundancyFinder managerToken = new RedundancyFinder(new Code());
        List<Redundancy> listeRedondance = new ArrayList<Redundancy>();
        List<Redundancy> listeObtenue = managerToken.removeRedundancyIncludedInAnotherOne(listeRedondance);
        assertEquals(true, listeObtenue.isEmpty());
    }

    private Code createCodeWith(String... tokenValues) {
        return new Code(createUnmodifiableTokenList(tokenValues));
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
