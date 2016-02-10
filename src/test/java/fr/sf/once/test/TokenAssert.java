package fr.sf.once.test;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import fr.sf.once.model.Token;

public class TokenAssert extends AbstractAssert<TokenAssert, Token> {

    public TokenAssert(Token actual) {
        super(actual, TokenAssert.class);
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