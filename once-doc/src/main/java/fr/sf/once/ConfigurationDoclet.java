package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.function.Function;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.RootDoc;

import fr.sf.asciidoc.AsciiDoclet;
import fr.sf.asciidoc.RunnerDoclet;
import fr.sf.once.launcher.OnceConfiguration;
import fr.sf.once.launcher.OnceConfiguration.OnceProperty;
import fr.sf.once.launcher.Property;

public class ConfigurationDoclet extends AsciiDoclet {

    public static final String OUTPUT_FILE_NAME = "configuration";
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();


    public static void execute(Path path, String packageName) {
        new RunnerDoclet(new ConfigurationDoclet(), path, packageName).execute();
    }
    
    @Override
    protected Writer newOutputWriter() throws IOException {
        return new FileWriter(outputFile);
    }

    @Override
    protected void display(RootDoc root) {
        adoc.title(1, "Configuration");         
        for (ClassDoc classDoc : root.classes()) {
            display(classDoc);

        }
    }

    private void display(ClassDoc classDoc) {
        if (!isAConfigurationClass(classDoc)) {
            return;
        }
        
        String qualifiedName = classDoc.qualifiedName();
        int lastIndex = qualifiedName.lastIndexOf('.');
        qualifiedName = qualifiedName.substring(0, lastIndex) + "$" + qualifiedName.substring(lastIndex+1);
        Class<?> forName = null;
        try {
            forName = (Class<?>)Class.forName(qualifiedName);
        } catch (ClassNotFoundException e) {
            new RuntimeException(e);
        }
        try {
//            Function<Property, String> writer = property -> "\tParameter:" + property.getKey() + "\n\tDefault value:" + property.getDefaultValue();
            Function<Property, String> writer = property -> "\t" + property.getKey() + ":" + property.getDefaultValue();
            generateDefaultConfigurationFile(classDoc, (Class<OnceProperty>)forName, writer);
        } catch (IOException e) {
            new RuntimeException(e);
        }
        
        adoc.title(2, classDoc.name());        
        if (classDoc.commentText().isEmpty()) {
            adoc.writeln("No documentation.");            
        } else {
            adoc.javaComment(classDoc.commentText());
        }
    }

    private static boolean isAConfigurationClass(ClassDoc classDoc) {
        if (classDoc.qualifiedName().equals(OnceConfiguration.OnceProperty.class.getCanonicalName())) {
            return true;
        }
        return false;
    }

    public <T> void generateDefaultConfigurationFile(ClassDoc classDoc, Class<OnceProperty> clazz, Function<T, String> writer) throws IOException {
        adoc.title(2, clazz.getSimpleName() + " file example");
        for (FieldDoc field : classDoc.fields()) {
            adoc.write("." + field.name().replaceAll("_",  " ").toLowerCase() + "\n");
            if (!field.commentText().isEmpty()) {
                adoc.write("\t" + field.commentText()+ "\n");
            }
            try {
                Field declaredField = clazz.getDeclaredField(field.name());
                adoc.write(writer.apply((T) declaredField.get(null)) + "\n\n");
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
}
