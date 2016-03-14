package fr.sf.once.ast;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import fr.sf.once.model.Token;

public class ParcoursAst {
    
    public static final Logger LOG = Logger.getLogger(ParcoursAst.class);
    private final String sourceEncoding;
    
    public ParcoursAst() {
        this(null);
    }
    
    public ParcoursAst(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }
    
    public List<Token> extraireToken(InputStream input, TokenVisitor tokenVisitor) {
        List<Token> listeToken = new ArrayList<Token>();
        tokenVisitor.visit(getParser(input), listeToken);
        return listeToken;
    }

    private CompilationUnit getParser(InputStream input) throws Error {
        try {
            return JavaParser.parse(input, sourceEncoding);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }
}
