/**
 * 
 */
package fr.sf.once;

import org.apache.log4j.Logger;

public class ComparateurSansSubstitution extends Comparateur {
    public static final Logger LOG = Logger.getLogger(ComparateurSansSubstitution.class);
    
    public ComparateurSansSubstitution(Code code) {
        super(code);
    }
    
    @Override
    public int compareTokenValue(Token token1, Token token2) {
        return token1.getValeurToken().compareTo(token2.getValeurToken());
        
    }
   
}