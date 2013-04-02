package fr.sf.once;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import fr.sf.ast.TypeJava;


public class ComparateurSimpleSansStringTest {
    /**
     *
     */
    @Test
    public void testCompareWithString() {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        tokenList.add(new Token(null, "A", Type.VALEUR));
        tokenList.add(new Token(null, "B", Type.VALEUR));
        tokenList.add(new Token(null, "A", TypeJava.STRING));
        tokenList.add(new Token(null, ";", Type.BREAK));
        tokenList.add(new Token(null, "B", TypeJava.STRING));
        tokenList.add(new Token(null, ";", Type.BREAK));
        
        ComparateurSimpleSansString comparator = new ComparateurSimpleSansString(new Code(tokenList));
        assertEquals(-1, comparator.compare(0, 1));
        assertEquals(0, comparator.compare(2, 4));
    }
}
