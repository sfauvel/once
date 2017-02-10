package fr.sf.once;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Documentation {
    public static final Path ASCIIDOC_OUTPUT_PATH = Paths.get("docs", "asciidoc");
    public static final Path HTML_OUTPUT_PATH = Paths.get("docs", "html");
    public static final Path STYLESHEET_PATH = Paths.get("src", "main", "resources", "livingdoc.css");
    public static final Path ACSIIDOC_BIN_PATH = Paths.get("C:", "My Program Files", "asciidoc", "asciidoc-8.6.9", "asciidoc.py");

    public static void main(String[] args) throws Exception {

        assertPathExists(ASCIIDOC_OUTPUT_PATH);
        assertPathExists(HTML_OUTPUT_PATH);

        Documentation doc = new Documentation();
         doc.generateTestDoc();
         doc.generateConfigurationFile();
         doc.generateDomainDoc();

        
        doc.generateArchi();
        // doc.allFiles(".\\src\\main\\java");

    }

    private static void assertPathExists(Path path) {
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
    }

    private void generateArchi() throws FileNotFoundException {
        new ArchiDoc().generateArchi();
    }

    private void generateConfigurationFile() throws Exception {
        new ConfigurationDoc().generateConfigurationFile();
    }

    public void generateDomainDoc() throws FileNotFoundException {
        generateJavadoc("GenerateDomainDoc", DomainDoclet.class, Paths.get("..", "once-core", "src", "main", "java"), "fr.sf");
        generateHtmlFromAsciidoc(ASCIIDOC_OUTPUT_PATH, HTML_OUTPUT_PATH, DomainDoclet.OUTPUT_FILE_NAME);
    }

    public void generateTestDoc() {
        generateJavadoc("GenerateTestDoc", TestDoclet.class, Paths.get("..", "once-core", "src", "test", "java"), "fr.sf");
        generateHtmlFromAsciidoc(ASCIIDOC_OUTPUT_PATH, HTML_OUTPUT_PATH, TestDoclet.OUTPUT_FILE_NAME);
    }

    public void allFiles(String rootPath) {
        Path dir = Paths.get(rootPath);
        if (dir.toFile().isFile()) {
            String className = dir.toString().replace(".\\src\\main\\java\\", "").replace(".java", "").replace("\\", ".");
            Class<?> clazz = getClazz(className);

            System.out.println("Class " + className);
        } else {
            try {
                Files.list(dir)
                        .forEach(path -> allFiles(path.toString()));
            } catch (IOException e) {
                System.out.println("Exception on " + rootPath.toString());
            }
        }
    }

    public void generateJavadoc(String name, Class<? extends Object> docletClass, Path folder, String packageName) {
        com.sun.tools.javadoc.Main.execute(name, docletClass.getName(),
                new String[] {
                        "-sourcepath", folder.toString(),
                        "-subpackages", packageName
                });
    }

    public static void generateHtmlFromAsciidoc(Path inputPath, Path outputPath, String filename) {

        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec(new String[] { "python", ACSIIDOC_BIN_PATH.toFile().getAbsolutePath(),
                    "-a", "stylesheet=" + STYLESHEET_PATH.toFile().getAbsolutePath(),
                    "-a", "stylesdir=./stylesheets",
                    "-o", outputPath.resolve(filename + ".html").toFile().getAbsolutePath(),
                    inputPath.resolve(filename + ".asciidoc").toFile().getAbsolutePath() });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateDotToPng(Path asciidocOutputPath, Path htmlOutputPath, String filename) {
        Runtime rt = Runtime.getRuntime();
        try {
            Path GRAPHVIZ_HOME = Paths.get("C:", "My Program Files", "Graphviz2.38", "bin");
            
            rt.exec(new String[] { GRAPHVIZ_HOME.resolve("dot").toFile().getAbsolutePath(),
                    "-Tpng",
                    "-o" + htmlOutputPath.resolve(filename + ".png").toFile().getAbsolutePath(),
                    "-v", asciidocOutputPath.resolve(filename + ".dot").toFile().getAbsolutePath() });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Class<?> getClazz(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Exception retrieving class " + className);
        }
        return null;
    }

}
