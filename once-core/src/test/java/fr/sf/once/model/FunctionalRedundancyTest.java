package fr.sf.once.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import fr.sf.once.test.UtilsToken;

public class FunctionalRedundancyTest {
    @Test
    public void should_have_no_substitution_when_all_token_are_identical() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "A", "B", "C", "D"));
        FunctionalRedundancy redundancy = new FunctionalRedundancy(code, new Redundancy(3, Arrays.asList(1, 5)));

        assertThat(redundancy.getSubstitutionList()).isEmpty();
    }

    @Test
    public void should_have_one_substitution_when_only_one_token_is_different() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "A", "b", "C", "D"));
        FunctionalRedundancy redundancy = new FunctionalRedundancy(code, new Redundancy(3, Arrays.asList(1, 5)));

        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("B", "b");
    }

    @Test
    public void should_have_all_substitutions_when_several_tokens_are_differents() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "C", "D", "a", "b", "c", "d"));
        FunctionalRedundancy redundancy = new FunctionalRedundancy(code, new Redundancy(3, Arrays.asList(0, 4)));

        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("A", "a");
        assertThat(redundancy.getSubstitutionList().get(1)).containsOnly("B", "b");
        assertThat(redundancy.getSubstitutionList().get(2)).containsOnly("C", "c");
    }

    @Test
    public void should_no_repeat_a_substitutions_when_there_is_the_same_twice() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "B", "A", "D", "a", "B", "a", "D"));
        FunctionalRedundancy redundancy = new FunctionalRedundancy(code, new Redundancy(3, Arrays.asList(0, 4)));

        assertThat(redundancy.getSubstitutionList()).hasSize(1);
        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("A", "a");
    }

    @Test
    public void should_have_3_substitutions_when_redundancy_contains_3_redundancies_on_different_token() throws Exception {
        Code code = new Code(UtilsToken.createUnmodifiableTokenList("A", "X", "C", "D", "A", "Y", "C", "D", "A", "Z", "C", "D"));
        FunctionalRedundancy redundancy = new FunctionalRedundancy(code, new Redundancy(3, Arrays.asList(0, 4, 8)));

        assertThat(redundancy.getSubstitutionList().get(0)).containsOnly("X", "Y", "Z");
    }
 
}
