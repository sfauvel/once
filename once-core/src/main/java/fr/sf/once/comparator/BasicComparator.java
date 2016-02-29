/**
 * 
 */
package fr.sf.once.comparator;

import org.apache.log4j.Logger;

import fr.sf.once.model.Code;
import fr.sf.once.model.Token;

/**
 * Simple comparator which compare token only on value.
 */
public class BasicComparator extends CodeComparator {
    public static final Logger LOG = Logger.getLogger(BasicComparator.class);
    
    public BasicComparator(Code code) {
        super(code);
    }
    
    @Override
    public int compareTokenValue(Token token1, Token token2) {
        return token1.getValeurToken().compareTo(token2.getValeurToken());
        
    }
   
}