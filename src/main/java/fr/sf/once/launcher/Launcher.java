package fr.sf.once.launcher;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import fr.sf.once.ast.ExtractTokenFileVisitor;
import fr.sf.once.comparator.Comparateur;
import fr.sf.once.comparator.ComparateurAvecSubstitution;
import fr.sf.once.comparator.ComparateurAvecSubstitutionEtType;
import fr.sf.once.comparator.ComparateurSansSubstitution;
import fr.sf.once.comparator.ComparateurSimpleSansString;
import fr.sf.once.core.Configuration;
import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.Redondance;
import fr.sf.once.report.Reporting;
import fr.sf.once.report.ReportingImpl;
import fr.sf.commons.Files;

public class Launcher {

    public static class OnceProperties {
        private OnceProperties() {
            
        }
        public static final String SRC_DIR = "once.sourceDir";
        public static final String SRC_ENCODING = "once.sourceEncoding";
    }
    private static final String ONCE_PROPERTY = "once.properties";
    
    public static final Logger LOG = Logger.getLogger(Launcher.class);

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Properties applicationProperties = new Properties();
        applicationProperties.load(Launcher.class.getClassLoader().getResourceAsStream(ONCE_PROPERTY));
        Properties applicationProps = new Properties(applicationProperties);

        String sourceDir = applicationProps.getProperty(OnceProperties.SRC_DIR, args[0]);
        String sourceEncoding = applicationProps.getProperty(OnceProperties.SRC_ENCODING, "iso8859-1");

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

      //  String dir = args[0];
        ExtractTokenFileVisitor extractToken = new ExtractTokenFileVisitor(sourceDir, sourceEncoding);
        Files.visitFile(sourceDir, extractToken);
        
      ReportingImpl reporting = new ReportingImpl(extractToken.getMethodList());
      reporting.display(extractToken.getTokenList());
        
        ManagerToken manager = new ManagerToken(extractToken.getTokenList());
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
