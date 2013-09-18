package fr.sf.commons;

import java.io.File;

import org.apache.log4j.Logger;

public class Files {

    public static final Logger LOG = Logger.getLogger(Files.class);

    public static interface FileVisitor {
        void visit(File file);
    }

    public static void visitFile(String dir, Files.FileVisitor visitor) {
        File file = new File(dir);
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                visitor.visit(files[i]);
                if (files[i].isFile()) {
                    LOG.debug("File: " + files[i].getName());
                } else if (files[i].isDirectory()) {
                    LOG.debug("Dir: " + files[i].getName());
                    visitFile(files[i].getAbsolutePath(), visitor);
                    LOG.debug("End of dir: " + files[i].getName());
                }
            }
        }
    }
}