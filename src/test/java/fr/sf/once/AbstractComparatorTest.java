package fr.sf.once;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fest.assertions.api.ListAssert;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.test.UtilsToken;

public abstract class AbstractComparatorTest {

    protected void assertSortList(Integer[] positionListExpected, Code code) {
        ComparatorWithSubstitution comparator = new ComparatorWithSubstitution(code);
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

    /**
     * Sort the position list with the comparator and return an object to make assertion.
     * @param comparateur
     * @param positionArray
     * @return
     */
    protected ListAssert<Integer> assertThatSortedPosition(CodeComparator comparateur, Integer... positionArray) {
        List<Integer> positionList = Arrays.asList(positionArray);
        comparateur.sortList(positionList);
        return assertThat(positionList);
    }

    /**
     * @param tokenList List where the token to change is.
     * @param position Token position to change.
     * @param newType New type for the token.
     */
    protected void changeTokenType(List<Token> tokenList, int position, Type newType) {
        Token token = tokenList.get(position);
        tokenList.set(position, new Token(token.getlocalisation(), token.getValeurToken(), newType));
    }    
    
    protected Code createCode(String... tokenList) {
        return new Code(createTokenList(tokenList));
    }

    /**
     * Create a token list from a string array. Each string can contains tokens separates by space.
     * @param stringTokenList
     * @return
     */
    protected List<Token> createTokenList(String... stringTokenList) {
        ArrayList<Token> resultatList = new ArrayList<Token>();
        for (String oneStringToken : stringTokenList) {
            for (String tokenValue : oneStringToken.split(" ")) {
                int index = resultatList.size();
                resultatList.add(new Token(new Localisation("", index, 0), String.valueOf(tokenValue), Type.VALEUR));
            }
        }
        return resultatList;
    }

}