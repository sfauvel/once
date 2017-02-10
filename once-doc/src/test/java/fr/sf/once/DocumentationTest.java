package fr.sf.once;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DocumentationTest {
    

    
    @Test
    public void should_build_a_path() throws Exception {
        Path rootPath = Paths.get(".", "root");
        Assertions.assertThat(rootPath.toString()).isEqualTo(".\\root");
        
        Assertions.assertThat(rootPath.resolve("subdir").toString()).isEqualTo(".\\root\\subdir");
        
    }
}
