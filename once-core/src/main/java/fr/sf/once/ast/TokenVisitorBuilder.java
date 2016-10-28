package fr.sf.once.ast;

import java.io.File;
import java.util.List;

import fr.sf.once.model.MethodLocation;

public class TokenVisitorBuilder {

    TokenVisitor build(final File file, String fileName, List<MethodLocation> methodList, int firstTokenNumber) {
        return new TokenVisitorInMethod(fileName, methodList, firstTokenNumber);
    }

}
