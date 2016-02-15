package fr.sf.once.ast;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ExtractTokenFileVisitorTest {
    
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();
    
    @Test
    public void should_do_nothing_when_not_java_file() throws Exception {
        ExtractTokenFileVisitor visitor = new ExtractTokenFileVisitor(tmp.getRoot().getAbsolutePath(), "UTF-8");
        File javaFile = tmp.newFile("file.txt");
        visitor.visit(javaFile);
    }

    @Test
    public void should_do_XXX_when_java_file() throws Exception {
        ExtractTokenFileVisitor visitor = new ExtractTokenFileVisitor(tmp.getRoot().getAbsolutePath(), "UTF-8");
        File javaFile = tmp.newFile("file.java");
        visitor.visit(javaFile);
    }
}
