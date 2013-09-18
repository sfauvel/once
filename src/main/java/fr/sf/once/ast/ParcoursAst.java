package fr.sf.once.ast;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
        CompilationUnit cu = null;
        try {
            cu = JavaParser.parse(input, sourceEncoding);
        } catch (ParseException e) {
            throw new Error(e);
        }
       
        List<Token> listeToken = new ArrayList<Token>();
        tokenVisitor.visit(cu, listeToken);

        return listeToken;
    }
}