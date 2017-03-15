package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import fr.sf.once.Documentation.AsciidocWriter;

public class DomainDoclet extends AsciiDoclet {

    public static final String OUTPUT_FILE_NAME = "domain";
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();
    private AsciidocWriter adoc;

    public DomainDoclet(AsciidocWriter adoc) {
        this.adoc = adoc;
    }

    public static boolean start(RootDoc root) {

        try (AsciidocWriter adoc = new AsciidocWriter(outputFile)) {
            new DomainDoclet(adoc).display(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void display(RootDoc root) {
        adoc.title(1, "Domain"); 
        
        for (ClassDoc classDoc : root.classes()) {
            display(classDoc);
        }
    }

    private void display(ClassDoc classDoc) {
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

    private boolean isADomainClass(ClassDoc classDoc) {
        return containsTag(classDoc, "Domain");
    }

}
