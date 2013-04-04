package fr.sf.once;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.omg.CORBA.IdentifierHelper;

public class RedondanceTest {
    @Test
    public void testContainsWhenRedundancyIsBigger() {
        Redondance referenceRedundancy = new Redondance(5);
        Redondance includedRedundancy = new Redondance(6);
        assertFalse(referenceRedundancy.contains(includedRedundancy));
    }

    @Test
    public void testContainsWhenRedundancyIsSmaller() {
        Redondance referenceRedundancy = new Redondance(5) {
            {
                getFirstTokenList().add(4);
                getFirstTokenList().add(8);
            }
        };

        Redondance includedRedundancy = new Redondance(2) {
            {
                getFirstTokenList().add(4 + 5 - 2);
                getFirstTokenList().add(8 + 5 - 2);
            }
        };
        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testContainsWhenRedundancyWithDifferentOrder() {
        Redondance referenceRedundancy = new Redondance(5) {
            {
                getFirstTokenList().add(4);
                getFirstTokenList().add(8);
            }
        };

        Redondance includedRedundancy = new Redondance(2) {
            {
                getFirstTokenList().add(8 + 5 - 2);
                getFirstTokenList().add(4 + 5 - 2);
            }
        };
        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testContainsWhenMorePosition() {
        Redondance referenceRedundancy = new Redondance(5) {
            {
                getFirstTokenList().add(4);
                getFirstTokenList().add(8);
            }
        };

        Redondance includedRedundancy = new Redondance(2) {
            {
                getFirstTokenList().add(4 + 5 - 2);
                getFirstTokenList().add(8 + 5 - 2);
                getFirstTokenList().add(23 + 5 - 2);
            }
        };
        assertFalse(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testContainsWhenLessPosition() {
        Redondance referenceRedundancy = new Redondance(5) {
            {
                getFirstTokenList().add(4);
                getFirstTokenList().add(8);
                getFirstTokenList().add(12);
            }
        };

        Redondance includedRedundancy = new Redondance(2) {
            {
                getFirstTokenList().add(4 + 5 - 2);
                getFirstTokenList().add(8 + 5 - 2);
            }
        };
        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testSortBySize() {
        List<Redondance> redundancyList = new ArrayList<Redondance>();
        
        redundancyList.add(new Redondance(2));
        redundancyList.add(new Redondance(7));
        redundancyList.add(new Redondance(3));
        redundancyList.add(new Redondance(9));
        redundancyList.add(new Redondance(4));
        
        Redondance.sort(redundancyList);

        assertEquals(9, redundancyList.get(0).getDuplicatedTokenNumber());
        assertEquals(7, redundancyList.get(1).getDuplicatedTokenNumber());
        assertEquals(4, redundancyList.get(2).getDuplicatedTokenNumber());
        assertEquals(3, redundancyList.get(3).getDuplicatedTokenNumber());
        assertEquals(2, redundancyList.get(4).getDuplicatedTokenNumber());
    }
    
    @Test
    public void testGetRedundancyNumber() {
        final int SIZE = 2;
        assertEquals(0, createRedundancy(SIZE).getRedundancyNumber());
        assertEquals(1, createRedundancy(SIZE, 3).getRedundancyNumber());
        assertEquals(4, createRedundancy(SIZE, 6, 8, 9, 34).getRedundancyNumber());
    }
    
    @Test
    public void testRemoveDuplicatedRedundancy() {

        List<Redondance> redundancyList = new ArrayList<Redondance>();
        // On vérifie les inclusions par le nombre de tokens pour faire simple.
        redundancyList.add(createRedundancy(5, 2, 12, 20));
        // Pas les bonnes valeurs
        redundancyList.add(createRedundancy(4, 3, 15, 28));
        // Duplication de redondance
        redundancyList.add(createRedundancy(3, 4, 14, 22));
        // Pas le bon nombre
        redundancyList.add(createRedundancy(2, 5, 15, 23, 30));
        // Pas le même nombre mais inclusion
        redundancyList.add(createRedundancy(1, 6, 24));
        
        Redondance.removeDuplicatedList(redundancyList);
        
        assertEquals(5, redundancyList.get(0).getDuplicatedTokenNumber());
        assertEquals(4, redundancyList.get(1).getDuplicatedTokenNumber());
        assertEquals(2, redundancyList.get(2).getDuplicatedTokenNumber());
        // Ce cas ne devrait pas être présent car il s'agit d'une inclusion
        // Toutefois, cette duplication ne doit pas pouvoir exister. 
        // L'algorithme ne peut pas détecter moins de valeurs.
        assertEquals(1, redundancyList.get(3).getDuplicatedTokenNumber());
        assertEquals(4, redundancyList.size());
        
    }

    /**
     *
     */
    @Test
    public void testGetRedundancyKey() {
        Redondance redondance = new Redondance(4);
        redondance.getFirstTokenList().add(3);
        redondance.getFirstTokenList().add(10);
        assertEquals("7,14,", Redondance.getRedundancyKey(redondance));
        
    }
    
    @Test
    public void testRemoveOverlapRedundancyWithNoOverlap() {
        Redondance redundancy = createRedundancy(5, 2, 12);
        assertEquals(2, redundancy.getFirstTokenList().size());
        redundancy.removeOverlapRedundancy();
        assertEquals(2, redundancy.getFirstTokenList().size());
    }
    
    @Test
    public void testRemoveOverlapRedundancyWithOneOverlap() {
        Redondance redundancy = createRedundancy(5, 2, 4);
        assertEquals(2, redundancy.getFirstTokenList().size());
        redundancy.removeOverlapRedundancy();
        assertEquals(1, redundancy.getFirstTokenList().size());
        assertEquals(4, redundancy.getFirstTokenList().get(0).intValue());
    }
    
    @Test
    public void testRemoveOverlapRedundancyWithSeveralOverlap() {
        Redondance redundancy = createRedundancy(5, 2, 4, 8, 11, 14);
        assertEquals(5, redundancy.getFirstTokenList().size());
        redundancy.removeOverlapRedundancy();
        assertEquals(3, redundancy.getFirstTokenList().size());
        assertEquals(2, redundancy.getFirstTokenList().get(0).intValue());
        assertEquals(8, redundancy.getFirstTokenList().get(1).intValue());
        assertEquals(14, redundancy.getFirstTokenList().get(2).intValue());
    }
    
    @Test
    public void testRemoveOverlapRedundancyLimit() {
        Redondance redundancyWithOverlap = createRedundancy(5, 2, 6);
        redundancyWithOverlap.removeOverlapRedundancy();
        assertEquals(1, redundancyWithOverlap.getFirstTokenList().size());
        
        Redondance redundancyWithoutOverlap = createRedundancy(5, 2, 7);
        redundancyWithoutOverlap.removeOverlapRedundancy();
        assertEquals(2, redundancyWithoutOverlap.getFirstTokenList().size());
    }
    
    private Redondance createRedundancyThatContains(final int redundancySize, final int indentifiedRedundancy) {
        return new Redondance(redundancySize) {
            @Override
            public boolean containsWithSortedRedundancy(Redondance includedRedundancy) {
                return includedRedundancy.getDuplicatedTokenNumber() == indentifiedRedundancy;
            }
            
        };
    }
    
    private Redondance createRedundancy(final int redundancySize, final Integer... firstTokenList) {
         Redondance redondance = new Redondance(redundancySize);
         redondance.getFirstTokenList().addAll(Arrays.asList(firstTokenList));
         return redondance;
    }
}
