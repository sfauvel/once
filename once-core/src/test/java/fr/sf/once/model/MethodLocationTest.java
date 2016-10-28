package fr.sf.once.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

public class MethodLocationTest {
    /**
 *
 */
    @Test
    public void testContainsPosition() {
        MethodLocation methodLocalisation = new MethodLocation("methodA", new IntRange(4, 9));
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
        ArrayList<MethodLocation> methodList = new ArrayList<MethodLocation>();
        methodList.add(new MethodLocation("methodA", new IntRange(4, 9)));
        methodList.add(new MethodLocation("methodB", new IntRange(15, 34)));
        methodList.add(new MethodLocation("methodC", new IntRange(56, 70)));
        

        assertNull(MethodLocation.findMethod(methodList, 3));
        
        assertEquals("methodA", MethodLocation.findMethod(methodList, 4).getMethodName());
        assertEquals("methodA", MethodLocation.findMethod(methodList, 6).getMethodName());
        assertEquals("methodA", MethodLocation.findMethod(methodList, 9).getMethodName());

        assertNull(MethodLocation.findMethod(methodList, 10));
        
        assertEquals("methodB", MethodLocation.findMethod(methodList, 20).getMethodName());
       
        assertNull(MethodLocation.findMethod(methodList, 71));
    }
    
}
