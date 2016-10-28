package fr.sf.once.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.sf.once.model.Type;


public class TypeTest {
   
   /**
    *
    */
   @Test
   public void testIs() {
       assertTrue(Type.VALUE.is(Type.VALUE));
       assertFalse(Type.VALUE.is(Type.BREAK));
       assertFalse(Type.VALUE.is(Type.NON_SIGNIFICATIF));
       
   }
   
   @Test
   public void testIsWithExtendedType() {
       final Type EXTENDED_TYPE = new Type("Extended type");
       assertFalse(Type.VALUE.is(EXTENDED_TYPE));
       assertTrue(EXTENDED_TYPE.is(EXTENDED_TYPE));
   }
   
   @Test
   public void testIsWithExtendedExistingType() {
       final Type EXTENDED_TYPE = new Type("Extended type", Type.VALUE);
       assertFalse(Type.BREAK.is(EXTENDED_TYPE));
       assertFalse(Type.VALUE.is(EXTENDED_TYPE));
       assertTrue(EXTENDED_TYPE.is(Type.VALUE));
       assertTrue(EXTENDED_TYPE.is(EXTENDED_TYPE));
   }
}
