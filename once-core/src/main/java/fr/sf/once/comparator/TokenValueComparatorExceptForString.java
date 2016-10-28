/**
 * 
 */
package fr.sf.once.comparator;

import org.apache.log4j.Logger;

import fr.sf.once.ast.TypeJava;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;


/**
 * Comparator that compare tokan value unless for strings. 
 * A string is equal to all other strings.
 */
public class TokenValueComparatorExceptForString extends CodeComparator {
        
    public static final Logger LOG = Logger.getLogger(TokenValueComparatorExceptForString.class);
    
    public TokenValueComparatorExceptForString(Code code) {
        super(code);
    }
    
    @Override
    public int compareTokenValue(Token token1, Token token2) {
        if (token1.getType().is(TypeJava.STRING) && token2.getType().is(TypeJava.STRING)) {
            return 0;
        }
        return token1.getTokenValue().compareTo(token2.getTokenValue());
        
    }
   
}