package fr.sf.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import org.apache.log4j.Logger;

public class Files {

    public static final Logger LOG = Logger.getLogger(Files.class);

    public static interface FileVisitor {
        void visit(File file);
    }

    /**
     * @param dir
     * @param visitor
     * @throws FileNotFoundException
     */
    public static void visitFile(String dir, Files.FileVisitor visitor) throws FileNotFoundException {
        visitFile(new File(dir), visitor);
    }

    public static void visitFile(File rootFile, Files.FileVisitor visitor) throws FileNotFoundException {
        LOG.debug("Resource: " + rootFile.getName());
        if (!rootFile.isFile()) {

            for (File file : getFiles(rootFile)) {
                visitor.visit(file);
                visitFile(file, visitor);
            }
        }
        LOG.debug("End of resource: " + rootFile.getName());
    }

    private static File[] getFiles(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }

        return Optional.ofNullable(file.listFiles()).orElse(new File[0]);
    }
}