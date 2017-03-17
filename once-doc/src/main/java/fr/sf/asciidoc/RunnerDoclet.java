package fr.sf.asciidoc;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

/**
 * To run a doclet, create an instance of the RunnerDoclet with a doclet instance.
 * The runner implements static methods of doclet and delegate execution to the doclet instance.
 *  
 * <pre>
 * {@code
 * new RunnerDoclet(new ConfigurationDoclet(), path, packageName).execute();
 * }
 * </pre>
 */
public class RunnerDoclet {

    private final CustomDoclet doclet;

    private Path path;
    private String packageName;
    private Map<String, Integer> options = new HashMap<String, Integer>();

    public RunnerDoclet(CustomDoclet doclet, Path path, String packageName) {
        this.doclet = doclet;
        this.path = path;
        this.packageName = packageName;
//        options.put("-tag", 2);
    }

    public void execute(Class<?> docletMainClass) {
        String[] javadocargs = getJavadocArgs();
        com.sun.tools.javadoc.Main.execute("", docletMainClass.getName(), javadocargs);
    }

    private String[] getJavadocArgs() {
        String[] javadocargs = {
                "-encoding", "utf8",
                "-private",
                "-sourcepath", path.toString(),
                "-subpackages", packageName,
//                "-tag", "Test"
        };
        return javadocargs;
    }

    private static RunnerDoclet docletRunner;

    /**
     * Execute the runner setting static variables.
     * Every call will be delegate to the doclet instance.
     */
    public void execute() {
        docletRunner = this;
        execute(RunnerDoclet.class);
    }

    public static boolean start(RootDoc root) {
//        String tagName = readOptions(root.options(), "-tag");
        return docletRunner.doclet.start(root);
    }

    protected static String readOptions(String[][] options, String parameter) {
        String tagName = null;
        for (int i = 0; i < options.length; i++) {
            String[] opt = options[i];
            if (opt[0].equals(parameter)) {
                tagName = opt[1];
            }
        }
        return tagName;
    }
    
    public static int optionLength(String option) {
        return docletRunner.optionLengthDoclet(option);
    }

    public static boolean validOptions(String options[][], DocErrorReporter reporter) {
        return docletRunner.validOptionsDoclet(options, reporter);
    }
    
    public int optionLengthDoclet(String option) {
        return options.getOrDefault(option, 0);
    }
    
    public boolean validOptionsDoclet(String options[][], DocErrorReporter reporter) {
//        boolean foundTagOption = false;
//        for (int i = 0; i < options.length; i++) {
//            String[] opt = options[i];
//            if (opt[0].equals("-tag")) {
//                if (foundTagOption) {
//                    reporter.printError("Only one -tag option allowed.");
//                    return false;
//                } else {
//
//                    foundTagOption = true;
//                }
//            }
//        }
//        if (!foundTagOption) {
//            reporter.printError("Usage: javadoc -tag mytag -doclet ListTags ...");
//        }
//        return foundTagOption;
        return true;
    }


}
