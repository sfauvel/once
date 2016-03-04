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
            TokenJava.PARCOURS_LISTE,
            TokenJava.PARENTHESE_OUVRANTE,
            TokenJava.PARENTHESE_FERMANTE,
            TokenJava.ACCOLADE_OUVRANTE,
            TokenJava.ACCOLADE_FERMANTE,
            TokenJava.TABLEAU_OUVRANT,
            TokenJava.TABLEAU_FERMANT,
            TokenJava.FIN_INSTRUCTION,
            TokenJava.SEPARATEUR_PARAMETRE,
            TokenJava.NEW);

    private static SubstitutionTokenList substitutionListRef = new SubstitutionTokenList();

    static {
        for (Token token : listeTokenNonSubstituable) {
            substitutionListRef.getPosition(token);
        }
    }

    private SubstitutionTokenList listeSubstitution1 = new SubstitutionTokenList();
    private SubstitutionTokenList listeSubstitution2 = new SubstitutionTokenList();

    public ComparateurAvecSubstitutionEtType(Code code) {
        super(code);
    }

    @Override
    protected void reinit() {
        super.reinit();
        listeSubstitution1 = new SubstitutionTokenList(substitutionListRef);
        listeSubstitution2 = new SubstitutionTokenList(substitutionListRef);
    }

    @Override
    public int compareTokenValue(Token token1, Token token2) {
        int positionSubstitution1 = listeSubstitution1.getPosition(token1);
        int positionSubstitution2 = listeSubstitution2.getPosition(token2);
        return positionSubstitution1 - positionSubstitution2;
    }

}