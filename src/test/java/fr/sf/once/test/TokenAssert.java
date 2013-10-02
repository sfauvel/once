package fr.sf.once.test;

import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

import fr.sf.once.model.Token;

public class TokenAssert extends GenericAssert<TokenAssert, Token> {

    public TokenAssert(Token actual) {
        super(TokenAssert.class, actual);
    }

    public static TokenAssert assertThat(Token actual) {
        return new TokenAssert(actual);
    }

    public TokenAssert hasValue(String expected) {
        String tokenValue = actual.getValeurToken();
        String errorMessage = String.format("Expected token(value to be <%s> but was <%s>", expected, tokenValue);
        Assertions.assertThat(tokenValue.equals(expected)).overridingErrorMessage(errorMessage).isTrue();           
        return this;
    }

}