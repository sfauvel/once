package fr.sf.once.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.ClassRule;
import org.junit.Test;

import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.test.LogRule;


public class ParcoursAstTest {

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule(); 
    
    public static final Logger LOG = Logger.getLogger(ParcoursAstTest.class);

    private String __ = " ";
    private String getCode(String... lines) {
        return String.join("\n", Arrays.asList(lines));
    }
    private AssertToken assertCode(String... lines) throws UnsupportedEncodingException {
        return assertListToken(extraireToken(getCode(lines)));
    }

    @Test
    public void testDeclarationClassPostion() throws Exception {
        assertCode( 
                "class MaClasse {",
                "}")
            .hasTokens("class", __ , "MaClasse", "{")
            .hasTokens("}");
    }

    @Test
    public void testDeclarationClassExtendPostion() throws Exception {
        assertCode(
                "class MaClasse extends Base {",
                "}")
            .hasTokens("class", __ , "MaClasse", __ , "extends", __ , "Base", "{")
            .hasTokens("}");
    }
    

    @Test
    public void testDeclarationMethode() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "  }"
                + "  public void maMethode(int param1, String param2) {"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{", "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "public", "void", "maMethode",
                "(", "int", "param1", ",", "String", "param2", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{", "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testDeclarationMethodePosition() throws Exception {
        String code = ""
                + "class MaClasse {\n"
                + "  public void maMethode() {\n"
                + "  }\n"
                + "  public void maMethode(int param1, String param2) {\n"
                + "  }\n"
                + "}\n";
        List<? extends Token> listToken = extraireToken(code);

        afficherListToken(listToken);

        assertListToken(listToken)
                .assertToken(3, "public", 2, 3)
                .hasToken("void", 2, 10)
                .hasToken("maMethode", 2, 14)
                .hasToken("(", 2, 24)
                .hasToken(")", 2, 25);

        assertListToken(listToken)
                .assertToken(12, "public", 4, 3)
                .hasToken("void", 4, 10)
                .hasToken("maMethode", 4, 14)
                .hasToken("(", 4, 24)
                .hasToken("int", 4, 25)
                .hasToken("param1", 4, 29)
                .hasToken(",", 4, 35)
                .hasToken("String", 4, 37)
                .hasToken("param2", 4, 44)
                .hasToken(")", 4, 50);
    }

    class AssertToken {
        private List<? extends Token> tokenList;
        private int currentPosition = -1;

        public AssertToken(List<? extends Token> tokenList) {
            this.tokenList = tokenList;
        }

        public AssertToken hasNewLine() {
           currentLine++;
            return this;
        }

        int currentLine = 0;
        public AssertToken hasTokens(String... tokens) {
            int currentColumn = 1;
            currentLine++;
            for (String token : tokens) {
//                System.out.println(token + " " + currentColumn);
                if (!_.equals(token)) {
                    hasToken(token.trim(), currentLine, currentColumn);
                }
                currentColumn += token.length();
            }
            return this;
        }

        public AssertToken hasToken(String token, int line, int column) {
            currentPosition++;
            return assertToken(currentPosition, token, line, column);
        }

        public AssertToken assertNextToken(String token, Type type, int line, int column) {
            currentPosition++;
            return assertToken(currentPosition, token, type, line, column);
        }
        
        public AssertToken assertToken(int position, String token, Type type, int line, int column) {
            currentPosition = position;
            Token tokenJava = tokenList.get(position);
            assertEquals(token, tokenJava.getValeurToken());
            if (type != null) {
                assertEquals(type, tokenJava.getType());
            }
            Localisation localisation = tokenJava.getlocalisation();
            assertThat(localisation.getLigne()).as("Error on line with token:" + token).isEqualTo(line);
            assertThat(localisation.getColonne()).as("Error on column with token:" + token).isEqualTo(column);
            return this;
        }
        
        public AssertToken assertToken(int position, String token, int line, int column) {
            return assertToken(position, token, null, line, column);
        }

    }

    private AssertToken assertListToken(List<? extends Token> tokenList) {
        return new AssertToken(tokenList);
    }

    private void afficherListToken(List<? extends Token> listToken) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        int listeSize = listToken.size();
        for (int i = 0; i < listeSize; i++) {
            Token token = listToken.get(i);
            Localisation localisation = token.getlocalisation();
            LOG.debug(i + ":" + token.getValeurToken() + "(" + localisation.getLigne() + ", " + localisation.getColonne() + ")");

        }
    }

    @Test
    public void testMethodeThrow() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() throws NullPointerException, SQLException {"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", "throws", "NullPointerException", ",", "SQLException", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testMethodeThrowPosition() throws Exception {
        String code = ""
                + "class MaClasse {\n"
                + "  public void maMethode() throws NullPointerException, SQLException {\n"
                + "  }\n"
                + "}\n";
        List<? extends Token> listToken = extraireToken(code);
        assertListToken(listToken)
                .assertToken(3, "public", 2, 3)
                .hasToken("void", 2, 10)
                .hasToken("maMethode", 2, 14)
                .hasToken("(", 2, 24)
                .hasToken(")", 2, 25)
                .hasToken("throws", 2, 27)
                .hasToken("NullPointerException", 2, 34)
                .hasToken(",", 2, 54)
                .hasToken("SQLException", 2, 56)
                .hasToken(TokenJava.METHOD_LIMIT.getValeurToken(), 2, 69)
                .hasToken("{", 2, 69);
    }

    @Test
    public void testAppelMethode() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    autreMethode(1, 2, 3);"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "autreMethode", "(", "1", ",", "2", ",", "3", ")", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testAppelMethodePosition() throws Exception {
        String code = ""
                + "class MaClasse {\n"
                + "  public void maMethode() {\n"
                + "    autreMethode(1, 2, 3);\n"
                + "  }\n"
                + "}\n";
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
                .assertToken(10, "autreMethode", 3, 5)
                .hasToken("(", 3, 17)
                .hasToken("1", 3, 18)
                .hasToken(",", 3, 19)
                .hasToken("2", 3, 21)
                .hasToken(",", 3, 22)
                .hasToken("3", 3, 24)
                .hasToken(")", 3, 25)
                .hasToken(";", 3, 26);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "autreMethode", "(", "1", ",", "2", ",", "3", ")", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testAppelMethodeWithThis() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    autreMethode(1, this, 3);"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "autreMethode", "(", "1", ",", "this", ",", "3", ")", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testAppelMethodeEnchainePosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public void maMethode() {",
                "    autreMethode().toString();",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
            .assertToken(10, "autreMethode", 3, 5)
            .hasToken("(", 3, 17)
            .hasToken(")", 3, 18)
            .hasToken(".", 3, 19)
            .hasToken("toString", 3, 20)
            .hasToken("(", 3, 28)
            .hasToken(")", 3, 29)
            .hasToken(";", 3, 30);
    }
    
    @Test
    public void testAppelConstructeur() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    new Classe(1, 2, 3);"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "new", "Classe", "(", "1", ",", "2", ",", "3", ")", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testAppelSuper() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    super.maMethode();"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "super", ".", "maMethode", "(", ")", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testConstructeur() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public MaClasse() {"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "MaClasse", "(", ")", "{",
                "}",
                "}");
    }

    @Test
    public void testAffectation() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    int i = 0;"
                + "    i = 2;"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "int", "i", "=", "0", ";",
                "i", "=", "2", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }
    
    @Test
    public void testAffectationPosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public void maMethode() {",
                "    int i = 0;",
                "    i = 2;",
                "    int j = init();",
                "    String s = new String();",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);
        assertListToken(listToken)
            .assertToken(10, "int", 3, 5)
            .hasToken("i", 3, 9)
            .hasToken("=", 3, 10)
            .hasToken("0", 3, 13)
            .hasToken(";", 3, 14)
            .hasToken("i", 4, 5)
            .hasToken("=", 4, 6)
            .hasToken("2", 4, 9)
            .hasToken(";", 4, 10)
            .hasToken("int", 5, 5)
            .hasToken("j", 5, 9)
            .hasToken("=", 5, 10)
            .hasToken("init", 5, 13)
            .hasToken("(", 5, 17)
            .hasToken(")", 5, 18)
            .hasToken(";", 5, 19)
            .hasToken("String", 6, 5)
            .hasToken("s", 6, 12)
            .hasToken("=", 6, 13)
            .hasToken("new", 6, 16)
            .hasToken("String", 6, 20)
            .hasToken("(", 6, 26)
            .hasToken(")", 6, 27)
            .hasToken(";", 6, 28);
    }

    @Test
    public void testSwitch() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    switch (variable) {"
                + "      case CONSTANTE:"
                + "         break;"
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "switch", "(", "variable", ")", "{",
                "case", "CONSTANTE", ":",
                "break", ";",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testIfElse() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public boolean maMethode() {"
                + "    if (variable) {"
                + "    } else {"
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "boolean", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "if", "(", "variable", ")", "{",
                "}", "else", "{",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    /**
     * Le "else" ne peut pas être localisé. Il se trouve entre la fin du bloc
     * "if" et le début du bloc "else". On va prendre la finc du bloc "if" comme
     * reférence.
     * 
     * @throws Exception
     */
    @Test
    public void testIfElsePosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    if (var1 && var2) {",
                "    } else {",
                "    }",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
                .assertToken(10, "if", 3, 5)
                .hasToken("(", 3, 7) // On prend la caractère après le if
                .hasToken("var1", 3, 9)
                .hasToken("&&", 3, 13)
                .hasToken("var2", 3, 17)
                .hasToken(")", 3, 21)
                .hasToken("{", 3, 23)
                .hasToken("}", 4, 5)
                .hasToken("else", 4, 6);
    }

    @Test
    public void testIfElseConditionPosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    if ((var1) && var2) {",
                "    } else {",
                "    }",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
                .assertToken(10, "if", 3, 5)
                .hasToken("(", 3, 7) 
                .hasToken("(", 3, 9)  
                .hasToken("var1", 3, 10)
                .hasToken(")", 3, 14)
                .hasToken("&&", 3, 15)
                .hasToken("var2", 3, 19)
                .hasToken(")", 3, 23)
                .hasToken("{", 3, 25)
                .hasToken("}", 4, 5)
                .hasToken("else", 4, 6);
    }
    
    @Test
    public void testIfElseSurLigneSeulePosition() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public boolean maMethode() {\n"
                + "    if (variable) {\n"
                + "    }\n"
                + "    else\n"
                + "    {\n"
                + "    }\n"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
                .assertToken(10, "if", 2, 5)
                .assertToken(16, "else", 3, 6); // Position de la fin de
                                                // l'accolade
    }

    @Test
    public void testInstanceOf() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public boolean maMethode() {"
                + "    if (variable instanceof String) {"
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "boolean", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "if", "(", "variable", "instanceof", "String", ")", "{",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testFor() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public boolean maMethode() {"
                + "    for (int i = 0; i < 10; i++) {"
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "boolean", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "for", "(", "int", "i", "=", "0", ";", "i", "<", "10", ";", "i", "++", ")", "{",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }
    
    @Test
    public void testForPosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    for (int i = 0; i < 10; i++) {",
                "    }",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
            .assertToken(10, "for", 3, 5)
            .hasToken("(", 3, 8)
            .hasToken("int", 3, 10)
            .hasToken("i", 3, 14)
            .hasToken("=", 3, 15)
            .hasToken("0", 3, 18)
            .hasToken(";", 3, 19)
            .hasToken("i", 3, 21)
            .hasToken("<", 3, 22)
            .hasToken("10", 3, 25)
            .hasToken(";", 3, 27)
            .hasToken("i", 3, 29)
            .hasToken("++", 3, 30)
            .hasToken(")", 3, 32)
            .hasToken("{", 3, 34)
            .hasToken("}", 4, 5);
    }
    @Test
    public void testWhile() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    int i = 0;"
                + "    while (i < 10) {"
                + "       i++;"
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "int", "i", "=", "0", ";",
                "while", "(", "i", "<", "10", ")", "{",
                "i", "++", ";",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testWhilePosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public void maMethode() {",
                "    int i = 0;",
                "    while (i < 10) {",
                "       i++;",
                "    }",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
            .assertToken(15, "while", 4, 5)
            .hasToken("(", 4, 10)
            .hasToken("i", 4, 12)
            .hasToken("<", 4, 13)
            .hasToken("10", 4, 16)
            .hasToken(")", 4, 18)
            .hasToken("{", 4, 20)
            .hasToken("i", 5, 8)
            .hasToken("++", 5, 9)
            .hasToken(";", 5, 11)
            .hasToken("}", 6, 5);
                
    }

    @Test
    public void testTryCatch() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    try {"
                + "    } catch (Exception e) { "
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "try", "{",
                "}", "catch", "(", "Exception", "e", ")", "{",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }
    
    @Test
    public void testTryCatchFinallyPosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public void maMethode() {",
                "    try {",
                "    } catch (Exception e) { ",
                "    } finally {",
                "    }",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
            .assertToken(10, "try", 3, 5)
            .hasToken("{", 3, 9)
            .hasToken("}", 4, 5)
            .hasToken("catch", 4, 7)
            .hasToken("(", 4, 12)
            .hasToken("Exception", 4, 14)
            .hasToken("e", 4, 24)
            .hasToken(")", 4, 25)
            .hasToken("{", 4, 27)
            .hasToken("}", 5, 5)
            .hasToken("finally", 5, 7)
            .hasToken("{", 5, 15)
            .hasToken("}", 6, 5);
    }

    @Test
    public void testTryPlusieursCatch() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    try {"
                + "    } catch (FunctionalException e) { "
                + "    } catch (TechnicalException e) { "
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "try", "{",
                "}", "catch", "(", "FunctionalException", "e", ")", "{",
                "}", "catch", "(", "TechnicalException", "e", ")", "{",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testTryFinally() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    try {"
                + "    } finally {"
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "try", "{",
                "}", "finally", "{",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testTryCatchFinally() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public void maMethode() {"
                + "    try {"
                + "    } catch (Exception e) { "
                + "    } finally {"
                + "    }"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "try", "{",
                "}", "catch", "(", "Exception", "e", ")", "{",
                "}", "finally", "{",
                "}",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testIncrement() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public boolean maMethode() {"
                + "    int i = 0; "
                + "    i++;"
                + "    ++i;"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "boolean", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "int", "i", "=", "0", ";",
                "i", "++", ";",
                "++", "i", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testOperateurComparaison() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public boolean different() {"
                + "    return 1 != 2;"
                + "  }"
                + "  public boolean egal() {"
                + "    return 1 == 2; "
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "boolean", "different", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "return", "1", "!=", "2", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "public", "boolean", "egal", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "return", "1", "==", "2", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testReturn() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public boolean maMethode() {"
                + "    return true;"
                + "  }"
                + "}";
        
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "boolean", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "return", "true", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }
    
    @Test
    public void testReturnPosition() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    return true;",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);
        assertListToken(listToken)
            .assertToken(10, "return",  3,  5)
            .hasToken("true", 3, 12)
            .hasToken(";", 3, 16);
    }

    @Test
    public void testReturnAvecScope() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public Class maMethode() {"
                + "    return MaClasse.class;"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "Class", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "return", "MaClasse", ".", "class", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testCast() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public Class maMethode() {"
                + "    return (MaClasse) this.getClass();"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "Class", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "return", "(", "MaClasse", ")", "this", ".", "getClass", "(", ")", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }
    
    @Test
    public void testCastPostion() throws Exception {
        String code = code(
                "class MaClasse {",
                "  public Class maMethode() {",
                "    return (MaClasse) this;",
                "  }",
                "}");
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
            .assertToken(10, "return", 3, 5)
            .hasToken("(", 3, 12)
            .hasToken("MaClasse", 3, 13)
            .hasToken(")", 3, 21)
            .hasToken("this", 3, 23)
            .hasToken(";", 3, 27);
    }

    @Test
    public void testCastEtAcces() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  public String maMethode() {"
                + "    return ((MaClasse) this.getClass()).toString();"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "public", "String", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "return", "(", "(", "MaClasse", ")", "this", ".", "getClass", "(", ")", ")", ".", "toString", "(", ")", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testDeclarationAttribut() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  private String attribut;"
                + "  public static final int nombre = 2;"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "private", "String", "attribut", ";",
                "public static final", "int", "nombre", "=", "2", ";",
                "}");
    }

    @Test
    public void testPackage() throws Exception {
        String code = ""
                + "package com.orange.ast;"
                + "class MaClasse {"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "package", "com", ".", "orange", ".", "ast", ";",
                "class", "MaClasse", "{",
                "}");
    }

    @Test
    public void testPackagePosition() throws Exception {
        String code = ""
                + "package com.orange.ast;"
                + "class MaClasse {"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
                .hasToken("package", 1, 1)
                .hasToken("com", 1, 9)
                .hasToken(".", 1, 12)
                .hasToken("orange", 1, 13)
                .hasToken(".", 1, 19)
                .hasToken("ast", 1, 20)
                .hasToken(";", 1, 23);
    }

    @Test
    public void testImport() throws Exception {
        String code = ""
                + "import com.orange.ast;"
                + "class MaClasse {"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "import", "com", ".", "orange", ".", "ast", ";",
                "class", "MaClasse", "{",
                "}");
    }

    @Test
    public void testImportPosition() throws Exception {
        String code = ""
                + "import com.orange.ast;"
                + "class MaClasse {"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertListToken(listToken)
                .hasToken("import", 1, 1)
                .hasToken("com", 1, 8)
                .hasToken(".", 1, 11)
                .hasToken("orange", 1, 12)
                .hasToken(".", 1, 18)
                .hasToken("ast", 1, 19)
                .hasToken(";", 1, 22);
    }

    @Test
    public void testExtends() throws Exception {
        String code = ""
                + "abstract class MaClasse extends Object {"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "abstract", "class", "MaClasse", "extends", "Object", "{",
                "}");
    }

    @Test
    public void testImplements() throws Exception {
        String code = ""
                + "abstract class MaClasse implements List<String>, Runnable {"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "abstract", "class", "MaClasse", "implements", "List", "<", "String", ">", ",", "Runnable", "{",
                "}");
    }

    @Test
    public void testAccesClass() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  Class clazz = MaClasse.class;"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "Class", "clazz", "=", "MaClasse", ".", "class", ";",
                "}");
    }

    @Test
    public void testDeclarationString() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  String chaine = \"valeur\";"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "String", "chaine", "=", "\"valeur\"", ";",
                "}");
        

        assertListToken(listToken)
                .assertToken(3, "String", TypeJava.CLASS, 1, 19)
                .assertNextToken("chaine", TypeJava.VARIABLE, 1, 26)
                .hasToken("=", 1, 32)
                .hasToken("\"valeur\"", 1, 35);
    }

    @Test
    public void testDeclarationChar() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  char chaine = 'a';"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "char", "chaine", "=", "'a'", ";",
                "}");
    }

    @Test
    public void testDeclarationPlusieursString() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  String chaine = \"valeur\", message = \"vide\";"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "String", "chaine", "=", "\"valeur\"", ",", "message", "=", "\"vide\"", ";",
                "}");
    }

    @Test
    public void testDeclarationPosition() throws Exception {
        String code = ""
                + "class MaClasse {\n"
                + "  String chaine = appelMethode();\n"
                + "}";
        List<? extends Token> listToken = extraireToken(code);
        assertListToken(listToken)
                .assertToken(3, "String", 2, 3)
                .hasToken("chaine", 2, 10)
                .hasToken("=", 2, 16)
                .hasToken("appelMethode", 2, 19)
                .hasToken("(", 2, 31)
                .hasToken(")", 2, 32)
                .hasToken(";", 2, 33);
    }

    @Test
    public void testDeclarationPlusieursStringDansMethode() throws Exception {
        String code = ""
                + "class MaClasse {"
                + " void maMethode() {"
                + "  String chaine = null, message = null;"
                + " }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "void", "maMethode", "(", ")", TokenJava.METHOD_LIMIT.getValeurToken(), "{",
                "String", "chaine", "=", "null", ",", "message", "=", "null", ";",
                "}", TokenJava.METHOD_LIMIT.getValeurToken(),
                "}");
    }

    @Test
    public void testAccesTableau() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  String[] chaine = new String[10];"
                + "  {"
                + "  chaine[2] = 3;"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "String", "[", "]", "chaine", "=", "new", "String", "[", "10", "]", ";",
                "{",
                "chaine", "[", "2", "]", "=", "3", ";",
                "}",
                "}");
    }

    @Test
    public void testDeclarationTableau() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  int[] chaineVide = new int[10];"
                + "  int[] chaineInit = new int[] {3};"
                + "  int[] chaineMultiple = new int[] {1, 2, 3};"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "int", "[", "]", "chaineVide", "=", "new", "int", "[", "10", "]", ";",
                "int", "[", "]", "chaineInit", "=", "new", "int", "[", "]", "{", "3", "}", ";",
                "int", "[", "]", "chaineMultiple", "=", "new", "int", "[", "]", "{", "1", ",", "2", ",", "3", "}", ";",
                "}");
    }

    @Test
    public void testDeclarationTableauMultiDimension() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  int[][] chaineVide = new int[10][5];"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "int", "[", "]", "[", "]", "chaineVide", "=", "new", "int", "[", "10", "]", "[", "5", "]", ";",
                "}");
    }

    @Test
    public void testDeclarationNull() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  String chaine = null;"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "String", "chaine", "=", "null", ";",
                "}");
    }

    @Test
    public void testConstructeurStatic() throws Exception {
        String code = ""
                + "class MaClasse {"
                + "  int valeur;"
                + "  static {"
                + "     valeur = 2;"
                + "  }"
                + "}";
        List<? extends Token> listToken = extraireToken(code);

        assertToken(listToken,
                "class", "MaClasse", "{",
                "int", "valeur", ";",
                "static", "{",
                "valeur", "=", "2", ";",
                "}",
                "}");
    }

    private void assertToken(List<? extends Token> listToken, String... listeTokenAttendu) {
        Iterator<? extends Token> iterator = listToken.iterator();
        String messageErreur = "Expected:\n" + getListTokenToString(listeTokenAttendu) + "   but was\n" + getListTokenToString(listToken);
        for (String token : listeTokenAttendu) {
            assertTrue(messageErreur, iterator.hasNext());
            assertEquals(messageErreur, token, iterator.next().getValeurToken());
        }
    }

    private String getListTokenToString(String... listToken) {
        StringBuffer buffer = new StringBuffer();
        for (String token : listToken) {
            buffer.append(token)
                    .append(" ");
        }
        buffer.append("\n");
        return buffer.toString();
    }

    private String getListTokenToString(List<? extends Token> listToken) {
        String[] listString = new String[listToken.size()];
        int i = 0;
        for (Token token : listToken) {
            listString[i] = token.getValeurToken();
            i++;
        }
        return getListTokenToString(listString);
    }

    private List<? extends Token> extraireToken(String code) throws UnsupportedEncodingException {
        InputStream input = new ByteArrayInputStream(code.getBytes("UTF-8"));
        List<? extends Token> listToken = null;

        try {
            ParcoursAst parcoursAst = new ParcoursAst();
            listToken = parcoursAst.extraireToken(input, new TokenVisitor());
        } catch (Error t) {
            t.printStackTrace();
            fail(t.getMessage());
        }
        return listToken;
    }

    private String code(String... lineList) {
        return StringUtils.join(lineList, "\n");
    }
}
