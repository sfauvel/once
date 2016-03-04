package fr.sf.once.comparator;

public abstract class AbstractSubstitutionList<T> {

    public static final int NOT_FOUND = -1;
    

    abstract public int getPosition(T token);
}
