package fr.sf.once.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class UtilsTokenTest {

    @Test
    public void should_1_2_3_when_create_a_range() throws Exception {
          assertThat(UtilsToken.createPositionArray(3)).containsExactly(0,1,2);
    }
}
