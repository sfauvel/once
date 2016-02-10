package fr.sf.once.ast;

import java.util.List;

import fr.sf.once.model.Localisation;
import fr.sf.once.model.MethodLocalisation;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * Simple visitor implementation for visiting MethodDeclaration nodes.
 */
public class MethodVisitor extends VoidVisitorAdapter<List<MethodLocalisation>> {

    @Override
    public void visit(MethodDeclaration n, List<MethodLocalisation> methodList) {
        MethodLocalisation methodLocalisation = new MethodLocalisation(n.getName(),
                new Localisation("", n.getBeginLine(), n.getBeginColumn()),
                new Localisation("", n.getEndLine(), n.getEndColumn()));
        methodList.add(methodLocalisation);
    }
}