package fr.sf.once.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.sf.once.test.UtilsToken;

public class RedundancyTest {
    @Test
    public void redundancy_between_5_and_20() {
        Redundancy redundancy = new Redundancy(null, 4, Arrays.asList(5, 20));

        assertThat(redundancy.getStartRedundancyList()).containsExactly(5, 20);
    }

    @Test
    public void creating_a_redundancy_between_2_block_i_retrieve_start_position_of_each_block() {
        final int FIRST_POSITION = 34;
        final int SECOND_POSITION = 65;

        Redundancy redundancy = new Redundancy(null, 1, Arrays.asList(FIRST_POSITION, SECOND_POSITION));

        assertThat(redundancy.getStartRedundancyList()).containsOnly(FIRST_POSITION, SECOND_POSITION);
    }

    @Test
    public void creating_a_redundancy_between_3_block_the_redundancy_number_is_3_and_it_s_equal_to_redundancy_size() {
        Redundancy redundancy = new Redundancy(null, 1, Arrays.asList(11, 22, 33));

        assertThat(redundancy.getRedundancyNumber())
                .isEqualTo(3)
                .isEqualTo(redundancy.getStartRedundancyList().size());
    }

    @Test
    public void creating_a_redundancy_on_7_tokens_the_duplicate_token_number_is_7() {
        final int REDUNDANCY_TOKEN_NUMBER = 3;

        Redundancy redundancy = new Redundancy(null, REDUNDANCY_TOKEN_NUMBER, Arrays.asList(11, 22, 33));

        assertThat(redundancy.getDuplicatedTokenNumber()).isEqualTo(REDUNDANCY_TOKEN_NUMBER);
    }

    @Test
    public void testContainsWhenRedundancyIsBigger() {
        Redundancy referenceRedundancy = createRedundancy(5);
        Redundancy includedRedundancy = createRedundancy(6);
        assertFalse(referenceRedundancy.contains(includedRedundancy));
    }

    @Test
    public void testContainsWhenRedundancyIsSmaller() {
        Redundancy referenceRedundancy = createRedundancy(5, new Integer[] { 4, 8 });
        Redundancy includedRedundancy = createRedundancy(2, new Integer[] { 4 + 5 - 2, 8 + 5 - 2 });

        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }

    @Test
    public void testContainsWhenRedundancyWithDifferentOrder() {
        Redundancy referenceRedundancy = createRedundancy(5, new Integer[] { 4, 8 });
        Redundancy includedRedundancy = createRedundancy(2, new Integer[] { 8 + 5 - 2, 4 + 5 - 2 });

        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }

    @Test
    public void testContainsWhenMorePosition() {
        Redundancy referenceRedundancy = createRedundancy(5, new Integer[] { 4, 8 });
        Redundancy includedRedundancy = createRedundancy(2, new Integer[] { 4 + 5 - 2, 8 + 5 - 2, 23 + 5 - 2 });

        assertFalse(referenceRedundancy.contains(includedRedundancy));
    }

    @Test
    public void testContainsWhenLessPosition() {
        Redundancy referenceRedundancy = createRedundancy(5, new Integer[] { 4, 8, 12 });
        Redundancy includedRedundancy = createRedundancy(2, new Integer[] { 4 + 5 - 2, 8 + 5 - 2 });
        assertTrue(referenceRedundancy.contains(includedRedundancy));
    }

    @Test
    @Ignore
    public void testSortBySize() {
        // List<Redundancy> redundancyList = new ArrayList<Redundancy>();
        //
        // redundancyList.add(createRedundancy(2));
        // redundancyList.add(createRedundancy(7));
        // redundancyList.add(createRedundancy(3));
        // redundancyList.add(createRedundancy(9));
        // redundancyList.add(createRedundancy(4));
        //
        // Redundancy.sort(redundancyList);
        //
        // assertEquals(9, redundancyList.get(0).getDuplicatedTokenNumber());
        // assertEquals(7, redundancyList.get(1).getDuplicatedTokenNumber());
        // assertEquals(4, redundancyList.get(2).getDuplicatedTokenNumber());
        // assertEquals(3, redundancyList.get(3).getDuplicatedTokenNumber());
        // assertEquals(2, redundancyList.get(4).getDuplicatedTokenNumber());
    }

    @Test
    public void testGetRedundancyNumber() {
        final int SIZE = 2;
        assertEquals(0, createRedundancy(SIZE).getRedundancyNumber());
        assertEquals(1, createRedundancy(SIZE, 3).getRedundancyNumber());
        assertEquals(4, createRedundancy(SIZE, 6, 8, 9, 34).getRedundancyNumber());
    }

    @Test
    @Ignore
    public void testRemoveDuplicatedRedundancy() {
        //
        // List<Redundancy> redundancyList = new ArrayList<Redundancy>();
        // // On vérifie les inclusions par le nombre de tokens pour faire simple.
        // redundancyList.add(createRedundancy(5, 2, 12, 20));
        // // Pas les bonnes valeurs
        // redundancyList.add(createRedundancy(4, 3, 15, 28));
        // // Duplication de redondance
        // redundancyList.add(createRedundancy(3, 4, 14, 22));
        // // Pas le bon nombre
        // redundancyList.add(createRedundancy(2, 5, 15, 23, 30));
        // // Pas le même nombre mais inclusion
        // redundancyList.add(createRedundancy(1, 6, 24));
        //
        // Redundancy.removeDuplicatedList(redundancyList);
        //
        // assertEquals(5, redundancyList.get(0).getDuplicatedTokenNumber());
        // assertEquals(4, redundancyList.get(1).getDuplicatedTokenNumber());
        // assertEquals(2, redundancyList.get(2).getDuplicatedTokenNumber());
        // // Ce cas ne devrait pas être présent car il s'agit d'une inclusion
        // // Toutefois, cette duplication ne doit pas pouvoir exister.
        // // L'algorithme ne peut pas détecter moins de valeurs.
        // assertEquals(1, redundancyList.get(3).getDuplicatedTokenNumber());
        // assertEquals(4, redundancyList.size());
    }

    @Test
    public void should_return_7_and_14_when_start_positions_are_3_and_10_and_size_is_4() {
        Redundancy redundancy = createRedundancy(4, new Integer[] { 3, 10 });
        assertThat(redundancy.getEndRedundancyList()).containsOnly(7, 14);
    }

    @Test
    public void should_have_no_substitution_when_all_token_are_identical() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "A", "B", "C", "D"));
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(1, 5));

        assertThat(redundancy.getSubstitutionList()).isEmpty();
    }

    @Test
    public void should_have_one_substitution_when_only_one_token_is_different() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "A", "b", "C", "D"));
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(1, 5));

        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("B", "b");
    }

    @Test
    public void should_have_all_substitutions_when_several_tokens_are_differents() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "a", "b", "c", "d"));
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(0, 4));

        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("A", "a");
        assertThat(redundancy.getSubstitutionList().get(1)).containsOnly("B", "b");
        assertThat(redundancy.getSubstitutionList().get(2)).containsOnly("C", "c");
    }

    @Test
    public void should_no_repeat_a_substitutions_when_there_is_the_same_twice() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "A", "D", "a", "B", "a", "D"));
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(0, 4));

        assertThat(redundancy.getSubstitutionList()).hasSize(1);
        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("A", "a");
    }

    @Test
    public void should_have_3_substitutions_when_redundancy_contains_3_redundancies_on_different_token() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "X", "C", "D", "A", "Y", "C", "D", "A", "Z", "C", "D"));
        Redundancy redundancy = new Redundancy(code, 3, Arrays.asList(0, 4, 8));

        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("X", "Y", "Z");
    }

    @Test
    public void should_not_overlap_when_all_second_redundancy_start_after_all_the_end_of_the_first_one() {
        Redundancy redundancy1 = createRedundancy("..XXX....XXX.....................");
        Redundancy redundancy2 = createRedundancy("................XXX......XXX.....");;

        assertThat(redundancy1.isOverlap(redundancy2)).isFalse();
        assertThat(redundancy2.isOverlap(redundancy1)).isFalse();
    }

    @Test
    public void should_not_overlap_when_token_1_of_the_2_redundancy_is_between_the_2_tokens_of_the__redundancy() {
        Redundancy redundancy1 = createRedundancy("..XXX....XXX.....................");
        Redundancy redundancy2 = createRedundancy(".....XXX...........XXX...........");

        assertThat(redundancy1.isOverlap(redundancy2)).isFalse();
        assertThat(redundancy2.isOverlap(redundancy1)).isFalse();
    }

    @Test
    public void should_overlap_when_one_redundancy_compare_with_itsef() {
        Redundancy redundancy = createRedundancy(".....XXX..........");

        assertThat(redundancy.isOverlap(redundancy)).isTrue();
    }
    
    @Test
    public void should_overlap_when_first_token_is_the_same_between_redundancies() {
        Redundancy redundancy1 = createRedundancy("..XXX....XXX.....................");
        Redundancy redundancy2 = createRedundancy("..XXX...........XXX..............");
        
        assertThat(redundancy1.isOverlap(redundancy2)).isTrue();
        assertThat(redundancy2.isOverlap(redundancy1)).isTrue();
    }
    
    @Test
    public void should_overlap_when_first_token_is_into_the_range_of_the_second_redundancy() {
        Redundancy redundancy1 = createRedundancy("..XXX....XXX.....................");
        Redundancy redundancy2 = createRedundancy("....XXX...........XXX............");
        
        assertThat(redundancy1.isOverlap(redundancy2)).isTrue();
        assertThat(redundancy2.isOverlap(redundancy1)).isTrue();
    }
    
    @Test
    public void should_overlap_when_second_token_range_starts_like_the_second_range_second_redundancies() {
        Redundancy redundancy1 = createRedundancy("..XXX...............XXX.............");
        Redundancy redundancy2 = createRedundancy("......XXX...........XXX............");
        
        assertThat(redundancy1.isOverlap(redundancy2)).isTrue();
        assertThat(redundancy2.isOverlap(redundancy1)).isTrue();
    }
    
    @Test
    public void should_overlap_when_second_token_range_overlaps_between_the_2_redundancies() {
        Redundancy redundancy1 = createRedundancy("..XXX...............XXX...........");
        Redundancy redundancy2 = createRedundancy("......XXX.............XXX............");
        
        assertThat(redundancy1.isOverlap(redundancy2)).isTrue();
        assertThat(redundancy2.isOverlap(redundancy1)).isTrue();
    }
    
    @Test
    public void should_overlap_when_second_token_range_overlaps_with_first_token_range_of_the_other_redundancies() {
        Redundancy redundancy1 = createRedundancy("..XXX...............XXX...........");
        Redundancy redundancy2 = createRedundancy("..................XXX......XXX....");
        
        assertThat(redundancy1.isOverlap(redundancy2)).isTrue();
        assertThat(redundancy2.isOverlap(redundancy1)).isTrue();
    }

    @Test
    public void should_return_method_for_the_first_token_given() {
        //                                       123456789012345678901234567890
        Code code = UtilsToken.initCode("methodA:........................",
                                        "methodB:........................");

        Redundancy redundancy = new Redundancy(code, 6, Arrays.asList(2, 28));
 
        assertThat(redundancy.getMethodAtTokenPosition(2).getMethodName()).isEqualTo("methodA");
        assertThat(redundancy.getMethodAtTokenPosition(5).getMethodName()).isEqualTo("methodA");
        assertThat(redundancy.getMethodAtTokenPosition(7).getMethodName()).isEqualTo("methodA");
        assertThat(redundancy.getMethodAtTokenPosition(28).getMethodName()).isEqualTo("methodB");
    }
    
    @Test
    public void should_return_method_name_even_token_is_outside_redundancy() {
        //                                       123456789012345678901234567890
        Code code = UtilsToken.initCode("methodA:........................",
                                        "methodB:........................");

        Redundancy redundancy = new Redundancy(code, 6, Arrays.asList(2, 28));
 
        assertThat(redundancy.getMethodAtTokenPosition(1).getMethodName()).isEqualTo("methodA");
        assertThat(redundancy.getMethodAtTokenPosition(8).getMethodName()).isEqualTo("methodA");
        assertThat(redundancy.getMethodAtTokenPosition(27).getMethodName()).isEqualTo("methodB");
        assertThat(redundancy.getMethodAtTokenPosition(34).getMethodName()).isEqualTo("methodB");
    }
    
    private Redundancy createRedundancyThatContains(final int redundancySize, final int indentifiedRedundancy) {
        return new Redundancy(null, redundancySize, Collections.emptyList()) {
            @Override
            public boolean containsWithSortedRedundancy(Redundancy includedRedundancy) {
                return includedRedundancy.getDuplicatedTokenNumber() == indentifiedRedundancy;
            }

        };
    }

    private Redundancy createRedundancy(String visualRedundancy) {
        char[] charArray = visualRedundancy.toCharArray();
        List<Integer> firstTokenList = new ArrayList<>();
        int redundancySize = -1;
        int currentSize = 0;
        for (int i = 0; i < charArray.length; i++) {
            boolean isInRedundancy =  charArray[i] != '.';
            boolean justExitFromRedundancy =  i > 0 && charArray[i-1] != '.' && charArray[i] != charArray[i-1];
            boolean justEnterIntoRedundancy =  isInRedundancy && (i==0 || charArray[i] != charArray[i-1]);
            
            if (justExitFromRedundancy) {
                if (redundancySize == -1) {
                    redundancySize = currentSize;
                } else if (charArray[i-1] != '.') {
                    assertThat(currentSize).describedAs("Redundancy size is not the same everywhere").isEqualTo(redundancySize);
                }
                currentSize = 0;
            }
            if (isInRedundancy) {
                currentSize++;
            } 
            if (justEnterIntoRedundancy) {
                firstTokenList.add(i);
            }
        }
        return new Redundancy(null, redundancySize, firstTokenList);
    }
    
    private Redundancy createRedundancy(final int redundancySize, final Integer... firstTokenList) {
        Redundancy redondance = new Redundancy(null, redundancySize, Arrays.asList(firstTokenList));
        return redondance;
    }
}
