package fr.sf.once;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.function.Function;

import fr.sf.once.launcher.OnceConfiguration;
import fr.sf.once.launcher.Property;
import fr.sf.once.launcher.OnceConfiguration.OnceProperty;

public class ConfigurationDoc {
    public void generateConfigurationFile() throws Exception {
        final String FILE_NAME = "configurationDoc";

        PrintStream printStream = new PrintStream(Documentation.ASCIIDOC_OUTPUT_PATH.resolve(FILE_NAME+ ".asciidoc").toFile());
        Function<Property, String> writer = property -> property.getKey() + "=" + property.getDefaultValue();
        generateDefaultConfigurationFile(printStream, OnceConfiguration.OnceProperty.class, writer);

        Documentation.generateHtmlFromAsciidoc(Documentation.ASCIIDOC_OUTPUT_PATH, Documentation.HTML_OUTPUT_PATH, FILE_NAME);
    }

    public static <T> void generateDefaultConfigurationFile(PrintStream outputStream, Class<OnceProperty> clazz, Function<T, String> writer) {
        outputStream.println("=== " + clazz.getSimpleName() + " file example");
        Field[] declaredFields = clazz.getDeclaredFields();
        outputStream.println("\n....");
        for (Field field : declaredFields) {
            try {
                outputStream.println(writer.apply((T) field.get(null)));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                outputStream.println("# Value could not be generated for field: " + field.getName());
            }
        }
        outputStream.println("\n....");
    }
    
}
