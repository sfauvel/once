package fr.sf.once;

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

import fr.sf.ast.ParcoursAst;
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

    public static class MyFileVisitor extends ParcoursAst implements FileVisitor {
        private final List<Token> tokenList = new ArrayList<Token>();
        private List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
        private String rootPath;

        public MyFileVisitor() {
            this("", null);
        }

        public MyFileVisitor(String rootPath, String sourceEncoding) {
            super(sourceEncoding);
            this.rootPath = rootPath.replaceAll("/", "\\\\") + "\\";
        }

        public void visit(final File file) {
            String fileName = file.getName();
            if (file.isFile() && fileName.endsWith(".java")) {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    TokenVisitor tokenVisitor = new TokenVisitorInMethod(file.getPath().replace(rootPath, ""), methodList);
                    tokenList.addAll(extraireToken(in, tokenVisitor));
                    LOG.info(fileName + ": " + tokenList.size());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    LOG.error("Erreur de lecture du fichier " + fileName, e);
                } catch (Error e) {
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

        public List<MethodLocalisation> getMethodList() {
            return methodList;
        }
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Logger.getRootLogger().setLevel(Level.INFO);

        if (args.length > 1 && args[1].equals("verbose")) {
            Reporting.LOG_CSV.addAppender(new FileAppender(new PatternLayout("%m\n"), "result/fichierSortie.csv", false));
            activeLog(Reporting.TRACE_TOKEN, Level.INFO, "result/listeToken.txt");
            activeLog(LOG, Level.INFO, "result/token.txt");
            activeComparateurLog(Level.INFO, "result/comparator.txt");
            activeLog(ComparateurAvecSubstitution.LOG, Level.DEBUG, null);

            ManagerToken.LOG.addAppender(new ConsoleAppender(new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} %m" + PatternLayout.LINE_SEP)));
            ManagerToken.LOG.setLevel(Level.INFO);
        }
        activeLog(Reporting.LOG_RESULTAT, Level.INFO, "result/once.txt");
        activeLog(ManagerToken.LOG, Level.INFO, null);
        activeLog(LOG, Level.INFO, null);
        
        Launcher launchMyAppli = new Launcher();

        String dir = args[0];
        MyFileVisitor myFileVisitor = new MyFileVisitor(dir, "iso8859-1");
        ManagerToken manager = new ManagerToken(myFileVisitor.getTokenList());

        launchMyAppli.visitFile(dir, myFileVisitor);

        ReportingImpl reporting = new ReportingImpl(myFileVisitor.methodList);
        reporting.display(manager);

        List<Redondance> listeRedondance = manager.getRedondance(
                new Configuration(ComparateurAvecSubstitutionEtType.class)
                        .withTailleMin(30));

        LOG.info("Affichage des resultats...");
        reporting.afficherRedondance(manager.getTokenList(), 20, listeRedondance);
        LOG.info("Fin");

    }

    private static void activeComparateurLog(Level level, String filename) throws IOException {
        activeLog(Comparateur.LOG, level, filename);
        activeLog(ComparateurSansSubstitution.LOG, level, filename);
        activeLog(ComparateurAvecSubstitution.LOG, level, filename);
        activeLog(ComparateurSimpleSansString.LOG, level, filename);
        activeLog(ComparateurAvecSubstitutionEtType.LOG, level, filename);
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
