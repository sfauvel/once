package fr.sf.commons;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class FilesTest {

    @Test
    public void when_file_does_not_exist_an_excpetion_is_thrown_____() throws Exception {
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
