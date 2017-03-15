package fr.sf.once;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Documentation {
    public static final Path ASCIIDOC_OUTPUT_PATH = Paths.get("docs", "asciidoc");
    public static final Path HTML_OUTPUT_PATH = Paths.get("docs", "html");
    public static final Path STYLESHEET_PATH = Paths.get("src", "main", "resources", "livingdoc.css");
    public static final Path ACSIIDOC_BIN_PATH = Paths.get("C:", "My Program Files", "asciidoc", "asciidoc-8.6.9", "asciidoc.py");

    public static void main(String[] args) throws Exception {

        assertPathExists(ASCIIDOC_OUTPUT_PATH);
        assertPathExists(HTML_OUTPUT_PATH);

        generateMainDocumentation();

        Documentation doc = new Documentation();
        doc.generateTestDoc();
        doc.generateConfigurationFile();
        doc.generateDomainDoc();
        doc.generateChangeLog();

        doc.generateArchi();
        // doc.allFiles(".\\src\\main\\java");

        generateMainDocumentation();
    }

    public static final String OUTPUT_FILE_NAME = "once";

    public static class AsciidocWriter extends Asciidoc implements Closeable {

        public AsciidocWriter(File file) throws IOException {
            super(new FileWriter(file));
        }

        @Override
        public void close() throws IOException {
            getWriter().close();
        }

    }

    private static void generateMainDocumentation() throws IOException {

        try (AsciidocWriter adoc = new AsciidocWriter(Documentation.ASCIIDOC_OUTPUT_PATH.resolve(OUTPUT_FILE_NAME + ".asciidoc").toFile())) {
            adoc
                    .title(1, "Once")
                    .tableOfContent()
                    .split()
                    .blankLine();

            adoc
                    .changeLevelOffset(1)
                    .include(DomainDoclet.OUTPUT_FILE_NAME + ".asciidoc")
                    .include(ConfigurationDoclet.OUTPUT_FILE_NAME + ".asciidoc")
                    .include(ArchiDoc.OUTPUT_FILE_NAME + ".asciidoc")
                    .changeLevelOffset(0);

            adoc
                    .title(2, "Other resources")
                    .link("testdoc.html", "Test plan")
                    .link("changelog.html", "Change log");

            adoc.blankLine();

        }

        generateHtmlFromAsciidoc(Documentation.ASCIIDOC_OUTPUT_PATH, Documentation.HTML_OUTPUT_PATH, OUTPUT_FILE_NAME);

    }

    private static void assertPathExists(Path path) {
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
    }

    private void generateArchi() throws FileNotFoundException {
        new ArchiDoc().generateArchi();
        Documentation.generateHtmlFromAsciidoc(Documentation.ASCIIDOC_OUTPUT_PATH, Documentation.HTML_OUTPUT_PATH, "archi");
        
    }

    private void generateConfigurationFile() throws Exception {
        generateJavadoc("GenerateConfigurationDoc", ConfigurationDoclet.class, Paths.get("..", "once-core", "src", "main", "java"), "fr.sf");

        Documentation.generateHtmlFromAsciidoc(Documentation.ASCIIDOC_OUTPUT_PATH, Documentation.HTML_OUTPUT_PATH, "configuration");
        // new ConfigurationDoc().generateConfigurationFile();
    }

    public void generateDomainDoc() throws FileNotFoundException {
        generateJavadoc("GenerateDomainDoc", DomainDoclet.class, Paths.get("..", "once-core", "src", "main", "java"), "fr.sf");
        generateHtmlFromAsciidoc(ASCIIDOC_OUTPUT_PATH, HTML_OUTPUT_PATH, DomainDoclet.OUTPUT_FILE_NAME);
    }

    public void generateTestDoc() {
        generateJavadoc("GenerateTestDoc", TestDoclet.class, Paths.get("..", "once-core", "src", "test", "java"), "fr.sf");
        generateHtmlFromAsciidoc(ASCIIDOC_OUTPUT_PATH, HTML_OUTPUT_PATH, TestDoclet.OUTPUT_FILE_NAME);
    }
    
    private void generateChangeLog() throws FileNotFoundException {
        new ChangeLogDoc().generate();
        Documentation.generateHtmlFromAsciidoc(Documentation.ASCIIDOC_OUTPUT_PATH, Documentation.HTML_OUTPUT_PATH, "changelog");
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

    public static List<String> getLogHistory() {
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "git",
                    "log",
                    "--pretty=format:%s" });

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
