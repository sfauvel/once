package fr.sf.once.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;


public class MethodDefinitionTest {

    @Test
    public void token_number_give_5_when_method_contains_5_token() throws Exception {
        assertThat(new MethodDefinition("", "", 15, 19).tokenNumber()).isEqualTo(5);
    }
}
