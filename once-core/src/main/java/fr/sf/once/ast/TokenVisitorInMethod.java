package fr.sf.once.ast;

import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;

import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Token;

/**
 * Visitor that keep only tokens within a method.
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
