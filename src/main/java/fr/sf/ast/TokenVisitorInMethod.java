package fr.sf.ast;

import japa.parser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

import fr.sf.once.MethodLocalisation;
import fr.sf.once.Token;
import fr.sf.once.Type;

/**
 * Visiteur ne retenant que les token à l'intérieur d'une méthode.
 */
public class TokenVisitorInMethod extends TokenVisitor {

    private boolean isInMethod = false;
    
    public TokenVisitorInMethod(String fileName, List<MethodLocalisation> methodList) {
        super(fileName, methodList);
    }

    public void visit(MethodDeclaration n, List<Token> arg) {
        isInMethod = true;
        super.visit(n, arg);
        isInMethod = false;
    }
    
    protected void addToken(int beginLine, int beginColumn, String token, fr.sf.once.Type type, List<Token> arg) {
        if (isInMethod) {
           super.addToken(beginLine, beginColumn, token, type, arg);
        }
    }
}
