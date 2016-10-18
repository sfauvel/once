/**
 * 
 */
package fr.sf.once.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.sf.once.model.Code;
import fr.sf.once.model.Token;

/**
 * A comparator that allow subsitutions to all token except syntax element.
 * The list of non substituable token:
 * ":", "(", ")", "{", "}", "[", "]", ";", "new", "."
 */
public class ComparatorWithSubstitution extends CodeComparator {

    public static final Logger LOG = Logger.getLogger(ComparatorWithSubstitution.class);

    private SubstitutionStringList listeSubstitution1 = new SubstitutionStringList();
    private SubstitutionStringList listeSubstitution2 = new SubstitutionStringList();
   
    public ComparatorWithSubstitution(Code code) {
        super(code);
    }
    
    @Override
    protected void reinit() {
        super.reinit();
        listeSubstitution1 = new SubstitutionStringList();
        listeSubstitution2 = new SubstitutionStringList();
        ajouterCaractereNonSubstituable(":", "(", ")", "{", "}", "[", "]", ";", "new", ".");
   
    }

    private void ajouterCaractereNonSubstituable(String... listeToken) {
        for (String token : listeToken) {
            listeSubstitution1.getPosition(token);
            listeSubstitution2.getPosition(token);
        }
    }

    @Override
    public int compareTokenValue(Token token1, Token token2) {
        int positionSubstitution1 = listeSubstitution1.getPosition(token1.getValeurToken());
        int positionSubstitution2 = listeSubstitution2.getPosition(token2.getValeurToken());
        return positionSubstitution1 - positionSubstitution2;
    }
   }