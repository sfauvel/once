package fr.sf.once.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;

import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import junit.framework.Assert;

public class AssertToken {
    private static final int TAB = 2;
    private List<? extends Token> tokenList;
    private int currentPosition = 0;

    public AssertToken(List<? extends Token> tokenList) {
        this.tokenList = tokenList;
    }

    public AssertToken hasNewLine() {
       currentLine++;
        return this;
    }

    private int currentLine = 1;
    private int currentTab = 1;
    public AssertToken fromLine(int line) {
        currentPosition = getFirstTokenPositionOnLine(line);
        currentLine = line;
        return this;
    }

    private int getFirstTokenPositionOnLine(int line) {
        for (int tokenPosition = 0; tokenPosition < tokenList.size(); tokenPosition++) {
            if (tokenList.get(tokenPosition).getLigneDebut() == line) {
                return tokenPosition;
            }
        }
        Assertions.fail("No token found on line " + line);
        throw null;
    }
    public AssertToken indent() {
        return indent(TAB);
    }
    public AssertToken indent(int tab) {
        currentTab += tab;
        return this;
    }
    public AssertToken unindent() {
        return unindent(TAB);
    }
    public AssertToken unindent(int tab) {            
        return indent(-tab);
    }
    public AssertToken hasTokens(String... tokens) {
        return hasTokens(currentTab, tokens);
    }

    private AssertToken hasTokens(int currentColumn, String... tokens) {
       
        for (String token : tokens) {
           System.out.println(token + " " + currentColumn);
            if (!ParcoursAstTest.__.equals(token)) {
                hasToken(token.trim(), currentLine, currentColumn);
            }
            if (!isTechnicalToken(token)) {
                currentColumn += token.length();
            }
        } 
        currentLine++;
        return this;
    }

    private boolean isTechnicalToken(String token) {
        return token.equals(TokenJava.METHOD_LIMIT.getValeurToken());
    }

    public AssertToken hasToken(String token, int line, int column) {        
        return assertToken(currentPosition, token, line, column);
    }

    public AssertToken assertNextToken(String token, Type type, int line, int column) {
        return assertToken(currentPosition, token, type, line, column);
    }
    
    public AssertToken assertToken(int position, String token, Type type, int line, int column) {
        Token tokenJava = tokenList.get(position);
        assertEquals(token, tokenJava.getValeurToken());
        if (type != null) {
            assertEquals(type, tokenJava.getType());
        }
        Localisation localisation = tokenJava.getlocalisation();
        assertThat(localisation.getLigne()).as("Error on line with token '" + token + "'").isEqualTo(line);
        assertThat(localisation.getColonne()).as("Error on column with token '" + token + "'").isEqualTo(column);
        currentPosition = position + 1;
        return this;
    }
    
    public AssertToken assertToken(int position, String token, int line, int column) {
        return assertToken(position, token, null, line, column);
    }

}