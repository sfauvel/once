package fr.sf.ast;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fr.sf.once.MethodLocalisation;
import fr.sf.once.Token;

public class ParcoursAst {
    public static final Logger OUTPUT_LOG = Logger.getLogger("SORTIE");
    public static final Logger LOG = Logger.getLogger(ParcoursAst.class);
    public static final Logger FILE_LOG = Logger.getLogger("SORTIE_FICHIER");

    /**
     * Entry points for the program.
     * 
     * @param args
     *            String[]
     * @throws ParseException
     * @throws IOException
     */
    public static void main(String[] args) throws ParseException, IOException {
        LOG.addAppender(new ConsoleAppender(new PatternLayout("%m\n")));
        LOG.setLevel(Level.DEBUG);

        OUTPUT_LOG.addAppender(new ConsoleAppender(new PatternLayout(">  %m\n")));
        OUTPUT_LOG.setLevel(Level.DEBUG);

        FILE_LOG.addAppender(new FileAppender(new PatternLayout("%m\n"), "result/token.txt", false));
        FILE_LOG.addAppender(new ConsoleAppender(new PatternLayout("%m\n")));
        FILE_LOG.setLevel(Level.INFO);

        
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream("./src/main/java/fr/sf/ast/PageTestAst.java");

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }

        // // prints the resulting compilation unit to default system output
        // logger.debug(cu.toString());

        // visit and print the methods names

        new MethodVisitor().visit(cu, new ArrayList<MethodLocalisation>());
        
        List<Token> listeToken = new ArrayList<Token>() {

            @Override
            public boolean add(Token e) {
                OUTPUT_LOG.info(e);
                return super.add(e);
            }
            
        };
        TokenVisitor tokenVisitor = new TokenVisitor() {
            public void genericVisit(Node n, List<Token> arg) { 
                logSortie.debug("                          " + n.getClass().getName());
            }
        };
        
        tokenVisitor.visit(cu, listeToken);

        for (Token token : listeToken) {
            FILE_LOG.info(token.getValeurToken());
        }
    }

    public List<Token> extraireToken(InputStream input) {
        CompilationUnit cu = null;
        try {
            cu = JavaParser.parse(input);
        } catch (ParseException e) {
            throw new Error(e);
        }
       
        List<Token> listeToken = new ArrayList<Token>() {
            @Override
            public boolean add(Token e) {
                LOG.debug(e.getValeurToken());
                return super.add(e);
            }
        };
        
        TokenVisitor tokenVisitor = new TokenVisitor() {
            public void genericVisit(Node n, List<Token> arg) { 
                LOG.debug("-----" + n.getClass().getName());
            }
        };
        
        tokenVisitor.visit(cu, listeToken);

        return listeToken;
    }
}
