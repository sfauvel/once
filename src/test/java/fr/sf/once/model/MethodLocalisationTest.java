package fr.sf.once.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

public class MethodLocalisationTest {
    /**
 *
 */
    @Test
    public void testContainsPosition() {
        MethodLocalisation methodLocalisation = new MethodLocalisation("methodA", new IntRange(4, 9));
        assertEquals(false, methodLocalisation.containsPosition(1));
        assertEquals(true, methodLocalisation.containsPosition(4));
        assertEquals(true, methodLocalisation.containsPosition(6));
        assertEquals(true, methodLocalisation.containsPosition(9));
        assertEquals(false, methodLocalisation.containsPosition(10));
    }

    /**
     *
     */
    @Test
    public void testFindMethod() {
        ArrayList<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        methodList.add(new MethodLocalisation("methodA", new IntRange(4, 9)));
        methodList.add(new MethodLocalisation("methodB", new IntRange(15, 34)));
        methodList.add(new MethodLocalisation("methodC", new IntRange(56, 70)));
        

        assertNull(MethodLocalisation.findMethod(methodList, 3));
        
        assertEquals("methodA", MethodLocalisation.findMethod(methodList, 4).getMethodName());
        assertEquals("methodA", MethodLocalisation.findMethod(methodList, 6).getMethodName());
        assertEquals("methodA", MethodLocalisation.findMethod(methodList, 9).getMethodName());

        assertNull(MethodLocalisation.findMethod(methodList, 10));
        
        assertEquals("methodB", MethodLocalisation.findMethod(methodList, 20).getMethodName());
       
        assertNull(MethodLocalisation.findMethod(methodList, 71));
    }
    
}
