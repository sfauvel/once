package fr.sf.once.test;

import fr.sf.once.model.Token;

public class OnceAssertions extends org.fest.assertions.api.Assertions {
    public static TokenAssert assertThat(Token actual) {
        return new TokenAssert(actual);
    }

}