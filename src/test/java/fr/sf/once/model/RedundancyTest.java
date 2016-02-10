package fr.sf.once.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class RedundancyTest {
    @Test
    public void redundancy_between_5_and_20() {
        Redundancy redundancy = new Redundancy(4).between(5, 20);
        
        assertThat(redundancy.getStartRedundancyList()).containsExactly(5, 20);
    }

    @Test
    public void testContainsWhenRedundancyIsBigger() {
        Redundancy referenceRedundancy = new Redundancy(5);
        Redundancy includedRedundancy = new Redundancy(6);
        assertFalse(referenceRedundancy.contains(includedRedundancy));
    }

    @Test
    public void testContainsWhenRedundancyIsSmaller() {
        Redundancy referenceRedundancy = new Redundancy(5) {
            {
                getStartRedundancyList().add(4);
                getStartRedundancyList().add(8);
            }
        };

        Redundancy includedRedundancy = new Redundancy(2) {
            {
                getStartRedundancyList().add(4 + 5 - 2);
                getStartRedundancyList().add(8 + 5 - 2);
            }
        };
        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testContainsWhenRedundancyWithDifferentOrder() {
        Redundancy referenceRedundancy = new Redundancy(5) {
            {
                getStartRedundancyList().add(4);
                getStartRedundancyList().add(8);
            }
        };

        Redundancy includedRedundancy = new Redundancy(2) {
            {
                getStartRedundancyList().add(8 + 5 - 2);
                getStartRedundancyList().add(4 + 5 - 2);
            }
        };
        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testContainsWhenMorePosition() {
        Redundancy referenceRedundancy = new Redundancy(5) {
            {
                getStartRedundancyList().add(4);
                getStartRedundancyList().add(8);
            }
        };

        Redundancy includedRedundancy = new Redundancy(2) {
            {
                getStartRedundancyList().add(4 + 5 - 2);
                getStartRedundancyList().add(8 + 5 - 2);
                getStartRedundancyList().add(23 + 5 - 2);
            }
        };
        assertFalse(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testContainsWhenLessPosition() {
        Redundancy referenceRedundancy = new Redundancy(5) {
            {
                getStartRedundancyList().add(4);
                getStartRedundancyList().add(8);
                getStartRedundancyList().add(12);
            }
        };

        Redundancy includedRedundancy = new Redundancy(2) {
            {
                getStartRedundancyList().add(4 + 5 - 2);
                getStartRedundancyList().add(8 + 5 - 2);
            }
        };
        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }
    
    @Test
    public void testSortBySize() {
        List<Redundancy> redundancyList = new ArrayList<Redundancy>();
        
        redundancyList.add(new Redundancy(2));
        redundancyList.add(new Redundancy(7));
        redundancyList.add(new Redundancy(3));
        redundancyList.add(new Redundancy(9));
        redundancyList.add(new Redundancy(4));
        
        Redundancy.sort(redundancyList);

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

        List<Redundancy> redundancyList = new ArrayList<Redundancy>();
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
        
        Redundancy.removeDuplicatedList(redundancyList);
        
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
        Redundancy redondance = new Redundancy(4);
        redondance.getStartRedundancyList().add(3);
        redondance.getStartRedundancyList().add(10);
        assertEquals("7,14,", Redundancy.getRedundancyKey(redondance));
        
    }
    
    @Test
    public void testRemoveOverlapRedundancyWithNoOverlap() {
        Redundancy redundancy = createRedundancy(5, 2, 12);
        assertEquals(2, redundancy.getStartRedundancyList().size());
        redundancy.removeOverlapRedundancy();
        assertEquals(2, redundancy.getStartRedundancyList().size());
    }
    
    @Test
    public void testRemoveOverlapRedundancyWithOneOverlap() {
        Redundancy redundancy = createRedundancy(5, 2, 4);
        assertEquals(2, redundancy.getStartRedundancyList().size());
        redundancy.removeOverlapRedundancy();
        assertEquals(1, redundancy.getStartRedundancyList().size());
        assertEquals(4, redundancy.getStartRedundancyList().get(0).intValue());
    }
    
    @Test
    public void testRemoveOverlapRedundancyWithSeveralOverlap() {
        Redundancy redundancy = createRedundancy(5, 2, 4, 8, 11, 14);
        assertEquals(5, redundancy.getStartRedundancyList().size());
        redundancy.removeOverlapRedundancy();
        assertEquals(3, redundancy.getStartRedundancyList().size());
        assertEquals(2, redundancy.getStartRedundancyList().get(0).intValue());
        assertEquals(8, redundancy.getStartRedundancyList().get(1).intValue());
        assertEquals(14, redundancy.getStartRedundancyList().get(2).intValue());
    }
    
    @Test
    public void testRemoveOverlapRedundancyLimit() {
        Redundancy redundancyWithOverlap = createRedundancy(5, 2, 6);
        redundancyWithOverlap.removeOverlapRedundancy();
        assertEquals(1, redundancyWithOverlap.getStartRedundancyList().size());
        
        Redundancy redundancyWithoutOverlap = createRedundancy(5, 2, 7);
        redundancyWithoutOverlap.removeOverlapRedundancy();
        assertEquals(2, redundancyWithoutOverlap.getStartRedundancyList().size());
    }
    
    private Redundancy createRedundancyThatContains(final int redundancySize, final int indentifiedRedundancy) {
        return new Redundancy(redundancySize) {
            @Override
            public boolean containsWithSortedRedundancy(Redundancy includedRedundancy) {
                return includedRedundancy.getDuplicatedTokenNumber() == indentifiedRedundancy;
            }
            
        };
    }
    
    private Redundancy createRedundancy(final int redundancySize, final Integer... firstTokenList) {
         Redundancy redondance = new Redundancy(redundancySize);
         redondance.getStartRedundancyList().addAll(Arrays.asList(firstTokenList));
         return redondance;
    }
}
