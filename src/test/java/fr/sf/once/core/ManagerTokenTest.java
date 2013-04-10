package fr.sf.once.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;

import fr.sf.once.comparator.Comparateur;
import fr.sf.once.comparator.ComparateurAvecSubstitution;
import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.Redondance;
import fr.sf.once.model.Token;
import fr.sf.once.test.LogRule;
import fr.sf.once.test.UtilsToken;

public class ManagerTokenTest {

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();
    

    @Test
    public void testCreateRedondance() {
        ManagerToken manager = new ManagerToken(UtilsToken.createTokenList("A", "B", "C", "D", "E"));
        
        Redondance redondance = manager.createRedondance(2, Arrays.asList(1, 3));
        List<Integer> firstTokenList = redondance.getFirstTokenList();
        assertEquals(2, firstTokenList.size());
        assertEquals("B", manager.getToken(firstTokenList.get(0)).getValeurToken());
        assertEquals("D", manager.getToken(firstTokenList.get(1)).getValeurToken());
    }
    
    @Test
    public void testAjouterToken() {
        List<Token> listeToken = UtilsToken.createTokenList("A", "B");
        ManagerToken manager = new ManagerToken(listeToken);

        assertEquals(2, listeToken.size());
        assertEquals("A", listeToken.get(0).getValeurToken());
        assertEquals("B", listeToken.get(1).getValeurToken());
    }

    /**
     * A A B 0: 1 1 2 -> 1 1: 1 2 -> 2 2: 1 -> 0
     */
    @Test
    public void testTrierListeTokenSansModifierListeOrigine() {
        
        List<Token> listeTokenOrigine = UtilsToken.createTokenList("A", "A", "B");
        ManagerToken manager = new ManagerToken(listeTokenOrigine);

        Comparateur comparator = new ComparateurAvecSubstitution(manager);

        List<Integer> positionList = Arrays.asList(0, 1, 2);
        manager.sortPositionList(positionList , comparator);
        assertEquals(3, positionList.size());
        UtilsToken.afficher(listeTokenOrigine, positionList);
        assertEquals(2, positionList.get(0).intValue());
        assertEquals(0, positionList.get(1).intValue());
        assertEquals(1, positionList.get(2).intValue());
        
        // Vérification que la liste d'origine n'a pas changée.
        assertEquals("A", manager.getToken(0).getValeurToken());
        assertEquals("A", manager.getToken(1).getValeurToken());
        assertEquals("B", manager.getToken(2).getValeurToken());
    }

    /**
     * A E A B A C 0:1 2 1 3 1 4 -> 3 1: 1 2 3 2 4 -> 5 2: 1 2 1 3 -> 2 3: 1 2 3
     * -> 4 4: 1 2 -> 1 5: 1 -> 0
     */
    @Test
    public void testTrierSurPlusieursTokens() {
        List<Token> listeTokenOrigine = UtilsToken.createTokenList("A", "E", "A", "B", "A", "C");
        ManagerToken manager = new ManagerToken(listeTokenOrigine);

        Comparateur comparator = new ComparateurAvecSubstitution(manager);

        List<Integer> positionList = Arrays.asList(0, 1, 2, 3, 4, 5);
        manager.sortPositionList(positionList , comparator);
        
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
        ManagerToken manager = new ManagerToken(UtilsToken.createTokenList("A", "A", "B", "B"));

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
                new int[]  { 2, 5, 3 },
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
