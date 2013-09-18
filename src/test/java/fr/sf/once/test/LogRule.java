package fr.sf.once.test;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class LogRule  implements TestRule {

    @Override
    public Statement apply(Statement paramStatement, Description paramDescription) {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.addAppender(new ConsoleAppender(new SimpleLayout()));
        rootLogger.setLevel(Level.ERROR);
        return paramStatement;
    }

}
