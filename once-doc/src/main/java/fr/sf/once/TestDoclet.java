package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

import fr.sf.asciidoc.AsciiDoclet;
import fr.sf.asciidoc.RunnerDoclet;

public class TestDoclet extends AsciiDoclet {

    public static final String OUTPUT_FILE_NAME = "testdoc";
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME+ ".asciidoc").toFile();
    
    @Override
    protected Writer newOutputWriter() throws IOException {
        return new FileWriter(outputFile);
    }

    public static void execute(Path path, String packageName) {
        new RunnerDoclet(new TestDoclet(), path, packageName).execute();
    }
    
    protected void display(RootDoc root) {
        adoc.title(1, "Tests");
        
        for (ClassDoc classDoc : root.classes()) {
            display(classDoc);

        }
    }
    
    private void display(ClassDoc classDoc) {
        if (!isAClassTest(classDoc)) {
            return;
        }
        
        adoc.title(2, classDoc.name());
        for (MethodDoc methodDoc : classDoc.methods()) {
            display(methodDoc);
        }
    }

    private void display(MethodDoc methodDoc) {
        if (containsTag(methodDoc, "Test")) {

            String name = methodDoc.name();
            adoc.list(name
                    .replaceAll("_", " ")
                    .replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
            
            if (!methodDoc.commentText().isEmpty()) {
                adoc.write("\n....\n");
                adoc.write("   " + methodDoc.commentText());
                adoc.write("\n....\n");
            }
            
        }
    }

    private boolean isAClassTest(ClassDoc classDoc) {
        for (MethodDoc methodDoc : classDoc.methods()) {
            if (containsTag(methodDoc, "Test")) {
                return true;
            }
        }
        return false;
    }
//
//    private static boolean containsTag(MethodDoc methodDoc, String tag) {
//        // Could not finf qualifedname when the class is not in the path.
//        return Arrays.stream(methodDoc.annotations())
//                .peek(annotation -> System.out.println("annotation:" + annotation.annotationType().name()))
//                .anyMatch(annotation -> tag.equals(annotation.annotationType().name()));
//    }
//
//
//    public static void write(Object message) {
//        try {
//            System.out.println(message);
//            fileWriter.write(message + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
}
