package fr.sf.once;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.sf.once.Token.Type;


public class TokenTest {
    /**
     *
     */
    @Test
    public void testname() {
        
        
    }
    
    /**
     *
     */
    @Test
    public void testIsType() {
        Token token = new Token(null, "value", Type.VALEUR);
        assertTrue(token.isType(Type.VALEUR));
        assertFalse(token.isType(Type.BREAK));
        assertFalse(token.isType(Type.NON_SIGNIFICATIF));
        
    }
}
