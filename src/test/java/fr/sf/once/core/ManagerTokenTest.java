package fr.sf.once.core;

import static fr.sf.once.test.OnceAssertions.assertThat;
import static fr.sf.once.test.UtilsToken.createUnmodifiableTokenList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;

import fr.sf.once.comparator.Comparateur;
import fr.sf.once.comparator.ComparateurAvecSubstitution;
import fr.sf.once.model.Redondance;
import fr.sf.once.model.Token;
import fr.sf.once.test.LogRule;
import fr.sf.once.test.UtilsToken;

public class ManagerTokenTest {
  
    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();

    @Test
    public void testCreateRedondance() {
        ManagerToken manager = new ManagerToken(Collections.<Token> emptyList());

        final int FIRST_POSITION = 1;
        final int SECOND_POSITION = 3;
        Redondance redondance = manager.createRedondance(2, Arrays.asList(FIRST_POSITION, SECOND_POSITION));
        List<Integer> startRedundancyList = redondance.getStartRedundancyList();

        assertThat(startRedundancyList.size())
                .isEqualTo(redondance.getRedundancyNumber())
                .isEqualTo(2);

        assertThat(startRedundancyList.get(0)).isEqualTo(FIRST_POSITION);
        assertThat(startRedundancyList.get(1)).isEqualTo(SECOND_POSITION);
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
     * A A B 0: 1 1 2 -> 1 1: 1 2 -> 2 2: 1 -> 0
     */
    @Test
    public void testTrierListeTokenSansModifierListeOrigine() {

        List<Token> listeTokenOrigine = createUnmodifiableTokenList("A", "A", "B");
        ManagerToken manager = new ManagerToken(listeTokenOrigine);
        Comparateur comparator = new ComparateurAvecSubstitution(manager);

        List<Integer> positionList = Arrays.asList(0, 1, 2);
        manager.sortPositionList(positionList, comparator);
        assertEquals(3, positionList.size());
        UtilsToken.afficher(listeTokenOrigine, positionList);
        assertEquals(2, positionList.get(0).intValue());
        assertEquals(0, positionList.get(1).intValue());
        assertEquals(1, positionList.get(2).intValue());
    }

    /**
     * A E A B A C 0:1 2 1 3 1 4 -> 3 1: 1 2 3 2 4 -> 5 2: 1 2 1 3 -> 2 3: 1 2 3
     * -> 4 4: 1 2 -> 1 5: 1 -> 0
     */
    @Test
    public void testTrierSurPlusieursTokens() {
        List<Token> listeTokenOrigine = createUnmodifiableTokenList("A", "E", "A", "B", "A", "C");
        ManagerToken manager = new ManagerToken(listeTokenOrigine);

        Comparateur comparator = new ComparateurAvecSubstitution(manager);

        List<Integer> positionList = Arrays.asList(0, 1, 2, 3, 4, 5);
        manager.sortPositionList(positionList, comparator);

        assertEquals("C", listeTokenOrigine.get(positionList.get(0)).getValeurToken());

        assertEquals(5, positionList.get(0).intValue());
        assertEquals(4, positionList.get(1).intValue());
        assertEquals(2, positionList.get(2).intValue());
        assertEquals(0, positionList.get(3).intValue());
        assertEquals(3, positionList.get(4).intValue());
        assertEquals(1, positionList.get(5).intValue());
    }

    /**
     * A A B B 0:1 1 2 2-> 2 1: 1 2 2-> 3 2: 1 1-> 1 3: 1-> 0 B : 1 B B : 1 1 A
     * A B B: 1 1 2 2 A B B : 1 2 2
     */
    @Test
    public void testAfficherRedondance() {
        ManagerToken manager = new ManagerToken(createUnmodifiableTokenList("A", "A", "B", "B"));

        List<Redondance> listeRedondance = manager.getRedondance(0);

        Redondance red = listeRedondance.get(0);

        assertRedondance(2, listeRedondance.get(0));
        assertRedondance(1, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondanceUneSeuleValeur() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F");
        List<Integer> positionList = UtilsToken.createPositionList(2);
        List<Redondance> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 3 },
                0);

        assertRedondance(3, listeRedondance.get(0));
        assertEquals(1, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondance() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(3);
        List<Redondance> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 3, 5 },
                0);

        assertRedondance(3, listeRedondance.get(0));
        assertRedondance(5, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondanceToujoursPlusGrand() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(6);
        List<Redondance> listeRedondance = managerToken.calculerRedondance(
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
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(6);
        List<Redondance> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 5, 5, 5, 5 },
                0);

        assertRedondance(5, listeRedondance.get(0));
        assertEquals(1, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondanceToujoursPlusPetit() throws Exception {
        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(3);
        List<Redondance> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 8, 5 },
                0);

        assertRedondance(8, listeRedondance.get(0));
        assertRedondance(5, listeRedondance.get(1));
        assertEquals(2, listeRedondance.size());
    }

    @Test
    public void testAjouterRedondancePlusGrandPuisPlusPetit() throws Exception {

        ManagerToken managerToken = UtilsToken.createManagerToken("A", "B", "C", "D", "E", "F", "G", "H");
        List<Integer> positionList = UtilsToken.createPositionList(4);
        List<Redondance> listeRedondance = managerToken.calculerRedondance(
                positionList,
                new int[] { 2, 5, 3 },
                0);

        assertRedondance(2, listeRedondance.get(0));
        assertRedondance(5, listeRedondance.get(1));
        assertRedondance(3, listeRedondance.get(2));
        assertEquals(3, listeRedondance.size());
    }

    private void assertRedondance(int tailleAttendu, Redondance redondance) {
        assertEquals(tailleAttendu, redondance.getDuplicatedTokenNumber());
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
        List<Redondance> listeRedondance = new ArrayList<Redondance>();
        List<Redondance> listeObtenue = managerToken.supprimerDoublon(listeRedondance);
        assertEquals(true, listeObtenue.isEmpty());
    }

}
