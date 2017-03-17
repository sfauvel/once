package fr.sf.asciidoc;

import java.io.IOException;
import java.io.Writer;

import com.sun.javadoc.RootDoc;

import fr.sf.once.Documentation.AsciidocWriter;

public abstract class AsciiDoclet extends CustomDoclet {
    protected AsciidocWriter adoc;
    
    public boolean start(RootDoc root) {
        try (AsciidocWriter adoc = new AsciidocWriter(newOutputWriter())) {
            this.adoc = adoc;
            display(root);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.adoc = null;
        }

        return true;
    }
    
    protected Writer newOutputWriter() throws IOException {
        return null;
    }
    protected void display(RootDoc root) {
        
    }
}
