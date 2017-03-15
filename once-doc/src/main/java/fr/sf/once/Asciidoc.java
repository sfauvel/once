package fr.sf.once;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

public class Asciidoc {
    private Writer writer;

    public Asciidoc(Writer writer) {
        this.writer = writer;
    }
    
    protected Writer getWriter() {
        return writer;
    }

    public Asciidoc writeln(String format, Object... args) {
        return write(format + "\n", args);
    }
    
    public Asciidoc write(String format, Object... args) {
        try {
            writer.write(String.format(format, args));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (MissingFormatArgumentException e) {
            System.err.println(e.getClass().getSimpleName() + ":" + e.getMessage());
            System.err.println("\tformat:" + format);
        }
        return this;
    }

    public Asciidoc title(int level, String title) {
        
        return writeln("\n%s %s", StringUtils.leftPad("", level, "="), title);
    }

    public Asciidoc tableOfContent() throws IOException {
        return writeln(":toc:");
    }

    public Asciidoc split() {
        return writeln(":split:");
    }
    
    public Asciidoc blankLine() {
        return writeln("");
    }

    public Asciidoc changeLevelOffset(int offset) {
        return writeln(":leveloffset: %d", offset);
    }

    public Asciidoc include(String filename) {
        return writeln("include::%s[]", filename);
    }

    public Asciidoc link(String filename, String label) {
        return writeln("link::%s[%s]\n", filename, label);
    }

    public Asciidoc javaComment(String comment) {
        return writeln(Arrays.stream(comment.split("\n")).map(t -> t.trim()).collect(Collectors.joining("\n\n")));
    }

    public Asciidoc list(String log) {
        return writeln("* %s", log);
    }

}