package fr.sf.once.ast;

import java.io.FileNotFoundException;

import fr.sf.commons.Files;
import fr.sf.once.model.Code;

public class ExtractCode {
    public Code extract(String sourceDir, String sourceEncoding) throws FileNotFoundException {
        ExtractTokenFileVisitor extractToken = new ExtractTokenFileVisitor(sourceDir, sourceEncoding);
        
        Files.visitFile(sourceDir, extractToken);
        return new Code(extractToken.getTokenList(), extractToken.getMethodList());
    }
}