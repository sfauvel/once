package fr.sf.once.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.sf.once.AbstractComparateurTest;
import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparateurAvecSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.test.UtilsToken;

public class ComparateurAvecSubstitutionTest extends AbstractComparateurTest {

    @Before
    public void init() {
    }

    @Test
    public void testGetListeTrieeSansSubstitution() throws Exception {
        // Chaîne(position):abc(1), bc(2), c(3)
        // Substitution: 123, 12, 1
        assertSortList(new Integer[] { 2, 1, 0 }, createCode("a", "b", "c"));
        // Chaîne(position):bac(1), ac(2), c(3)
        // Substitution: 123, 12, 1
        assertSortList(new Integer[] { 2, 1, 0 }, createCode("b", "a", "c"));
    }

    @Test
    public void testGetListeTrieeAvecSubstitution() throws Exception {
        // Chaîne(position):aabb(1), abb(2), bb(3), b(4)
        // Substitution: 1122, 122, 11, 1
        assertSortList(new Integer[] { 3, 2, 0, 1 }, createCode("a", "a", "b", "b"));
    }

    @Test
    public void testCompareBasique() throws Exception {
        CodeComparator comparateur = new ComparateurAvecSubstitution(createCode("a", "b", "c"));
        assertEquals(true, comparateur.compare(1, 2) > 0);
        assertEquals(false, comparateur.compare(2, 1) > 0);
        assertEquals(0, comparateur.compare(1, 1));
    }

    @Test
    public void testCompareIdentiqueSurPlusieursValeurs() throws Exception {
        CodeComparator comparateur = new ComparateurAvecSubstitution(createCode("a", "b", "c", "d", "e", "a", "b", "c", "x", "y"));
        assertEquals(true, comparateur.compare(0, 5) > 0);
        assertEquals(false, comparateur.compare(5, 0) > 0);
    }

    @Test
    public void testComparePatternDifferent() throws Exception {
        CodeComparator comparateur = new ComparateurAvecSubstitution(createCode("a", "a", "b", "b", "c", "c", "d", "e"));
        assertEquals(false, comparateur.compare(0, 4) > 0);
        assertEquals(true, comparateur.compare(4, 0) > 0);
    }

    @Test
    public void testGetRedundancySizeParPosition() throws Exception {
        CodeComparator comparateur = new ComparateurAvecSubstitution(createCode("a", "a", "b", "b", "c", "c", "d", "e"));
        assertEquals(8, comparateur.getRedundancySize(0, 0));
        assertEquals(3, comparateur.getRedundancySize(0, 4));
        assertEquals(1, comparateur.getRedundancySize(0, 3));
    }

    @Test
    public void testGetRedundancySizeWithSpace() throws Exception {
        final Code CODE = createCode(
                "a", "b", "c", "d", "c", "b", "b", "a", ";",
                "A", "B", "C", "A", "C", "A", "A", "B", ";");

        assertEquals(3, new ComparateurAvecSubstitution(CODE).getRedundancySize(0, 9));
        assertEquals(5, new ComparateurAvecSubstitution(CODE).getRedundancySize(4, 13));

        CodeComparator comparateur = new ComparateurAvecSubstitution(CODE);
        assertEquals(3, comparateur.getRedundancySizeWithPreviousSubstitution(0, 9));
        // Only 'c' with 'C'. 'b' doesn't match with 'A'.
        assertEquals(1, comparateur.getRedundancySizeWithPreviousSubstitution(4, 13));
    }

    @Test
    public void testGetRedundancySizeCaractereNonInterchangeable() throws Exception {

        assertEquals(4, getComparator("a", "a", "b", "x", "c", "c", "d", "z").getRedundancySize(0, 4));
        assertEquals(2, getComparator("a", "a", ":", "x", "c", "c", "d", "z").getRedundancySize(0, 4));
        assertEquals(2, getComparator("a", "a", "b", "x", "c", "c", ":", "z").getRedundancySize(0, 4));

        assertEquals(2, getComparator("a", "a", "(", "x", "c", "c", "d", "z").getRedundancySize(0, 4));
        assertEquals(2, getComparator("a", "a", "{", "x", "c", "c", "d", "z").getRedundancySize(0, 4));
        assertEquals(2, getComparator("a", "a", "}", "x", "c", "c", "d", "z").getRedundancySize(0, 4));
        assertEquals(2, getComparator("a", "a", "[", "x", "c", "c", "d", "z").getRedundancySize(0, 4));
        assertEquals(2, getComparator("a", "a", "]", "x", "c", "c", "d", "z").getRedundancySize(0, 4));
        assertEquals(2, getComparator("a", "a", ";", "x", "c", "c", "d", "z").getRedundancySize(0, 4));

        assertEquals(4, getComparator("a", "a", ":", "x", "c", "c", ":", "z").getRedundancySize(0, 4));

    }

    @Test
    public void testGetRedundancySize() throws Exception {

        CodeComparator comparator = new ComparateurAvecSubstitution(createCode("a", "a", "b", "x", "c", "c", "d", "z"));
        assertEquals(4, comparator.getRedundancySize(0, 4));
    }

    @Test
    public void testGetRedundancySizeWithDifference() throws Exception {

        CodeComparator comparator = new ComparateurAvecSubstitution(createCode("a", "b", "a", "x", "c", "d", "e", "z"));
        assertEquals(2, comparator.getRedundancySize(0, 4));
    }

    /**
     * Test que les token de type BREAK stop la redondance.
     * 
     * @throws Exception
     */
    @Test
    public void testGetRedundancySizeWithBreak() throws Exception {

        List<Token> tokenList = creerListeTokenListe("a", "a", "X", "c", "c", "c", "X", "z");
        tokenList.set(2, new Token(new Localisation("", 0, 0), "X", Type.BREAK));
        tokenList.set(6, new Token(new Localisation("", 0, 0), "X", Type.BREAK));
        CodeComparator comparateur = new ComparateurAvecSubstitution(new Code(tokenList));

        // On bloque au niveau de caracactère spécifique de fin.
        assertEquals(2, comparateur.getRedundancySize(0, 4));
    }

    @Test
    public void testCompareWithBreak() throws Exception {
        List<Token> tokenList = creerListeTokenListe("a", "a", "X", "c", "c", "c", "X", "z");
        tokenList.set(2, new Token(new Localisation("", 0, 0), "X", Type.BREAK));
        tokenList.set(6, new Token(new Localisation("", 0, 0), "X", Type.BREAK));
        CodeComparator comparateur = new ComparateurAvecSubstitution(new Code(tokenList));

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

        CodeComparator comparator = new ComparateurAvecSubstitution(createCode("A", "B", "A", "C", "E", "G", "E", "F"));
        assertTrue(comparator.compare(0, 1) < 0); // A B A - B A C | 1 2 1 - 1
                                                  // 2 3
        assertTrue(comparator.compare(2, 3) > 0); // A C E G - C E G E | 1 2 3
                                                  // 4 - 1 2 3 2

    }

    private CodeComparator getComparator(String... tokenList) {
        return new ComparateurAvecSubstitution(createCode(tokenList));
    }
}
