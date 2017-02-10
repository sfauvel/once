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

public class DomainDoclet {

    public static final String OUTPUT_FILE_NAME = "domain";
    private static FileWriter fileWriter;
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();

    public static boolean start(RootDoc root) {

        System.out.println(outputFile.getAbsolutePath());
        try (FileWriter localFileWriter = new FileWriter(outputFile)) {
            fileWriter = localFileWriter;


            write("\n= Domain \n"); 
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
        if (!isADomainClass(classDoc)) {
            return;
        }

        write("\n=== " + classDoc.name() + "\n");        
        if (classDoc.commentText().isEmpty()) {
            write("No documentation.");            
        } else {
            write(formatComment(classDoc.commentText()));
        }

        // for (MethodDoc methodDoc : classDoc.methods()) {
        // display(methodDoc);
        // }
    }

    private static String formatComment(String text) {
        return Arrays.stream(text.split("\n")).map(t -> t.trim()).collect(Collectors.joining("\n\n"));
    }

    // private static void display(MethodDoc methodDoc) {
    // if (containsTag(methodDoc, "Test")) {
    //
    //
    // // for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
    // // write(" @" + annotationDesc.annotationType().simpleTypeName());
    // // }
    //
    // String name = methodDoc.name();
    // write(" * " + name
    // .replaceAll("_", " ")
    // .replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
    //
    // if (!methodDoc.commentText().isEmpty()) {
    // write("\n....\n");
    // write(" " + methodDoc.commentText());
    // write("\n....\n");
    // }
    //
    // }
    // }
    //
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
