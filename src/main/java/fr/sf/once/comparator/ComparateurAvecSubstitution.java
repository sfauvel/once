/**
 * 
 */
package fr.sf.once.comparator;

import org.apache.log4j.Logger;

import fr.sf.once.core.ListeSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;

public class ComparateurAvecSubstitution extends Comparateur {

    public static final Logger LOG = Logger.getLogger(ComparateurAvecSubstitution.class);

    private ListeSubstitution listeSubstitution1 = new ListeSubstitution();
    private ListeSubstitution listeSubstitution2 = new ListeSubstitution();
   
    public ComparateurAvecSubstitution(Code code) {
        super(code);
    }
    
    @Override
    protected void reinit() {
        super.reinit();
        listeSubstitution1 = new ListeSubstitution();
        listeSubstitution2 = new ListeSubstitution();
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