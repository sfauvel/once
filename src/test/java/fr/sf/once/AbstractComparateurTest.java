package fr.sf.once;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import fr.sf.once.comparator.ComparateurAvecSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.test.UtilsToken;

public abstract class AbstractComparateurTest {

    protected void assertSortList(Integer[] positionListExpected, Code code) {
        ComparateurAvecSubstitution comparator = new ComparateurAvecSubstitution(code);
        List<Integer> positionList = UtilsToken.createPositionList(positionListExpected.length);
        comparator.sortList(positionList);
        assertTokenList(positionListExpected, positionList);
    }
    
    protected void assertTokenList(Integer[] positionListExpected, List<Integer> positionList) {
        assertEquals(positionListExpected.length, positionList.size());
        for (int i = 0; i < positionListExpected.length; i++) {
            assertEquals(positionListExpected[i].intValue(), positionList.get(i).intValue());
        }
    }    

    protected Code creerCode(String... tokenList) {
        return new Code(creerListeTokenListe(tokenList));
    }
    protected List<Token> creerListeTokenListe(String... tokenList) {
        ArrayList<Token> resultatList = new ArrayList<Token>();
        for (String tokenValue : tokenList) {
            int index = resultatList.size();
            resultatList.add(new Token(new Localisation("", index, 0), String.valueOf(tokenValue), Type.VALEUR));
        }
        return resultatList;
    }
    
}