package fr.sf.once.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

import fr.sf.once.test.UtilsToken;

public class CodeTest {
    @Test
    public void testGetCodeBasic() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A"));
        assertEquals("A", code.getToken(0).getValeurToken());
    }

    @Test
    public void testGetCode() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D"));
        assertEquals("A", code.getToken(0).getValeurToken());
        assertEquals("B", code.getToken(1).getValeurToken());
        assertEquals("C", code.getToken(2).getValeurToken());
        assertEquals("D", code.getToken(3).getValeurToken());
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

    @Test
    public void should_create_a_new_code_without_token_indicated_when_remove_between_2_position() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G"));
        
        Code newCode = code.removeFromTo(3,5);
        List<String> values = newCode.getTokenList().stream().map(t -> t.getValeurToken()).collect(Collectors.toList());
        assertThat(values).containsExactly("A", "B", "C", "G");
    }
    
    @Test
    public void should_create_a_new_code_without_token_indicated_when_remove_between_until_the_end() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G"));
        
        Code newCode = code.removeFromTo(3,6);
        List<String> values = newCode.getTokenList().stream().map(t -> t.getValeurToken()).collect(Collectors.toList());
        assertThat(values).containsExactly("A", "B", "C");
    }
    
    @Test
    public void should_create_a_new_code_without_token_indicated_when_remove_from_the_beginning() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G"));
        
        Code newCode = code.removeFromTo(0,1);
        List<String> values = newCode.getTokenList().stream().map(t -> t.getValeurToken()).collect(Collectors.toList());
        assertThat(values).containsExactly("C", "D", "E", "F", "G");
    }

    @Test
    public void should_return_method_code_when_ask_one_method() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G"),
                Arrays.asList(new MethodLocalisation("org.myMethod", new IntRange(2, 4))));
        
        Code newCode = code.getMethodCode("org.myMethod");
        List<String> values = newCode.getTokenList().stream().map(t -> t.getValeurToken()).collect(Collectors.toList());
        assertThat(values).containsExactly("C", "D");
    }
    
    @Test
    public void should_return_code_of_the_2_methods_when_ask_two_methods() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G", "H"),
                Arrays.asList(new MethodLocalisation("org.myFirstMethod", new IntRange(1, 3)),
                        new MethodLocalisation("org.mySecondMethod", new IntRange(5, 7))));
        
        Code newCode = code.getMethodCode("org.myFirstMethod", "org.mySecondMethod");
        List<String> values = newCode.getTokenList().stream().map(t -> t.getValeurToken()).collect(Collectors.toList());
        assertThat(values).containsExactly("B", "C", "F", "G");
    }
    
    @Test
    public void should_create_a_new_code_without_token_indicated_when_remove_several_int_range() {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G"));
        
        Code newCode = code.removeCode(new IntRange(1,2), new IntRange(4,5));
        List<String> values = newCode.getTokenList().stream().map(t -> t.getValeurToken()).collect(Collectors.toList());
        assertThat(values).containsExactly("A", "D", "G");
    }
}
