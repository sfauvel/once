package fr.sf.once.test;

import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class StringWriterLogRule implements TestRule {

    private StringWriter stringWriter = new StringWriter();

    public StringWriterLogRule(Logger logResult, Level level) {
        WriterAppender writerAppender = new WriterAppender(new PatternLayout("%m\n"), stringWriter);
        logResult.addAppender(writerAppender);
        logResult.setLevel(level);
    }

    @Override
    public Statement apply(Statement arg0, Description arg1) {
        return arg0;
    }

    public String getLog() {
        return stringWriter.toString();
    }

}