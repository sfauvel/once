package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

public class TestDoclet {

    public static final String OUTPUT_FILE_NAME = "testdoc";
    private static FileWriter fileWriter;
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME+ ".asciidoc").toFile();
    
    public static boolean start(RootDoc root) {
        
        try (FileWriter localFileWriter = new FileWriter(outputFile)) {
            fileWriter = localFileWriter;
            write("\n\n= Tests\n\n");
            
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
        if (!isAClassTest(classDoc)) {
            return;
        }

        
        write("\n=== " + classDoc.name() + "\n");
        for (MethodDoc methodDoc : classDoc.methods()) {
            display(methodDoc);
        }
    }

    private static void display(MethodDoc methodDoc) {
        if (containsTag(methodDoc, "Test")) {
          

            // for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
            // write(" @" + annotationDesc.annotationType().simpleTypeName());
            // }

            String name = methodDoc.name();
            write("   * " + name
                    .replaceAll("_", " ")
                    .replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
            
            if (!methodDoc.commentText().isEmpty()) {
                write("\n....\n");
                write("   " + methodDoc.commentText());
                write("\n....\n");
            }
            
        }
    }

    private static boolean isAClassTest(ClassDoc classDoc) {
        for (MethodDoc methodDoc : classDoc.methods()) {
            if (containsTag(methodDoc, "Test")) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsTag(MethodDoc methodDoc, String tag) {
        // Could not finf qualifedname when the class is not in the path.
        return Arrays.stream(methodDoc.annotations())
                .peek(annotation -> System.out.println("annotation:" + annotation.annotationType().name()))
                .anyMatch(annotation -> tag.equals(annotation.annotationType().name()));
    }


    public static void write(Object message) {
        try {
            System.out.println(message);
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
