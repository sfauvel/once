package fr.sf.once;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import fr.sf.once.test.UtilsToken;


public class CodeTest {
    @Test
    public void testGetCodeBasic() {
        Code code = new Code(UtilsToken.createTokenList("A"));
        assertEquals("A", code.getToken(0).getValeurToken());
    }

    @Test
    public void testGetCode() {
        Code code = new Code(UtilsToken.createTokenList("A", "B", "C", "D"));
        assertEquals("A", code.getToken(0).getValeurToken());
        assertEquals("B", code.getToken(1).getValeurToken());
        assertEquals("C", code.getToken(2).getValeurToken());
        assertEquals("D", code.getToken(3).getValeurToken());
    }
    
    @Test
    public void testTokenListUnmodifiable() {
        Code code = new Code(UtilsToken.createTokenList("A", "B", "C", "D"));
        boolean isException = false;
        try {
            code.getTokenList().add(null);
        } catch (Exception e) {
            isException = true;
        }
        assertTrue("Une exception aurait du être levée", isException);
    }
    
    /**
     *
     */
    @Test
    public void testCodeSize() {
         assertEquals(0, new Code(Collections.<Token> emptyList()).getSize());
         assertEquals(1, new Code(UtilsToken.createTokenList("A")).getSize());
         assertEquals(4, new Code(UtilsToken.createTokenList("A", "B", "C", "D")).getSize());
    }
}
