/**
 * 
 */
package fr.sf.once.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.ast.TokenJava;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;

public class ComparateurWithSubstitutionAndType extends CodeComparator {

    public static final Logger LOG = Logger.getLogger(ComparateurWithSubstitutionAndType.class);

    static final List<Token> notSubstitutableTokenList = Arrays.asList(
            TokenJava.TRAVEL_LIST,
            TokenJava.OPENING_PARENTHESIS,
            TokenJava.CLOSING_PARENTHESIS,
            TokenJava.OPENING_BRACE,
            TokenJava.CLOSING_BRACE,
            TokenJava.OPENING_ARRAY,
            TokenJava.CLOSING_ARRAY,
            TokenJava.ENDING_STATEMENT,
            TokenJava.PARAMETER_SEPARATOR,
            TokenJava.NEW);

    private static SubstitutionTokenListWithBasicArray substitutionListRef = new SubstitutionTokenListWithBasicArray();

    static {
        for (Token token : notSubstitutableTokenList) {
            substitutionListRef.getPosition(token);
        }
    }

    private SubstitutionTokenListWithBasicArray substitutionList1 = new SubstitutionTokenListWithBasicArray(substitutionListRef);
    private SubstitutionTokenListWithBasicArray substitutionList2 = new SubstitutionTokenListWithBasicArray(substitutionListRef);

    public ComparateurWithSubstitutionAndType(Code code) {
        super(code);
    }

    @Override
    protected void reinit() {
        super.reinit();
        substitutionList1.reinit();
        substitutionList2.reinit();
    }

    @Override
    public int compareTokenValue(Token token1, Token token2) {
        int substitutionPostion1 = substitutionList1.getPosition(token1);
        int substitutionPosition2 = substitutionList2.getPosition(token2);
        return substitutionPostion1 - substitutionPosition2;
    }

}