package fr.sf.once.test;

import org.fest.assertions.Assertions;

import fr.sf.once.model.Token;

public class OnceAssertions extends Assertions {
    public static TokenAssert assertThat(Token actual) {
        return new TokenAssert(actual);
    }

}