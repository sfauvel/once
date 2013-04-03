package fr.sf.ast;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import fr.sf.once.ComparateurAvecSubstitution;
import fr.sf.once.ManagerToken;
import fr.sf.once.Redondance;
import fr.sf.once.Token;

public class ProgSpike {

    /**
     * @param args
     * @throws ParseException
     * @throws IOException
     */
    public static void main(String[] args) throws ParseException, IOException {

        Logger logger = Logger.getRootLogger();
        // logger.addAppender(new ConsoleAppender(new PatternLayout("%m\n")));
        // logger.addAppender(new FileAppender(new PatternLayout("%m\n"),
        // "fichierSortie.txt"));
        logger.setLevel(Level.INFO);

        Logger.getLogger("RESULTAT").addAppender(new FileAppender(new SimpleLayout(), "fichierSortie.txt", false));
        Logger.getLogger("RESULTAT").setLevel(Level.DEBUG);
        
        Logger.getLogger("CSV").addAppender(new FileAppender(new PatternLayout("%m\n"), "fichierExport.csv", false));
        Logger.getLogger("CSV").setLevel(Level.DEBUG);
        
        Logger.getLogger(ComparateurAvecSubstitution.class).setLevel(Level.INFO);

        // creates an input stream for the file to be parsed
        // FileInputStream in = new
        // FileInputStream("./src/main/java/fr/sf/ast/ExempleClasseAvecRedondance.java");
        //FileInputStream in = new FileInputStream("./src/test/resources/DmdPltIntranetImpl.java");
        
        // Sur ce fichier, il y a une redondance entre les lignes66 et 106 (nom de la remise différente)
        // Il y un bloque différent entre 79 et 90 avant de redevenir identique.
        // C'est un cas intéressant pour fair eun calcule de redondance global d'une méthode.
        FileInputStream in = new FileInputStream("./src/test/resources/RetourConsulterAction.java");

        ParcoursAst parcoursAst = new ParcoursAst();
        List<Token> listeToken = parcoursAst.extraireToken(in, new TokenVisitor());
        for (Token token : listeToken) {
            System.out.println(token.getValeurToken());
        }

        ManagerToken manager = new ManagerToken(listeToken);

        List<Redondance> listeRedondance = manager.getRedondance(50);

    }

}
