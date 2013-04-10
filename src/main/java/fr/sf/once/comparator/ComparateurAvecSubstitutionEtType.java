/**
 * 
 */
package fr.sf.once.comparator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.ast.TokenJava;
import fr.sf.once.core.ListeSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;

public class ComparateurAvecSubstitutionEtType extends Comparateur {

    public static final Logger LOG = Logger.getLogger(ComparateurAvecSubstitutionEtType.class);

    private ListeSubstitution listeSubstitution1 = new ListeSubstitution();
    private ListeSubstitution listeSubstitution2 = new ListeSubstitution();
   
    public ComparateurAvecSubstitutionEtType(Code code) {
        super(code);
    }
    
    @Override
    protected void reinit() {
        super.reinit();
        listeSubstitution1 = new ListeSubstitution();
        listeSubstitution2 = new ListeSubstitution();
        ajouterCaractereNonSubstituable(":", "(", ")", "{", "}", "[", "]", ";", "new", ".");
   
    }

    static final List<Token> listeTokenNonSubstituable = new ArrayList<Token>() {{
        add(TokenJava.PARCOURS_LISTE);
        add(TokenJava.PARENTHESE_OUVRANTE);
        add(TokenJava.PARENTHESE_FERMANTE);
        add(TokenJava.ACCOLADE_OUVRANTE);
        add(TokenJava.ACCOLADE_FERMANTE);
        add(TokenJava.TABLEAU_OUVRANT);
        add(TokenJava.TABLEAU_FERMANT);
        add(TokenJava.FIN_INSTRUCTION);
        add(TokenJava.SEPARATEUR_PARAMETRE);
        add(TokenJava.NEW);
    }};
    
    private void ajouterCaractereNonSubstituable(String... listeToken) {
        for (String token : listeToken) {
            listeSubstitution1.getPosition(token);
            listeSubstitution2.getPosition(token);
        }
        
        for (Token token : listeTokenNonSubstituable) {
            listeSubstitution1.getPosition(token);
            listeSubstitution2.getPosition(token);
        }
    }

    @Override
    public int compareTokenValue(Token token1, Token token2) {
        int positionSubstitution1 = listeSubstitution1.getPosition(token1);
        int positionSubstitution2 = listeSubstitution2.getPosition(token2);
        return positionSubstitution1 - positionSubstitution2;
    }

}