package fr.sf.once.launcher;

public class Property {
    private final String key;
    private final String defaultValue;

    private Property(final String key) {
        this.key = key;
        this.defaultValue = null;
    };

    Property(final String key, final String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    };

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}