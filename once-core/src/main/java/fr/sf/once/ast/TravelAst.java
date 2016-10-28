package fr.sf.once.ast;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import fr.sf.once.model.Token;

public class TravelAst {
    
    public static final Logger LOG = Logger.getLogger(TravelAst.class);
    private final String sourceEncoding;
    
    public TravelAst() {
        this(null);
    }
    
    public TravelAst(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }
    
    public List<Token> extractToken(InputStream input, TokenVisitor tokenVisitor) {
        List<Token> tokenList = new ArrayList<Token>();
        tokenVisitor.visit(getParser(input), tokenList);
        return tokenList;
    }

    private CompilationUnit getParser(InputStream input) throws Error {
        try {
            return JavaParser.parse(input, sourceEncoding);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }
}
