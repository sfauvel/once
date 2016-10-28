package fr.sf.once.core;

import java.lang.reflect.Constructor;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.BasicComparator;
import fr.sf.once.model.Code;

public class Configuration {
    private static final int DEFAULT_MINIMAL_TOKEN_NUMBER = 20;
    
    private final int minimalTokenNumber;
    private final Class<? extends CodeComparator> comparatorClass; 
    
    public Configuration() {
        this(BasicComparator.class);
    }

    public Configuration(Class<? extends CodeComparator> comparatorClass) {
        this(comparatorClass, DEFAULT_MINIMAL_TOKEN_NUMBER);
    }     
    
    public Configuration(Class<? extends CodeComparator> comparatorClass, int minimalTokenNumber) {
        this.comparatorClass = comparatorClass;
        this.minimalTokenNumber = minimalTokenNumber;
    }

    public int getMinimalTokenNumber() {
        return minimalTokenNumber;
    }

    public Configuration withMinimalTokenNumber(int minimalTokenNumber) {
        return new Configuration(this.comparatorClass, minimalTokenNumber);
    }
    
    public CodeComparator getComparator(Code code) {
        try {
            Constructor<? extends CodeComparator> constructor = comparatorClass.getConstructor(Code.class);
            return constructor.newInstance(code);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public Configuration withComparator(Class<? extends CodeComparator> codeComparator) {
        return new Configuration(codeComparator, this.minimalTokenNumber);
    }
}