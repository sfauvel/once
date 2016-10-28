/**
 * 
 */
package fr.sf.once.comparator;

import org.apache.log4j.Logger;

import fr.sf.once.model.Code;
import fr.sf.once.model.Token;

/**
 * A comparator that allow subsitutions to all token except syntax element.
 * The list of non substituable token:
 * ":", "(", ")", "{", "}", "[", "]", ";", "new", "."
 */
public class ComparatorWithSubstitution extends CodeComparator {

    public static final Logger LOG = Logger.getLogger(ComparatorWithSubstitution.class);

    private SubstitutionStringList substitutionList1 = new SubstitutionStringList();
    private SubstitutionStringList substitutionList2 = new SubstitutionStringList();
   
    public ComparatorWithSubstitution(Code code) {
        super(code);
    }
    
    @Override
    protected void reinit() {
        super.reinit();
        substitutionList1 = new SubstitutionStringList();
        substitutionList2 = new SubstitutionStringList();
        addNotSubtitutableCharacters(":", "(", ")", "{", "}", "[", "]", ";", "new", ".");
   
    }

    private void addNotSubtitutableCharacters(String... listeToken) {
        for (String token : listeToken) {
            substitutionList1.getPosition(token);
            substitutionList2.getPosition(token);
        }
    }

    @Override
    public int compareTokenValue(Token token1, Token token2) {
        int substitutionPosition1 = substitutionList1.getPosition(token1.getTokenValue());
        int substitutionPosition2 = substitutionList2.getPosition(token2.getTokenValue());
        return substitutionPosition1 - substitutionPosition2;
    }
   }