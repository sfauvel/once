package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import fr.sf.asciidoc.AsciiDoclet;
import fr.sf.asciidoc.RunnerDoclet;

public class DomainDoclet extends AsciiDoclet {

    public static final String OUTPUT_FILE_NAME = "domain";
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();
   
    public static void execute(Path path, String packageName) {
        new RunnerDoclet(new DomainDoclet(), path, packageName).execute();
    }
    
    @Override
    protected Writer newOutputWriter() throws IOException {
        return new FileWriter(outputFile);
    }
    
    @Override
    protected void display(RootDoc root) {
        adoc.title(1, "Domain"); 
        
        for (ClassDoc classDoc : root.classes()) {
            display(classDoc);
        }
        adoc.blankLine();
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
