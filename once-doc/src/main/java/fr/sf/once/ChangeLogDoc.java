package fr.sf.once;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import fr.sf.once.Documentation.AsciidocWriter;

public class ChangeLogDoc {

    public static final String OUTPUT_FILE_NAME = "changelog";
    private static FileWriter fileWriter;
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();

    public void generate() throws FileNotFoundException {
        List<String> logHistory = Documentation.getLogHistory();

        try (AsciidocWriter adoc = new AsciidocWriter(outputFile)) {
            for (String log : logHistory) {
                adoc.list(log);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
