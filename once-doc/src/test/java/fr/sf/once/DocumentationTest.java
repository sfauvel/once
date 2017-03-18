package fr.sf.once;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DocumentationTest {
    

    
    @Test
    public void should_build_a_path() throws Exception {
        Path rootPath = Paths.get(".", "root");
        Assertions.assertThat(rootPath.toString())
                .isEqualTo(buildPath(".", "root"));
                
        Assertions.assertThat(rootPath.resolve("subdir").toString())
            .isEqualTo(buildPath(".", "root", "subdir"));
        
    }

    private String buildPath(String... folders) {
        return String.join(Character.toString(File.separatorChar), folders);
    }
}
