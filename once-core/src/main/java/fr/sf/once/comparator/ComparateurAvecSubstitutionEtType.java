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

public class ComparateurAvecSubstitutionEtType extends CodeComparator {

    public static final Logger LOG = Logger.getLogger(ComparateurAvecSubstitutionEtType.class);

    static final List<Token> listeTokenNonSubstituable = Arrays.asList(
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
        for (Token token : listeTokenNonSubstituable) {
            substitutionListRef.getPosition(token);
        }
    }

    private SubstitutionTokenListWithBasicArray listeSubstitution1 = new SubstitutionTokenListWithBasicArray(substitutionListRef);
    private SubstitutionTokenListWithBasicArray listeSubstitution2 = new SubstitutionTokenListWithBasicArray(substitutionListRef);

    public ComparateurAvecSubstitutionEtType(Code code) {
        super(code);
    }

    @Override
    protected void reinit() {
        super.reinit();
        listeSubstitution1.reinit();
        listeSubstitution2.reinit();
    }

    @Override
    public int compareTokenValue(Token token1, Token token2) {
        int positionSubstitution1 = listeSubstitution1.getPosition(token1);
        int positionSubstitution2 = listeSubstitution2.getPosition(token2);
        return positionSubstitution1 - positionSubstitution2;
    }

}