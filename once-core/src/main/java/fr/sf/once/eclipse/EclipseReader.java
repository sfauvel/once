package fr.sf.once.eclipse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Read a report to display it int Eclipse console and to have shortcut to code.
 * 
 * Main take one argument, the report file.
 */
public class EclipseReader {

    public static void main(String[] args) {
        File file = new File(args[0]);
        System.out.println(file.getAbsolutePath());
        
        Pattern p = Pattern.compile("(.*)\\)(.*)\\.(.*)\\.(.*) from line (\\d+) to (\\d+) \\((.*)");
        try (Stream<String> stream = Files.lines(Paths.get(args[0]))) {

            stream.forEach(s -> {
                Matcher m = p.matcher(s);
                if (m.find()) {
                    int groupNumber = 1;
                    String beginning = m.group(groupNumber++);
                    String packageName = m.group(groupNumber++);
                    String className = m.group(groupNumber++);
                    String methodName = m.group(groupNumber++);
                    String firstLine = m.group(groupNumber++);
                    String lastLine = m.group(groupNumber++);
                    String ending = m.group(groupNumber++);
                    System.out.printf("%s) %s.%s.%s(%s.java:%s) (%s.java:%s) (%s%n", beginning, packageName,className, methodName, className, firstLine, className, lastLine, ending);
                } else {
                    System.out.println(s);
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
