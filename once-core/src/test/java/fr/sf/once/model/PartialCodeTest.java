package fr.sf.once.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

import fr.sf.once.test.UtilsToken;

public class PartialCodeTest {

    @Test
    public void should_return_code_token_when_all_code_is_selected() {
        ICode code = new PartialCode(new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D")),
                Arrays.asList(new IntRange(0, 3)));
  
        assertTokenAt(code, 0, "A");
        assertTokenAt(code, 1, "B");
        assertTokenAt(code, 2, "C");
        assertTokenAt(code, 3, "D");

        assertTokenListSize(code, 4);
    }
    
    @Test
    public void should_return_only_token_given_by_the_range_when_range_start_at_0() {
        ICode code = new PartialCode(new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D")),
                Arrays.asList(new IntRange(0, 1)));
        
        assertTokenAt(code, 0, "A");
        assertTokenAt(code, 1, "B");

        assertTokenListSize(code, 2);
    }
    
    @Test
    public void should_return_only_token_given_by_the_range_when_range_not_start_at_0() {
        ICode code = new PartialCode(new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D")),
                Arrays.asList(new IntRange(1, 2)));
        
        assertTokenAt(code, 0, "B");
        assertTokenAt(code, 1, "C");

        assertTokenListSize(code, 2);
    }

    @Test
    public void should_return_only_token_given_by_the_ranges_when_2_ranges() {
        ICode code = new PartialCode(new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G", "H")),
                Arrays.asList(new IntRange(1, 2), new IntRange(4, 6)));
        
        assertTokenAt(code, 0, "B");
        assertTokenAt(code, 1, "C");
        assertTokenAt(code, 2, "E");
        assertTokenAt(code, 3, "F");
        assertTokenAt(code, 4, "G");

        assertTokenListSize(code, 5);
    }
    
    @Test
    public void should_return_original_position() {
        PartialCode code = new PartialCode(new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "E", "F", "G", "H")),
                Arrays.asList(new IntRange(1, 2), new IntRange(4, 6)));
        
        assertEquals(1, code.getOriginalPosition(0));
        assertEquals(4, code.getOriginalPosition(2));
    }
    
    
    
    private void assertTokenAt(ICode code, int position, String expectedValue) {
        assertEquals(expectedValue, code.getToken(position).getTokenValue());
        assertEquals(expectedValue, code.getTokenList().get(position).getTokenValue());
    }
    
    
    private void assertTokenListSize(ICode code, int expectedSize) {
        assertEquals(expectedSize, code.getSize());
        assertEquals(expectedSize, code.getTokenList().size());
    }
    
   
}

