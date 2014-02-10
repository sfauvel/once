package fr.sf.once.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import fr.sf.commons.Files;
import fr.sf.once.ast.ExtractTokenFileVisitor;
import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.comparator.ComparateurAvecSubstitutionEtType;
import fr.sf.once.comparator.BasicComparator;
import fr.sf.once.comparator.ComparateurSimpleSansString;
import fr.sf.once.core.Configuration;
import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import fr.sf.once.report.Reporting;
import fr.sf.once.report.ReportingImpl;

public class Launcher {

    public static class OnceProperties {
        private OnceProperties() {

        }

        public static final String SRC_DIR = "once.sourceDir";
        public static final String SRC_ENCODING = "once.sourceEncoding";
        public static final String VERBOSE = "once.verbose";
    }

    private static final String ONCE_PROPERTY = "once.properties";

    public static final Logger LOG = Logger.getLogger(Launcher.class);

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Properties applicationProperties = new Properties();

        File propertiesFile = new File(ONCE_PROPERTY);

        Properties applicationProps = new Properties();
        if (propertiesFile.exists()) {
            InputStream resourceAsStream = new FileInputStream(propertiesFile);
            applicationProperties.load(resourceAsStream);
            applicationProps = new Properties(applicationProperties); 
        }

        LOG.debug("Properties:");
        for (Entry<Object, Object> entry : applicationProps.entrySet()) {
            LOG.debug(entry.getKey() + ":" + entry.getValue());
        }
        String sourceDir = applicationProps.getProperty(OnceProperties.SRC_DIR, (args.length <= 0) ? "." : args[0]);
        String sourceEncoding = applicationProps.getProperty(OnceProperties.SRC_ENCODING, "iso8859-1");
        boolean isVerbose = Boolean.parseBoolean(applicationProps.getProperty(OnceProperties.VERBOSE, "false"));

//        Logger.getRootLogger().setLevel(Level.INFO);

        if (isVerbose) {
            Reporting.LOG_CSV.addAppender(new FileAppender(new PatternLayout("%m\n"), "result/fichierSortie.csv", false));
            activeLog(Reporting.TRACE_TOKEN, Level.INFO, "result/listeToken.txt");
            activeLog(LOG, Level.INFO, "result/token.txt");
            activeComparateurLog(Level.INFO, "result/comparator.txt");
            activeLog(ComparatorWithSubstitution.LOG, Level.DEBUG, null);

            ManagerToken.LOG.addAppender(new ConsoleAppender(new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} %m" + PatternLayout.LINE_SEP)));
            ManagerToken.LOG.setLevel(Level.INFO);
        }
        activeLog(Reporting.LOG_RESULTAT, Level.INFO, "result/once.txt");
        activeLog(ManagerToken.LOG, Level.INFO, null);
        activeLog(LOG, Level.INFO, null);

        LOG.info("Source directory:" + sourceDir);
        ExtractTokenFileVisitor extractToken = new ExtractTokenFileVisitor(sourceDir, sourceEncoding);
        Files.visitFile(sourceDir, extractToken);

        Class<ComparateurAvecSubstitutionEtType> comparator = ComparateurAvecSubstitutionEtType.class;
        int tailleMin = 20;
        Configuration configuration = new Configuration(comparator).withTailleMin(tailleMin);

        Code code = new Code(extractToken.getTokenList());
        ManagerToken manager = new ManagerToken(code);
        List<Redundancy> listeRedondance = manager.getRedondance(configuration);

        LOG.info("Affichage des resultats...");
        Reporting reporting = new ReportingImpl(extractToken.getMethodList());
        reporting.display(new Code(extractToken.getTokenList(), extractToken.getMethodList()));
        reporting.afficherRedondance(code, 20, listeRedondance);

        LOG.info("Fin");

    }

    private static void activeComparateurLog(Level level, String filename) throws IOException {
        activeLog(CodeComparator.LOG, level, filename);
        activeLog(BasicComparator.LOG, level, filename);
        activeLog(ComparatorWithSubstitution.LOG, level, filename);
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
