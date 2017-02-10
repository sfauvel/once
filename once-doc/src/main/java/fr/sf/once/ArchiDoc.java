package fr.sf.once;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class ArchiDoc {

    private void generateGraph(Path asciidocOutputPath, String filename) throws FileNotFoundException {
        try (PrintWriter print = new PrintWriter(asciidocOutputPath.resolve(filename + ".dot").toFile())) {
            print.println("digraph G {");
            print.println("graph[splines=false];");
            print.println("node [shape=box];");
            // A simple dependance
            print.println("Class_A -> Class_B");
            print.println("}");
        }
        
        // A simple file with a link to the image
        try (PrintWriter print = new PrintWriter(asciidocOutputPath.resolve("archi" + ".asciidoc").toFile())) {
            print.println("\n=== Archi");
            print.println("Description de l'archi");
            print.println("image:"+filename+".png[Archi]");
        }
    }

    public void generateArchi() throws FileNotFoundException {
        String filename = "sample";
        generateGraph(Documentation.ASCIIDOC_OUTPUT_PATH, filename);
        Documentation.generateDotToPng(Documentation.ASCIIDOC_OUTPUT_PATH, Documentation.HTML_OUTPUT_PATH, filename);
        
        Documentation.generateHtmlFromAsciidoc(Documentation.ASCIIDOC_OUTPUT_PATH, Documentation.HTML_OUTPUT_PATH, "archi");
    }

}
