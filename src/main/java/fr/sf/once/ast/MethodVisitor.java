package fr.sf.once.ast;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import fr.sf.once.model.Localisation;
import fr.sf.once.model.MethodLocalisation;

/**
 * Simple visitor implementation for visiting MethodDeclaration nodes.
 */
public class MethodVisitor extends VoidVisitorAdapter<List<MethodLocalisation>> {

    @Override
    public void visit(MethodDeclaration n, List<MethodLocalisation> methodList) {
        MethodLocalisation methodLocalisation = new MethodLocalisation();
        methodLocalisation.setMethodName(n.getName());
        methodLocalisation.setLocalisationDebut(new Localisation("", n.getBeginLine(), n.getBeginColumn()));
        methodLocalisation.setLocalisationFin(new Localisation("", n.getEndLine(), n.getEndColumn()));
        methodList.add(methodLocalisation);
    }
}