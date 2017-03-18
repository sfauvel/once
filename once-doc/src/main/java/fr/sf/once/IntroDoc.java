package fr.sf.once;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import fr.sf.once.Documentation.AsciidocWriter;

public class IntroDoc {
    public static final String OUTPUT_FILE_NAME = "intro";
    private static File outputFile = Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile();
    
    protected AsciidocWriter adoc;
    
    public void generate() {
        try (AsciidocWriter adoc = new AsciidocWriter(newOutputWriter())) {
            this.adoc = adoc;
            display();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.adoc = null;
        }
    }

    private void display() {

        adoc.title(1,  "Quick start");
      try {
          List<String> readAllLines = Files.readAllLines(Paths.get("..", "readme.md"), Charset.forName("UTF8"));
          boolean firstLine = true;
          for (String line : readAllLines) {
              
              if(firstLine) {
                  line = line.substring(1);
              }
              firstLine = false;
              
              int index = 0;
              while (line.length()>index && line.charAt(index) == '#') {
                  index ++;
              }
              if (index > 0) {
                  adoc.title(index, line.substring(index));
              } else {
                  adoc.writeln(line);
              }
          }
          
//          Files.copy(Paths.get("..", "readme.md"), Documentation.ASCIIDOC_OUTPUT_PATH.resolve("readme.md"), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
          e.printStackTrace();
      } 

    }

    private Writer newOutputWriter() throws IOException {
        return new FileWriter(outputFile);
    }

}
