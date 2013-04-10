package fr.sf.once.core;

import java.lang.reflect.Constructor;

import fr.sf.once.comparator.Comparateur;
import fr.sf.once.comparator.ComparateurSansSubstitution;
import fr.sf.once.model.Code;

public class Configuration {
    private int tailleMin;
    private final Class<? extends Comparateur> comparatorClass;

    public Configuration() {
        this(ComparateurSansSubstitution.class);
    }

    public Configuration(Class<? extends Comparateur> comparatorClass) {
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

    public Comparateur getComparateur(Code code) {
        try {
            Constructor<? extends Comparateur> constructor = comparatorClass.getConstructor(Code.class);
            return constructor.newInstance(code);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}