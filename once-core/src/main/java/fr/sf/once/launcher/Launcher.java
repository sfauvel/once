package fr.sf.once.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.log4j.Priority;

import fr.sf.once.ast.ExtractCode;
import fr.sf.once.comparator.BasicComparator;
import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.comparator.ComparatorWithSubstitutionAndType;
import fr.sf.once.comparator.TokenValueComparatorExceptForString;
import fr.sf.once.core.Configuration;
import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.report.Reporting;
import fr.sf.once.report.ReportingCrossMethod;

/**
 * Application main class.
 * 
 * 
 */
public class Launcher {

    public static class OnceProperties extends Properties {

        private static final long serialVersionUID = 1L;

        private static final String ONCE_PROPERTY = "once.properties";

        public static class Key {
            static final Key SRC_DIR = new Key("once.sourceDir");
            static final Key SRC_ENCODING = new Key("once.sourceEncoding");
            static final Key VERBOSE = new Key("once.verbose");

            private final String key;

            private Key(String key) {
                this.key = key;
            };

            @Override
            public String toString() {
                return key;
            }
        }

        private OnceProperties(Properties applicationProps) {
            super(applicationProps);
        }

        public String getSourceDir() {
            return get(Key.SRC_DIR, ".");
        }

        public String getSourceEncoding() {
            return get(Key.SRC_ENCODING, "iso8859-1");
        }

        public boolean isVerbose() {
            return Boolean.parseBoolean(get(Key.VERBOSE, "false"));
        }

        private String get(Key key, String defaultValue) {
            return getProperty(key.toString(), defaultValue);
        }

        public static OnceProperties extractConfigurationFrom(String[] args) throws FileNotFoundException, IOException {
            return extractConfiguration(args, new File(ONCE_PROPERTY));
        }

        public static OnceProperties extractConfiguration(String[] args, File propertiesFile) throws FileNotFoundException, IOException {
            Properties applicationProperties = getPropertiesFromFile(propertiesFile);
            logProperties(Level.DEBUG, applicationProperties);
            return getPropertiesWithArguments(applicationProperties, args);
        }

        private static OnceProperties getPropertiesWithArguments(Properties applicationProperties, String[] args) {
            OnceProperties onceProperties = new OnceProperties(applicationProperties);
            if (args.length > 0) {
                onceProperties.put(Key.SRC_DIR.toString(), args[0]);
            }
            return onceProperties;
        }

        private static Properties getPropertiesFromFile(File propertiesFile) throws FileNotFoundException, IOException {
            Properties applicationProperties = new Properties();
            if (propertiesFile.exists()) {
                InputStream resourceAsStream = new FileInputStream(propertiesFile);
                applicationProperties.load(resourceAsStream);
            }
            return applicationProperties;
        }

        private static void logProperties(Level level, Properties applicationProperties) {
            if (!LOG.isEnabledFor(level)) {
                return;
            }
            
            LOG.log(level, "Properties:");
            for (Entry<Object, Object> entry : applicationProperties.entrySet()) {
                LOG.log(level, entry.getKey() + ":" + entry.getValue());
            }
        }
    }

    public static final Logger LOG = Logger.getLogger(Launcher.class);

    private String sourceDir;

    private String sourceEncoding;

    private Class<? extends CodeComparator> comparator;

    private int minimalSize;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        OnceProperties onceProps = OnceProperties.extractConfigurationFrom(args);

        // Logger.getRootLogger().setLevel(Level.INFO);

        setLogger(onceProps.isVerbose());

        LOG.info("Source directory:" + onceProps.getSourceDir());

        Class<ComparatorWithSubstitutionAndType> comparator = ComparatorWithSubstitutionAndType.class;
        int tailleMin = 20;
        Launcher launcher = new Launcher()
                .withSource(onceProps.getSourceDir(), onceProps.getSourceEncoding())
                .withComparator(comparator)
                .withMinimalSize(tailleMin);
        launcher.execute();

        long endTime = System.currentTimeMillis();
        LOG.info("End (" + (endTime - startTime) + "ms)");

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

    public Launcher withMinimalSize(int minimalSize) {
        this.minimalSize = minimalSize;
        return this;
    }

    public Launcher withComparator(Class<? extends CodeComparator> comparator) {
        this.comparator = comparator;
        return this;
    }

    public Launcher withSource(String sourceDir, String sourceEncoding) {
        this.sourceDir = sourceDir;
        this.sourceEncoding = sourceEncoding;
        return this;
    }

    public void execute() throws FileNotFoundException {
        Code code = new ExtractCode().extract(sourceDir, sourceEncoding);

        Configuration configuration = new Configuration(comparator).withMinimalTokenNumber(minimalSize);

        RedundancyFinder manager = new RedundancyFinder(code);
        List<Redundancy> listeRedondance = manager.findRedundancies(configuration);

        LOG.info("Display results...");
        // Reporting reporting = new ReportingImpl();
        Reporting reporting = new ReportingCrossMethod();
        reporting.display(code);
        reporting.displayRedundancy(code, 20, listeRedondance);
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
