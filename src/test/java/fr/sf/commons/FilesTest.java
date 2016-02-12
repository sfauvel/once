package fr.sf.commons;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FilesTest {

    private final FileVisitorRecorder VISITOR_RECORDER = new FileVisitorRecorder();

    @Test
    public void when_file_does_not_exist_an_excpetion_is_thrown() throws Exception {
        try {
            Files.visitFile("/NoExistingFolder", VISITOR_RECORDER);
        } catch (Exception r) {
            return;
        }

        fail("Exception should have been throw");
    }

    @Test
    public void should_visit_all_files() throws Exception {
        Path path = Paths.get("src", "test", "resources", getClass().getPackage().getName().replace('.', File.separatorChar) );
        
        Files.visitFile(path.toString(), VISITOR_RECORDER);
        assertThat(VISITOR_RECORDER.fileVisited).containsExactly(
                "folderA",
                "subFolderA_A",
                "FileA_A_A",
                "FileA_A_B",
                "folderB");
    }
    
    @Test
    public void should_do_nothing_when_folder_is_empty() throws Exception {
        Path path = Paths.get("src", "test", "resources", getClass().getPackage().getName().replace('.', File.separatorChar), "folderB" );
        
        Files.visitFile(path.toString(), VISITOR_RECORDER);
        assertThat(VISITOR_RECORDER.fileVisited).isEmpty();
    }
    
    @Test
    public void should_do_nothing_when_a_file_is_given() throws Exception {
        Path path = Paths.get("src", "test", "resources", getClass().getPackage().getName().replace('.', File.separatorChar), "folderA", "subFolderA_A", "FileA_A_A" );
        
        Files.visitFile(path.toString(), VISITOR_RECORDER);
        assertThat(VISITOR_RECORDER.fileVisited).isEmpty();
    }
    
    class FileVisitorRecorder implements Files.FileVisitor {
        final List<String> fileVisited = new ArrayList<String>();
        @Override
        public void visit(File file) {
            fileVisited.add(file.getName());
        }
    }
}
