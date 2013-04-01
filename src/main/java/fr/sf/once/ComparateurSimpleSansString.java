/**
 * 
 */
package fr.sf.once;

import org.apache.log4j.Logger;

import fr.sf.once.Token.Type;

/**
 * TODO Gérer les types différement pour permettre l'extension
 * Compare les éléments par leur valeur sauf pour les String. 
 * Une chaîne peut être remplacé par n'importe quoi.
 */
public class ComparateurSimpleSansString extends Comparateur {
    
    public static final Type STRING = new Type();
    
    public static final Logger LOG = Logger.getLogger(ComparateurSimpleSansString.class);
    
    public ComparateurSimpleSansString(Code code) {
        super(code);
    }
    
    @Override
    public int compareTokenValue(Token token1, Token token2) {
        if (token1.getType() == STRING && token2.getType() == STRING) {
            return 0;
        }
        return token1.getValeurToken().compareTo(token2.getValeurToken());
        
    }
   
}