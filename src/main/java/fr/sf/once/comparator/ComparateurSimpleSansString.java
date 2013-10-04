/**
 * 
 */
package fr.sf.once.comparator;

import org.apache.log4j.Logger;

import fr.sf.once.ast.TypeJava;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;


/**
 * TODO Gérer les types différement pour permettre l'extension
 * Compare les éléments par leur valeur sauf pour les String. 
 * Une chaîne peut être remplacé par n'importe quoi.
 */
public class ComparateurSimpleSansString extends CodeComparator {
        
    public static final Logger LOG = Logger.getLogger(ComparateurSimpleSansString.class);
    
    public ComparateurSimpleSansString(Code code) {
        super(code);
    }
    
    @Override
    public int compareTokenValue(Token token1, Token token2) {
        if (token1.getType().is(TypeJava.STRING) && token2.getType().is(TypeJava.STRING)) {
            return 0;
        }
        return token1.getValeurToken().compareTo(token2.getValeurToken());
        
    }
   
}