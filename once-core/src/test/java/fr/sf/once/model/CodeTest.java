package fr.sf.once.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

import fr.sf.once.test.UtilsToken;

public class CodeTest {
    @Test
    public void testGetCodeBasic() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A"));
        assertEquals("A", code.getToken(0).getTokenValue());
    }

    @Test
    public void testGetCode() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D"));
        assertEquals("A", code.getToken(0).getTokenValue());
        assertEquals("B", code.getToken(1).getTokenValue());
        assertEquals("C", code.getToken(2).getTokenValue());
        assertEquals("D", code.getToken(3).getTokenValue());
    }

    @Test
    public void testTokenListUnmodifiable() {
        // On rend la liste modifiable pour les besoins du test
        List<Token> tokenList = new ArrayList<Token>(UtilsToken.createUnmodifiableTokenList("A"));
        Code code = new Code(tokenList);
      
        assertEquals(1, code.getTokenList().size());
        tokenList.clear();
        assertEquals(1, code.getTokenList().size());

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
        assertEquals(1, new Code(UtilsToken.createUnmodifiableTokenList("A")).getSize());
        assertEquals(4, new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D")).getSize());
    }

    @Test
    public void testGetMethodListUnmodifiable() {
        List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        methodList.add(new MethodLocalisation(null, null, null));
        Code code = new Code(Collections.<Token> emptyList(), methodList);

        assertEquals(1, code.getMethodList().size());
        methodList.clear();
        assertEquals(1, code.getMethodList().size());

        boolean isException = false;
        try {
            code.getMethodList().add(null);
        } catch (Exception e) {
            isException = true;
        }
        assertTrue("Une exception aurait du être levée", isException);

    }

    /**
     * Vérifie la récupération de la liste des méthodes.
     */
    @Test
    public void testGetMethodList() {
        List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        methodList.add(new MethodLocalisation("methodA", null, null));
        methodList.add(new MethodLocalisation("methodB", null, null));
        Code code = new Code(Collections.<Token> emptyList(), methodList);

        assertEquals("methodA", code.getMethodList().get(0).getMethodName());
        assertEquals("methodB", code.getMethodList().get(1).getMethodName());
    }

    /**
     * Vérifie la récupération d'une méthode par position du token.
     */
    @Test
    public void testGetMethodAtTokenPosition() {
        List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        methodList.add(new MethodLocalisation("methodA", new IntRange(2, 5)));
        methodList.add(new MethodLocalisation("methodB", new IntRange(8, 15)));
        Code code = new Code(Collections.<Token> emptyList(), methodList);

        assertNull(code.getMethodAtTokenPosition(1));
        
        assertEquals("methodA", code.getMethodAtTokenPosition(2).getMethodName());
        assertEquals("methodA", code.getMethodAtTokenPosition(4).getMethodName());
        assertEquals("methodA", code.getMethodAtTokenPosition(5).getMethodName());

        assertNull(code.getMethodAtTokenPosition(6));
        
        assertEquals("methodB", code.getMethodAtTokenPosition(10).getMethodName());
        assertEquals("methodB", code.getMethodAtTokenPosition(15).getMethodName());

        assertNull(code.getMethodAtTokenPosition(16));

    }

    
}
