package fr.sf.once.ast;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import fr.sf.once.model.Token;

public class ExtractTokenFileVisitorTest {
    
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    TokenVisitorBuilder tokenVisitorBuidler = mock(TokenVisitorBuilder.class);
    
    private ExtractTokenFileVisitor createVisitor() {
        return new ExtractTokenFileVisitor(tmp.getRoot().getAbsolutePath(), "UTF-8", tokenVisitorBuidler) {
            @Override
            public List<Token> extraireToken(InputStream input, TokenVisitor tokenVisitor) {
                return Collections.emptyList();
            }
        };
    }
    
    @Test
    public void should_do_nothing_when_not_java_file() throws Exception {
        createVisitor().visit(tmp.newFile("file.txt"));
        
        Mockito.verifyZeroInteractions(tokenVisitorBuidler);
    }

    @Test
    public void should_do_XXX_when_java_file() throws Exception {
        createVisitor().visit(tmp.newFile("file.java"));
        
        Mockito.verify(tokenVisitorBuidler).build(any(File.class), anyString(), any(List.class), anyInt());
    }
}
