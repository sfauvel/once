package fr.sf.once.ast;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.ClassRule;
import org.junit.Test;

import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.test.LogRule;

public class TravelAstTest {

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();

    public static final Logger LOG = Logger.getLogger(TravelAstTest.class);

    public static final String $METHOD_LIMIT = "METHOD BREAK";
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
                        .hasTokens("public", __, "static", __, "MaClasse", "(", ")", $METHOD_LIMIT, __,"{");
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
    public void testTrySeveralExceptionOnSameCatch() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public void maMethode() {",
                "    try {",
                "    } catch (Exception | TechnicalException e) {",
                "    }",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("try", __, "{")
                        .hasTokens("}", __, "catch", "(", __, "Exception", "|", __, __, "TechnicalException", __, "e", ")", __, "{")
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
                        .hasTokens("int", __, "i", "=", __, __, "0", ";")
                        .hasTokens("i", "++", ";")
                        .hasTokens("++", "i", ";");
    }

    @Test
    public void testOperateurComparaison() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean different() {",
                "    return 1 != 2;",
                "  }",
                "  public boolean egal() {",
                "    return 1 == 2; ",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("return", __, "1", "!=", __, __, "2", ";")
                        .fromLine(6)
                        .hasTokens("return", __, "1", "==", __, __, "2", ";");
    }

    @Test
    public void testReturn() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    return true;",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("return", __, "true", ";");
    }

    @Test
    public void testReturnAvecScope() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    return MaClasse.class;",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("return", __, "MaClasse", ".", "class", ";");
    }

    @Test
    public void testCast() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    return (MaClasse) this.getClass();",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("return", __, "(", "MaClasse", ")", __, "this", ".", "getClass", "(", ")", ";");
    }

    @Test
    public void testCastEtAcces() throws Exception {
        assertCode(
                "class MaClasse {",
                "  public boolean maMethode() {",
                "    return ((MaClasse) this.getClass()).toString();",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("return", __, "(", "(", "MaClasse", ")", __, "this", ".", "getClass", "(", ")", ")", ".", "toString", "(", ")", ";");
    }

    @Test
    public void testDeclarationAttribut() throws Exception {
        assertCode(
                "class MaClasse {",
                "  private String attribut;",
                "  public static final int nombre = 2;",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("private", __, "String", __, "attribut", ";")
                        .hasTokens("public", __, "static", __, "final", __, "int", __, "nombre", "=", __, __, "2", ";");
    }

    @Test
    public void testPackage() throws Exception {
        assertCode(
                "package com.orange.ast;",
                "class MaClasse {",
                "}")
                        .hasTokens("package", __, "com", ".", "orange", ".", "ast", ";");
    }

    @Test
    public void testImport() throws Exception {
        assertCode(
                "import com.orange.ast;",
                "class MaClasse {",
                "}")
                        .hasTokens("import", __, "com", ".", "orange", ".", "ast", ";");
    }

    @Test
    public void testExtends() throws Exception {
        assertCode(
                "abstract class MaClasse extends Object {",
                "}")
                        .hasTokens("abstract", __, "class", __, "MaClasse", __, "extends", __, "Object", "{");
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
        assertCode(
                "class MaClasse {",
                "  Class clazz = MaClasse.class;",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("Class", __, "clazz", "=", __, __, "MaClasse", ".", "class", ";");
    }

    @Test
    public void testDeclarationString() throws Exception {
        assertCode(
                "class MaClasse {",
                "  String chaine = \"valeur\";",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("String", __, "chaine", "=", __, __, "\"valeur\"", ";");
    }

    @Test
    public void testDeclarationChar() throws Exception {
        assertCode(
                "class MaClasse {",
                "  char chaine = 'a';",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("char", __, "chaine", "=", __, __, "'a'", ";");
    }

    @Test
    public void testDeclarationPlusieursString() throws Exception {
        assertCode(
                "class MaClasse {",
                "  String chaine = \"valeur\", message = \"vide\";",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("String", __, "chaine", "=", __, __, "\"valeur\"", ",", __, "message", "=", __, __, "\"vide\"", ";");
    }

    @Test
    public void testDeclarationPlusieursStringDansMethode() throws Exception {
        assertCode(
                "class MaClasse {",
                "  void maMethode() {",
                "    String chaine = null, message = null;",
                "  }",
                "}")
                        .fromLine(3).indent().indent()
                        .hasTokens("String", __, "chaine", "=", __, __, "null", ",", __, "message", "=", __, __, "null", ";");
    }

    @Test
    public void testAccesTableau() throws Exception {
        assertCode(
                "class MaClasse {",
                "  String[] chaine = new String[10];",
                "  {",
                "  chaine[2] = 3;",
                "  }",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("String", "[", "]", __, "chaine", "=", __, __, "new", __, "String", "[", "10", "]", ";");
    }

    @Test
    public void testDeclarationTableau() throws Exception {
        assertCode(
                "class MaClasse {",
                "  int[] chaineVide = new int[10];",
                "  int[] chaineInit = new int[] {3};",
                "  int[] chaineMultiple = new int[] {1, 2, 3};",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("int", "[", "]", __, "chaineVide", "=", __, __, "new", __, "int", "[", "10", "]", ";")
                        .hasTokens("int", "[", "]", __, "chaineInit", "=", __, __, "new", __, "int", "[", "]", __, "{", "3", "}", ";")
                        .hasTokens("int", "[", "]", __, "chaineMultiple", "=", __, __, "new", __, "int", "[", "]", __, "{", "1", ",", __, "2", ",", __, "3",
                                "}", ";");
    }

    @Test
    public void testDeclarationTableauMultiDimension() throws Exception {
        assertCode(
                "class MaClasse {",
                "  int[][] chaineVide = new int[10][5];",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("int", "[", "]", "[", "]", __, "chaineVide", "=", __, __, "new", __, "int", "[", "10", "]", "[", "5", "]", ";");
    }

    @Test
    public void testDeclarationNull() throws Exception {
        assertCode(
                "class MaClasse {",
                "  String chaine = null;",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("String", __, "chaine", "=", __, __, "null", ";");
    }

    @Test
    public void testConstructeurStatic() throws Exception {
        assertCode(
                "class MaClasse {",
                "  int valeur;",
                "  static {",
                "    valeur = 2;",
                "  }",
                "}")
                        .fromLine(2).indent()
                        .hasTokens("int", __, "valeur", ";")
                        .hasTokens("static", __, "{")
                        .hasTokens(____, "valeur", "=", __, __, "2", ";")
                        .hasTokens("}");
    }

    private List<? extends Token> extraireToken(String code) throws UnsupportedEncodingException {
        InputStream input = new ByteArrayInputStream(code.getBytes("UTF-8"));
        List<? extends Token> listToken = null;

        try {
            TravelAst parcoursAst = new TravelAst();
            listToken = parcoursAst.extractToken(input, new TokenVisitor());
        } catch (Error t) {
            t.printStackTrace();
            fail(t.getMessage());
        }
        return listToken;
    }

    private AssertToken assertListToken(List<? extends Token> tokenList) {
        afficherListToken(tokenList);
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
            LOG.debug(i + ":" + token.getTokenValue() + "(" + localisation.getLine() + ", " + localisation.getColonne() + ")");

        }
    }
}
