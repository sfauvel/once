package fr.sf.once;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import fr.sf.ast.TokenVisitor;
import fr.sf.ast.TokenVisitorInMethod;

public class Launcher {

    public static final Logger LOG = Logger.getLogger(Launcher.class);

    protected void visitFile(String dir, FileVisitor visitor) {
        File file = new File(dir);
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                visitor.visit(files[i]);
                if (files[i].isFile()) {
                    LOG.debug("Fichier: " + files[i].getName());
                } else if (files[i].isDirectory()) {
                    LOG.debug("Répertoire: " + files[i].getName());
                    visitFile(files[i].getAbsolutePath(), visitor);
                    LOG.debug("Fin de répertoire: " + files[i].getName());
                }
            }
        }
    }

    public interface FileVisitor {
        void visit(File file);
    }

    public static class MyFileVisitor implements FileVisitor {
        private final List<Token> tokenList = new ArrayList<Token>();
        private ArrayList<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();

        public void visit(final File file) {
            String fileName = file.getName();
            if (file.isFile() && fileName.endsWith(".java")) {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    CompilationUnit cu = JavaParser.parse(in, "iso8859-1");

                    TokenVisitor tokenVisitor = new TokenVisitorInMethod(fileName, methodList);
                    tokenVisitor.visit(cu, tokenList);
                    LOG.info(fileName + ": " + tokenList.size());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    LOG.error("Erreur de lecture du fichier " + fileName, e);
                } catch (ParseException e) {
                    LOG.error("Erreur de parsing du fichier " + fileName, e);
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        LOG.error("Erreur de fermeture du fichier " + fileName, e);
                    }
                }
            }
        }

        public List<Token> getTokenList() {
            return tokenList;
        }

        public ArrayList<MethodLocalisation> getMethodList() {
            return methodList;
        }
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Logger.getRootLogger().setLevel(Level.INFO);

        // Logger.getLogger("RESULTAT").addAppender(new FileAppender(new
        // SimpleLayout(), "fichierSortie.txt", false));
        // Logger.getLogger("RESULTAT").setLevel(Level.INFO);

        //Reporting.LOG_CSV.addAppender(new FileAppender(new PatternLayout("%m\n"), "result/fichierSortie.csv", false));
        // Logger.getLogger("RESULTAT").setLevel(Level.INFO);
        activeLog(Reporting.LOG_RESULTAT, Level.INFO, "result/once.txt");
        //activeLog(Reporting.TRACE_TOKEN, Level.INFO, "result/listeToken.txt");
        //activeLog(LOG, Level.INFO, "result/token.txt");
        //activeComparateurLog(Level.INFO, "result/comparator.txt");

        // activeLog(ComparateurAvecSubstitution.LOG, Level.DEBUG, null);
        LOG.addAppender(new ConsoleAppender(new SimpleLayout()));

        Launcher launchMyAppli = new Launcher();

//        ManagerToken.LOG.addAppender(new FileAppender(new SimpleLayout(), "result/sortedList.txt", false));
//        ManagerToken.LOG.addAppender(new ConsoleAppender(new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} %m" + PatternLayout.LINE_SEP)));
//        ManagerToken.LOG.setLevel(Level.INFO);

        MyFileVisitor myFileVisitor = new MyFileVisitor();
        ManagerToken manager = new ManagerToken(myFileVisitor.getTokenList());

        String dir = args[0];
        launchMyAppli.visitFile(dir, myFileVisitor);

        ReportingImpl reporting = new ReportingImpl(myFileVisitor.methodList);
        reporting.display(manager);

        List<Redondance> listeRedondance = manager.getRedondance(
                new Configuration(ComparateurAvecSubstitution.class)
                        .withTailleMin(20));

        LOG.info("Affichage des resultats...");
        reporting.afficherRedondance(manager.getTokenList(), 20, listeRedondance);
//        for (Redondance redondance : listeRedondance) {
//            reporting.afficherMethodeDupliqueAvecSubtitution(manager.getTokenList(), redondance);
//        }

        LOG.info("Fin");

    }

    private static void activeComparateurLog(Level level, String filename) throws IOException {
        activeLog(Comparateur.LOG, level, filename);
        activeLog(ComparateurSansSubstitution.LOG, level, filename);
        activeLog(ComparateurAvecSubstitution.LOG, level, filename);
    }

    private static void activeLog(Logger log, Level level, String filename) throws IOException {
        if (filename == null) {
            log.addAppender(new ConsoleAppender(new SimpleLayout()));
        } else {
            log.addAppender(new FileAppender(new PatternLayout(), filename, false));
        }
        log.setLevel(level);

    }

}
