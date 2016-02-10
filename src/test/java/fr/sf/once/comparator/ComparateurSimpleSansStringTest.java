package fr.sf.once.comparator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import fr.sf.once.ast.TypeJava;
import fr.sf.once.model.Code;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;


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
