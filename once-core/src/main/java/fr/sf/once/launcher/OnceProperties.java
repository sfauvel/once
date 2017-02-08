package fr.sf.once.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Level;

public class OnceProperties extends Properties {

    private static final long serialVersionUID = 1L;

    private static final String ONCE_PROPERTY = "once.properties";

    public static class Key {
        public static final OnceProperties.Key SRC_DIR = new Key("once.sourceDir");
        public static final OnceProperties.Key SRC_ENCODING = new Key("once.sourceEncoding");
        public static final OnceProperties.Key VERBOSE = new Key("once.verbose");

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

    private String get(OnceProperties.Key key, String defaultValue) {
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
        if (!Launcher.LOG.isEnabledFor(level)) {
            return;
        }
        
        Launcher.LOG.log(level, "Properties:");
        for (Entry<Object, Object> entry : applicationProperties.entrySet()) {
            Launcher.LOG.log(level, entry.getKey() + ":" + entry.getValue());
        }
    }
}