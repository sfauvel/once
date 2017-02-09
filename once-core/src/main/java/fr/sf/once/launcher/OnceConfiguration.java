package fr.sf.once.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Level;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitutionAndType;
import fr.sf.once.core.RedundancyFinderConfiguration;

/**
 * 
 */
public class OnceConfiguration implements RedundancyFinderConfiguration {

    private static final long serialVersionUID = 1L;

    private static final String ONCE_PROPERTY_FILE_NAME = "once.properties";

    public static class OnceProperty {
        public static final Property SRC_DIR = new Property("once.sourceDir", ".");
        public static final Property SRC_ENCODING = new Property("once.sourceEncoding", "iso8859-1");
        public static final Property VERBOSE = new Property("once.verbose", Boolean.FALSE.toString());
        public static final Property MINIMAL_SIZE_DETECTION = new Property("once.minimalSizeDetection", "20");
        public static final Property CLASS_COMPARATOR = new Property("once.classComparator", ComparatorWithSubstitutionAndType.class.getName());
        
        public static void generateDefaultConfigurationFile(PrintStream outputStream) {
            outputStream.println("# Configuration property file example");
            Field[] declaredFields = OnceConfiguration.OnceProperty.class.getDeclaredFields();
            for (Field field : declaredFields) {
                try {
                    Property property = (Property) field.get(null);
                    outputStream.println(property.getKey() + "=" + property.getDefaultValue());
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    outputStream.println("# Value could not be generated for field: "+ field.getName());
                }
            }
            outputStream.println();
        }
    }
    
    private final Properties properties;

    public OnceConfiguration() {
        properties = new Properties();
    }

    private OnceConfiguration(Properties applicationProps) {
        properties = new Properties(applicationProps);
    }

    public String getSourceDir() {
        return get(OnceProperty.SRC_DIR);
    }

    public OnceConfiguration withSourceDir(String sourceDir) {
        return put(OnceProperty.SRC_DIR, sourceDir);
    }

    public String getSourceEncoding() {
        return get(OnceProperty.SRC_ENCODING);
    }

    public OnceConfiguration withSourceEncoding(String sourceEncoding) {
        return put(OnceProperty.SRC_ENCODING, sourceEncoding);
    }

    public OnceConfiguration withSource(String sourceDir, String sourceEncoding) {
        return withSourceDir(sourceDir)
                .withSourceDir(sourceDir);
    }

    public boolean isVerbose() {
        return Boolean.parseBoolean(get(OnceProperty.VERBOSE));
    }

    public OnceConfiguration withVerbose(boolean verbose) {
        return put(OnceProperty.VERBOSE, Boolean.toString(verbose));
    }

    @Override
    public int getMinimalTokenNumberDetection() {
        return Integer.parseInt(get(OnceProperty.MINIMAL_SIZE_DETECTION));
    }
    
    public OnceConfiguration withMinimalTokenNumberDetection(int minimalSizeDetection) {
        return put(OnceProperty.MINIMAL_SIZE_DETECTION, Integer.toString(minimalSizeDetection));
    }

    @Override
    public Class<? extends CodeComparator> getCodeComparatorClass() {
        try {
            return (Class<CodeComparator>) Class.forName(get(OnceProperty.CLASS_COMPARATOR));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public OnceConfiguration withCodeComparatorClass(Class<? extends CodeComparator> codeComparatorClass) {
        return put(OnceProperty.CLASS_COMPARATOR, codeComparatorClass.getName());
    }

    private String get(Property property) {
        return properties.getProperty(property.getKey(), property.getDefaultValue());
    }

    private OnceConfiguration put(Property property, String value) {
        if (property == null || value == null) {
            return this;
        }

        OnceConfiguration onceConfiguration = new OnceConfiguration(this.properties);
        onceConfiguration.properties.put(property.getKey(), value);
        return onceConfiguration;
    }

    public static OnceConfiguration load(String[] args) throws FileNotFoundException, IOException {
        return load(args, new File(ONCE_PROPERTY_FILE_NAME));
    }

    public static OnceConfiguration load(String[] args, File propertiesFile) throws FileNotFoundException, IOException {
        Properties applicationProperties = getPropertiesFromFile(propertiesFile);
        logProperties(Level.DEBUG, applicationProperties);
        return load(applicationProperties, args);
    }

    private static OnceConfiguration load(Properties applicationProperties, String[] args) {
        OnceConfiguration onceProperties = new OnceConfiguration(applicationProperties);
        if (args.length > 0) {
            return onceProperties.withSourceDir(args[0]);
        } else {
            return onceProperties;
        }
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
        if (!Launcher.LOG.isEnabledFor(level)) {
            return;
        }

        Launcher.LOG.log(level, "Properties:");
        for (Entry<Object, Object> entry : applicationProperties.entrySet()) {
            Launcher.LOG.log(level, entry.getKey() + ":" + entry.getValue());
        }
    }

}