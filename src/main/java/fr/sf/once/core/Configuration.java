package fr.sf.once.core;

import java.lang.reflect.Constructor;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.BasicComparator;
import fr.sf.once.model.Code;

public class Configuration {
    private int tailleMin;
    private final Class<? extends CodeComparator> comparatorClass;

    public Configuration() {
        this(BasicComparator.class);
    }

    public Configuration(Class<? extends CodeComparator> comparatorClass) {
        this.comparatorClass = comparatorClass;
    }

    public int getTailleMin() {
        return tailleMin;
    }

    public void setTailleMin(int tailleMin) {
        this.tailleMin = tailleMin;
    }
    
    public Configuration withTailleMin(int tailleMin) {
        setTailleMin(tailleMin);
        return this;
    }

    public CodeComparator getComparateur(Code code) {
        try {
            Constructor<? extends CodeComparator> constructor = comparatorClass.getConstructor(Code.class);
            return constructor.newInstance(code);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}