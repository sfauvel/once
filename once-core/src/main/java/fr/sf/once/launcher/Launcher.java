package fr.sf.once.launcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fr.sf.once.ast.ExtractCode;
import fr.sf.once.comparator.BasicComparator;
import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.comparator.ComparatorWithSubstitutionAndType;
import fr.sf.once.comparator.TokenValueComparatorExceptForString;
import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.model.CodeAsATokenList;
import fr.sf.once.model.Redundancy;
import fr.sf.once.report.Reporting;
import fr.sf.once.report.ReportingCrossMethod;

/**
 * Application main class.
 * 
 * 
 */
public class Launcher {

    public static final Logger LOG = Logger.getLogger(Launcher.class);

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        OnceConfiguration configuration = OnceConfiguration.load(args);

        // Logger.getRootLogger().setLevel(Level.INFO);
        setLogger(configuration.isVerbose());
        LOG.info("Source directory:" + configuration.getSourceDir());

        new Launcher().execute(configuration);

        long endTime = System.currentTimeMillis();
        LOG.info("End (" + (endTime - startTime) + "ms)");

    }

    public void execute(OnceConfiguration configuration) throws FileNotFoundException {
        CodeAsATokenList code = retreiveCode(configuration);
        List<Redundancy> redundancies = findRedundancies(configuration, code);
        formatResult(configuration, code, redundancies);
    }

    private void formatResult(OnceConfiguration configuration, CodeAsATokenList code, List<Redundancy> listeRedondance) {
        LOG.info("Display results...");
        Reporting reporting = buildReporting(configuration);
        reporting.display(code);
        reporting.displayRedundancy(code, 20, listeRedondance);
    }

    private Reporting buildReporting(OnceConfiguration configuration) {
        try {
            return configuration.getReporting().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Redundancy> findRedundancies(OnceConfiguration configuration, CodeAsATokenList code) {
        RedundancyFinder manager = new RedundancyFinder(code);
        List<Redundancy> listeRedondance = manager.findRedundancies(configuration);
        return listeRedondance;
    }

    private CodeAsATokenList retreiveCode(OnceConfiguration configuration) throws FileNotFoundException {
        CodeAsATokenList code = new ExtractCode().extract(configuration.getSourceDir(), configuration.getSourceEncoding());
        return code;
    }

    private static void setLogger(boolean isVerbose) throws IOException {
        if (isVerbose) {
            Reporting.LOG_CSV.addAppender(new FileAppender(new PatternLayout("%m\n"), "result/outputFile.csv", false));
            activateLog(Reporting.TRACE_TOKEN, Level.INFO, "result/tokenList.txt");
            activateLog(LOG, Level.INFO, "result/token.txt");
            activateComparatorLog(Level.INFO, "result/comparator.txt");
            activateLog(ComparatorWithSubstitution.LOG, Level.DEBUG, null);

            RedundancyFinder.LOG.addAppender(new ConsoleAppender(new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} %m" + PatternLayout.LINE_SEP)));
            RedundancyFinder.LOG.setLevel(Level.INFO);
        }
        activateLog(Reporting.LOG_RESULT, Level.INFO, "result/once.txt");
        activateLog(RedundancyFinder.LOG, Level.INFO, null);
        activateLog(LOG, Level.INFO, null);
    }
    
    private static void activateComparatorLog(Level level, String filename) throws IOException {
        activateLog(CodeComparator.LOG, level, filename);
        activateLog(BasicComparator.LOG, level, filename);
        activateLog(ComparatorWithSubstitution.LOG, level, filename);
        activateLog(TokenValueComparatorExceptForString.LOG, level, filename);
        activateLog(ComparatorWithSubstitutionAndType.LOG, level, filename);
    }

    private static void activateLog(Logger log, Level level, String filename) throws IOException {
        if (filename == null) {
            log.addAppender(new ConsoleAppender(new PatternLayout("%d{HH:mm:ss,SSS} - %-5p %c{1} - %m%n")));
        } else {
            log.addAppender(new FileAppender(new PatternLayout(), filename, false));
        }
        log.setLevel(level);

    }

}
