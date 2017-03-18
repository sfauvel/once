package fr.sf.once.ast;

import java.io.FileNotFoundException;

import fr.sf.commons.Files;
import fr.sf.once.model.CodeAsATokenList;

public class ExtractCode {
    public CodeAsATokenList extract(String sourceDir, String sourceEncoding) throws FileNotFoundException {
        ExtractTokenFileVisitor extractToken = new ExtractTokenFileVisitor(sourceDir, sourceEncoding);
        
        Files.visitFile(sourceDir, extractToken);
        return new CodeAsATokenList(extractToken.getTokenList(), extractToken.getMethodList());
    }
}