package fr.sf.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FilesTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private final FileVisitorRecorder VISITOR_RECORDER = new FileVisitorRecorder();

    @Before
    public void initFolder() throws IOException {
        tmp.newFolder("folderA");
        tmp.newFolder("folderB");
        tmp.newFolder("folderA","subFolderA_A");
        tmp.newFile("folderA/subFolderA_A/FileA_A_A");
        tmp.newFile("folderA/subFolderA_A/FileA_A_B");
    }

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
        Files.visitFile(tmp.getRoot().getAbsolutePath(), VISITOR_RECORDER);
        assertThat(VISITOR_RECORDER.fileVisited).containsExactly("folderA", "subFolderA_A", "FileA_A_A", "FileA_A_B", "folderB");
    }

    @Test
    public void should_do_nothing_when_folder_is_empty() throws Exception {
        Path path = Paths.get(tmp.getRoot().getAbsolutePath(), "folderB");

        Files.visitFile(path.toString(), VISITOR_RECORDER);
        assertThat(VISITOR_RECORDER.fileVisited).isEmpty();
    }

    @Test
    public void should_do_nothing_when_a_file_is_given() throws Exception {
        Path path = Paths.get(tmp.getRoot().getAbsolutePath(), "folderA", "subFolderA_A", "FileA_A_A");

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
