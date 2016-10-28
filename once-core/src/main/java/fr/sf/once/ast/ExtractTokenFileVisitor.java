package fr.sf.once.ast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.sf.commons.Files;
import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Token;

public class ExtractTokenFileVisitor extends TravelAst implements Files.FileVisitor {
    private final List<Token> tokenList = new ArrayList<Token>();
    private List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
    private TokenVisitorBuilder tokenVisitorBuilder;
    private String rootPath;

    public ExtractTokenFileVisitor() {
        this("", null);
    }

    public ExtractTokenFileVisitor(String rootPath, String sourceEncoding) {
        this(rootPath, sourceEncoding, new TokenVisitorBuilder());
    }

    public ExtractTokenFileVisitor(String rootPath, String sourceEncoding, TokenVisitorBuilder tokenVisitorBuilder) {
        super(sourceEncoding);
        this.rootPath = rootPath.replaceAll("/", "\\\\") + "\\";
        this.tokenVisitorBuilder = tokenVisitorBuilder;
    }
    
    public void visit(final File file) {
        if (isJavaFile(file)) {
            try (FileInputStream in = new FileInputStream(file)) {
                TokenVisitor tokenVisitor = tokenVisitorBuilder.build(file, file.getPath().replace(rootPath, ""), methodList, tokenList.size());
                tokenList.addAll(extractToken(in, tokenVisitor));
                LOG.info(file.getName() + ": " + tokenList.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error("Error reading file " + file.getName(), e);
            } catch (Error e) {
                LOG.error("Error parsing file " + file.getName(), e);
            } catch (IOException e) {
                LOG.error("Error closing file " + file.getName(), e);
            }
        }
    }

    private boolean isJavaFile(final File file) {
        return file.isFile() && file.getName().endsWith(".java");
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public List<MethodLocalisation> getMethodList() {
        return methodList;
    }
}