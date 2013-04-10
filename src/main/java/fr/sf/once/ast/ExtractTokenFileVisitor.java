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

public class ExtractTokenFileVisitor extends ParcoursAst implements Files.FileVisitor {
    private final List<Token> tokenList = new ArrayList<Token>();
    private List<MethodLocalisation> methodList = new ArrayList<MethodLocalisation>();
    private String rootPath;

    public ExtractTokenFileVisitor() {
        this("", null);
    }

    public ExtractTokenFileVisitor(String rootPath, String sourceEncoding) {
        super(sourceEncoding);
        this.rootPath = rootPath.replaceAll("/", "\\\\") + "\\";
    }

    public void visit(final File file) {
        String fileName = file.getName();
        if (file.isFile() && fileName.endsWith(".java")) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                TokenVisitor tokenVisitor = new TokenVisitorInMethod(file.getPath().replace(rootPath, ""), methodList);
                tokenList.addAll(extraireToken(in, tokenVisitor));
                LOG.info(fileName + ": " + tokenList.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error("Erreur de lecture du fichier " + fileName, e);
            } catch (Error e) {
                LOG.error("Erreur de parsing du fichier " + fileName, e);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    LOG.error("Erreur de fermeture du fichier " + fileName, e);
                }
            }
        }
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public List<MethodLocalisation> getMethodList() {
        return methodList;
    }
}