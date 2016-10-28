package fr.sf.once.ast;



import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import fr.sf.once.model.Location;
import fr.sf.once.model.MethodLocation;

/**
 * Simple visitor implementation for visiting MethodDeclaration nodes.
 */
public class MethodVisitor extends VoidVisitorAdapter<List<MethodLocation>> {

    @Override
    public void visit(MethodDeclaration n, List<MethodLocation> methodList) {
        MethodLocation methodLocalisation = new MethodLocation(n.getName(),
                new Location("", n.getBeginLine(), n.getBeginColumn()),
                new Location("", n.getEndLine(), n.getEndColumn()));
        methodList.add(methodLocalisation);
    }
}