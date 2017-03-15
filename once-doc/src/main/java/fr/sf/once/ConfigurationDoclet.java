package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

import fr.sf.once.launcher.OnceConfiguration;
import fr.sf.once.launcher.OnceConfiguration.OnceProperty;
import fr.sf.once.launcher.Property;

public class ConfigurationDoclet {

    public static final String OUTPUT_FILE_NAME = "configuration";
    private static FileWriter fileWriter;
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();

    public static boolean start(RootDoc root) {

        System.out.println(outputFile.getAbsolutePath());
        try (FileWriter localFileWriter = new FileWriter(outputFile)) {
            fileWriter = localFileWriter;

            write("\n\n= Configuration \n"); 
            for (ClassDoc classDoc : root.classes()) {
                display(classDoc);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static void display(ClassDoc classDoc) {
        System.out.println(classDoc.name());
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
        
        write("\n=== " + classDoc.name() + "\n");        
        if (classDoc.commentText().isEmpty()) {
            write("No documentation.");            
        } else {
            write(formatComment(classDoc.commentText()));
        }
    }

    private static String formatComment(String text) {
        return Arrays.stream(text.split("\n")).map(t -> t.trim()).collect(Collectors.joining("\n\n"));
    }

    private static boolean isAConfigurationClass(ClassDoc classDoc) {
        if (classDoc.qualifiedName().equals(OnceConfiguration.OnceProperty.class.getCanonicalName())) {
            return true;
        }
        return false;
    }

    public static void write(Object message) {
        try {
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static <T> void generateDefaultConfigurationFile(ClassDoc classDoc, Class<OnceProperty> clazz, Function<T, String> writer) throws IOException {
        fileWriter.write("=== " + clazz.getSimpleName() + " file example\n");
        Field[] declaredFields = clazz.getDeclaredFields();
        for (FieldDoc field : classDoc.fields()) {
            fileWriter.write("." + field.name().replaceAll("_",  " ").toLowerCase() + "\n");
            if (!field.commentText().isEmpty()) {
                fileWriter.write("\t" + field.commentText()+ "\n");
            }
            try {
                Field declaredField = clazz.getDeclaredField(field.name());
                fileWriter.write(writer.apply((T) declaredField.get(null)) + "\n\n");
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
}
