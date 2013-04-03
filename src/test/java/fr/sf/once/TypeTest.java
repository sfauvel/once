package fr.sf.once;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class TypeTest {
   
   /**
    *
    */
   @Test
   public void testIs() {
       assertTrue(Type.VALEUR.is(Type.VALEUR));
       assertFalse(Type.VALEUR.is(Type.BREAK));
       assertFalse(Type.VALEUR.is(Type.NON_SIGNIFICATIF));
       
   }
   
   @Test
   public void testIsWithExtendedType() {
       final Type EXTENDED_TYPE = new Type("Extended type");
       assertFalse(Type.VALEUR.is(EXTENDED_TYPE));
       assertTrue(EXTENDED_TYPE.is(EXTENDED_TYPE));
   }
   
   @Test
   public void testIsWithExtendedExistingType() {
       final Type EXTENDED_TYPE = new Type("Extended type", Type.VALEUR);
       assertFalse(Type.BREAK.is(EXTENDED_TYPE));
       assertFalse(Type.VALEUR.is(EXTENDED_TYPE));
       assertTrue(EXTENDED_TYPE.is(Type.VALEUR));
       assertTrue(EXTENDED_TYPE.is(EXTENDED_TYPE));
   }
}
