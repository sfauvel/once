package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

import fr.sf.once.Documentation.AsciidocWriter;

public class DomainDoclet {

    public static final String OUTPUT_FILE_NAME = "domain";
    private static FileWriter fileWriter;
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();

    public static boolean start(RootDoc root) {

        try (AsciidocWriter adoc = new AsciidocWriter(outputFile)) {

            adoc.title(1, "Domain"); 
            
            for (ClassDoc classDoc : root.classes()) {
                display(adoc, classDoc);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static void display(AsciidocWriter adoc, ClassDoc classDoc) {
        if (!isADomainClass(classDoc)) {
            return;
        }

        adoc.title(2, classDoc.name());        
        if (classDoc.commentText().isEmpty()) {
            adoc.writeln("No documentation.");            
        } else {
            adoc.javaComment(classDoc.commentText());
        }
    }

    private static boolean isADomainClass(ClassDoc classDoc) {
        return containsTag(classDoc, "Domain");
    }

    private static boolean containsTag(MethodDoc methodDoc, String tag) {
        // Could not finf qualifedname when the class is not in the path.
        return containsTag(tag, methodDoc.annotations());
    }

    private static boolean containsTag(ClassDoc classDoc, String tag) {
        // Could not finf qualifedname when the class is not in the path.
        return containsTag(tag, classDoc.annotations());
    }

    private static boolean containsTag(String tag, AnnotationDesc[] annotations) {
        return Arrays.stream(annotations)
                .peek(annotation -> System.out.println("annotation:" + annotation.annotationType().name()))
                .anyMatch(annotation -> tag.equals(annotation.annotationType().name()));
    }

    public static void write(Object message) {
        try {
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
