package fr.sf.commons;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import fr.sf.once.core.ManagerToken;
import fr.sf.once.report.Reporting;

public class FilesTest {

    @Test
    public void when_file_does_not_exist_an_excpetion_is_thrown() throws Exception {
        try {
            Files.visitFile("/NoExistingFolder", new Files.FileVisitor() {
                @Override
                public void visit(File file) {
                }
            });
        } catch (Exception r) {
             return;
        }

        fail("Exception should have been throw");
    }
}
