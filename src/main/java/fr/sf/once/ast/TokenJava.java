package fr.sf.once.ast;

import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;


public class TokenJava extends Token {

    
    
    public static final TokenJava ACCOLADE_OUVRANTE = new TokenJava("{", Type.VALEUR);
    public static final TokenJava ACCOLADE_FERMANTE = new TokenJava("}", Type.VALEUR);
    public static final TokenJava PARENTHESE_OUVRANTE = new TokenJava("(", Type.NON_SIGNIFICATIF);
    public static final TokenJava PARENTHESE_FERMANTE = new TokenJava(")", Type.NON_SIGNIFICATIF);
    public static final TokenJava GENERIQUE_OUVRANTE = new TokenJava("<", Type.NON_SIGNIFICATIF);
    public static final TokenJava GENERIQUE_FERMANTE = new TokenJava(">", Type.NON_SIGNIFICATIF);
    public static final TokenJava TABLEAU_OUVRANT = new TokenJava("[", Type.NON_SIGNIFICATIF);
    public static final TokenJava TABLEAU_FERMANT = new TokenJava("]", Type.NON_SIGNIFICATIF);
    public static final TokenJava SEPARATEUR_PARAMETRE = new TokenJava(",", Type.NON_SIGNIFICATIF);

    public static final TokenJava POINT = new TokenJava(".", Type.NON_SIGNIFICATIF);
    public static final TokenJava FIN_INSTRUCTION = new TokenJava(";", Type.NON_SIGNIFICATIF);
    public static final TokenJava PARCOURS_LISTE = new TokenJava(":", Type.NON_SIGNIFICATIF);

    public static final TokenJava BREAK = new TokenJava("break", Type.NON_SIGNIFICATIF);
    public static final TokenJava CASE = new TokenJava("case");
    public static final TokenJava CASE_SEPARATEUR = new TokenJava(":", Type.NON_SIGNIFICATIF);
    public static final TokenJava FOR = new TokenJava("for");
    public static final TokenJava WHILE = new TokenJava("while");
    public static final TokenJava IF = new TokenJava("if");
    public static final TokenJava ELSE = new TokenJava("else");
    public static final TokenJava NEW = new TokenJava("new");
    public static final TokenJava SWITCH = new TokenJava("switch");
    public static final TokenJava VOID = new TokenJava("void");
    public static final TokenJava INTERFACE = new TokenJava("interface");
    public static final TokenJava CLASS = new TokenJava("class");
    public static final TokenJava RETURN = new TokenJava("return");

    public static final TokenJava AFFECTATION = new TokenJava("=", Type.NON_SIGNIFICATIF);
    public static final TokenJava STATIC = new TokenJava("static");
    public static final TokenJava NULL = new TokenJava("null");
    public static final TokenJava THIS = new TokenJava("this");
    public static final TokenJava SUPER = new TokenJava("super");
    public static final TokenJava TRY = new TokenJava("try");
    public static final TokenJava CATCH = new TokenJava("catch");
    public static final TokenJava FINALLY = new TokenJava("finally");
    public static final TokenJava PACKAGE = new TokenJava("package");
    public static final TokenJava IMPORT = new TokenJava("import");
    public static final TokenJava EXTENDS = new TokenJava("extends");
    public static final TokenJava IMPLEMENTS = new TokenJava("implements");
    public static final TokenJava INSTANCE_OF = new TokenJava("instanceof");
    public static final TokenJava THROWS = new TokenJava("throws");
    public static final TokenJava METHOD_LIMIT = new TokenJava("[METHOD LIMIT]", Type.BREAK);

    private TokenJava(String token) {
        super(new Localisation("", 0, 0), token, Type.VALEUR);
    }

    private TokenJava(String token, Type type) {
        super(new Localisation("", 0, 0), token, type);
    }
}