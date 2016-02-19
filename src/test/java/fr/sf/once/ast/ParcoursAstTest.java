package fr.sf.once.ast;

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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.test.LogRule;

public class ParcoursAstTest {

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();

    public static final Logger LOG = Logger.getLogger(ParcoursAstTest.class);

    public static final String $METHOD_LIMIT = TokenJava.METHOD_LIMIT.getValeurToken();
    public static final String __ = " ";
    public static final String ____ = "  ";
    public static final String TAB = ____;

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
                        .hasTokens("class", __, "MaClasse", "{")
                        .hasTokens("}");
    }

    @Test
    public void testDeclarationClassExtendPostion() throws Exception {
        assertCode(
                "class MaClasse extends Base {",
                "}")
                        .hasTokens("class", __, "MaClasse", __, "extends", __, "Base", "{")
                        .hasTokens("}");
    }

    @Test
    public void testDeclarationMethode() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "  }",
                "  public void maMethode(int param1, String param2) {",
                "  }",
                "}")
                        .fromLine(2).indent(2)
                        .hasTokens("public", __, "void", __, "maMethode", "(", ")", $METHOD_LIMIT, __, "{")
                        .hasTokens("}", $METHOD_LIMIT)
                        .hasTokens("public", __, "void", __, "maMethode", "(", "int", __, "param1", ",", __, "String", __, "param2", ")", $METHOD_LIMIT, __,
                                "{")
                        .hasTokens("}", $METHOD_LIMIT);
    }

    @Test
    public void testMethodeThrow() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() throws NullPointerException, SQLException {",
                "  }",
                "}")
                        .fromLine(2).indent(2)
                        .hasTokens("public", __, "void", __, "maMethode", "(", ")", __,
                                "throws", __, "NullPointerException", ",", __, "SQLException", $METHOD_LIMIT, __, "{")
                        .hasTokens("}", $METHOD_LIMIT);
    }

    @Test
    public void testAppelMethode() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    autreMethode(1, 2, 3);",
                "  }",
                "}")
                        .fromLine(2)
                        .indent().hasTokens("public", __, "void", __, "maMethode", "(", ")", $METHOD_LIMIT, __, "{")
                        .indent().hasTokens("autreMethode", "(", "1", ",", __, "2", ",", __, "3", ")", ";")
                        .unindent().hasTokens("}", $METHOD_LIMIT);
    }

    @Test
    public void testAppelMethodeWithThis() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    autreMethode(1, this, 3);",
                "  }",
                "}")
                        .fromLine(3).indent()
                        .indent().hasTokens("autreMethode", "(", "1", ",", __, "this", ",", __, "3", ")", ";");
    }

    @Test
    public void testAppelMethodeEnchainePosition() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    autreMethode().toString();",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("autreMethode", "(", ")", ".", "toString", "(", ")", ";");
    }

    @Test
    public void testAppelConstructeur() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    new Classe(1, 2, 3);",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("new", __, "Classe", "(", "1", ",", __, "2", ",", __, "3", ")", ";");
    }

    @Test
    public void testAppelSuper() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    super.maMethode();",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("super", ".", "maMethode", "(", ")", ";");
    }

    @Test
    public void testConstructeur() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public MaClasse() {",
                "  }",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("public", __, "MaClasse", "(", ")", $METHOD_LIMIT, __, "{");
    }

    @Test
    public void testConstructorWithoutModifier() throws Exception {
        assertCode(
                "class MaClasse {",
                "  MaClasse() {",
                "  }",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("MaClasse", "(", ")", $METHOD_LIMIT, __, "{");
    }

    @Test
    public void testConstructorWithSeveralModifiers() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public static MaClasse() {",
                "  }",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("public", __, "static", __, "MaClasse", "(", ")", $METHOD_LIMIT, __, "{");
    }

    @Test
    public void testAffectation() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    int i = 0;",
                "    i = 2;",
                "    int j = init();",
                "    String s = new String();",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        // TODO problème sur la position des espaces avant ou après le égale.
                        .hasTokens("int", __, "i", "=", __, __, "0", ";")
                        .hasTokens("i", "=", __, __, "2", ";")
                        .hasTokens("int", __, "j", "=", __, __, "init", "(", ")", ";")
                        .hasTokens("String", __, "s", "=", __, __, "new", __, "String", "(", ")", ";");
    }

    @Test
    public void testSwitch() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    switch (variable) {",
                "      case CONSTANTE:",
                "        break;",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("switch", "(", __, "variable", ")", "{")
                        .indent()
                        .hasTokens("case", __, "CONSTANTE", ":")
                        .hasTokens(____, "break", ";")
                        .unindent()
                        .hasTokens("}");
    }

    @Test
    public void testIfElse() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    if (var1 && var2) {",
                "    } else {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("if", "(", __, "var1", "&&", __, __, "var2", ")", __, "{")
                        // TODO un problème dans les espaces
                        .hasTokens("}", "else", __, __, "{")
                        .hasTokens("}");
    }

    @Test
    public void testIfElseConditionPosition() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    if ((var1) && var2) {",
                "    } else {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("if", "(", __, "(", "var1", ")", "&&", __, __, "var2", ")", __, "{");
    }

    @Test
    public void testIfElseSurLigneSeule() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    if (variable) {",
                "    }",
                "    else",
                "    {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("if", "(", __, "variable", ")", __, "{")
                        .hasTokens("}", "else")
                        // It's not possible to knom where is the 'else' between the accolade.
                        .hasTokens()
                        .hasTokens("{")
                        .hasTokens("}");
    }

    @Test
    public void testInstanceOf() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    if (variable instanceof String) {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("if", "(", __, "variable", __, "instanceof", __, "String", ")", __, "{")
                        .hasTokens("}");
    }

    @Test
    public void testFor() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    for (int i = 0; i < 10; i++) {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("for", "(", __, "int", __, "i", "=", __, __, "0",
                                ";", __, "i", "<", __, __, "10", ";", __, "i", "++", ")", __, "{")
                        .hasTokens("}");
    }

    @Test
    public void testWhile() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    int i = 0;",
                "    while (i < 10) {",
                "      i++;",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("int", __, "i", "=", __, __, "0", ";")
                        .hasTokens("while", "(", __, "i", "<", __, __, "10", ")", __, "{")
                        .hasTokens(____, "i", "++", ";")
                        .hasTokens("}");
    }

    @Test
    public void testTryCatchFinally() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    try {",
                "    } catch (Exception e) { ",
                "    } finally {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("try", __, "{")
                        .hasTokens("}", __, "catch", "(", __, "Exception", __, "e", ")", __, "{")
                        .hasTokens("}", __, "finally", __, "{")
                        .hasTokens("}");
    }

    @Test
    public void testTryFinally() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    try {",
                "    } finally {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("try", __, "{")
                        .hasTokens("}", __, "finally", __, "{")
                        .hasTokens("}");
    }

    @Test
    public void testTrySeveralCatch() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    try {",
                "    } catch (Exception e) { ",
                "    } catch (TechnicalException e) {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("try", __, "{")
                        .hasTokens("}", __, "catch", "(", __, "Exception", __, "e", ")", __, "{")
                        .hasTokens("}", __, "catch", "(", __, "TechnicalException", __, "e", ")", __, "{")
                        .hasTokens("}");
    }

    @Test
    public void testIncrement() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    int i = 0; ",
                "    i++;",
                "    ++i;",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("int",__,"i","=",__,__,"0",";")
                        .hasTokens("i","++",";")
                        .hasTokens("++","i",";");
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
                .assertToken(10, "return", 3, 5)
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
                "public", "static", "final", "int", "nombre", "=", "2", ";",
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
        assertCode(
                "abstract class MaClasse implements List<String>, Runnable {",
                "}")
                        .hasTokens("abstract", __, "class", __, "MaClasse", __,
                                "implements", __, "List", "<", "String", ">", ",", __, "Runnable", "{");
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
}
