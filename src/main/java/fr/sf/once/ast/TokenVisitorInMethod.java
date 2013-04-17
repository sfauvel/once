package fr.sf.once.ast;

import japa.parser.ast.body.MethodDeclaration;

import java.util.List;

import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Token;

/**
 * Visiteur ne retenant que les token à l'intérieur d'une méthode.
 */
public class TokenVisitorInMethod extends TokenVisitor {

    private boolean isInMethod = false;
    
    public TokenVisitorInMethod(String fileName, List<MethodLocalisation> methodList, int firstTokenNumber) {
        super(fileName, methodList, firstTokenNumber);
    }

    public void visit(MethodDeclaration n, List<Token> arg) {
        isInMethod = true;
        super.visit(n, arg);
        isInMethod = false;
    }
    
    protected void addToken(int beginLine, int beginColumn, String token, fr.sf.once.model.Type type, List<Token> arg) {
        if (isInMethod) {
           super.addToken(beginLine, beginColumn, token, type, arg);
        }
    }
}
