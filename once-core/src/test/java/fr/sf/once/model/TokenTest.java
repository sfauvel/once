package fr.sf.once.model;

import static org.junit.Assert.*;

import org.assertj.core.api.Assertions;
import org.junit.Test;



public class TokenTest {
 
    @Test
    public void should_return_10_when_token_has_6_character_and_start_at_4() throws Exception {
        Assertions.assertThat(new Token(new Localisation("", 2, 4), "valeur", Type.VALUE).getColonneFin()).isEqualTo(10);
    }
}
