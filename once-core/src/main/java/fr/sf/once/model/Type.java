package fr.sf.once.model;

public class Type {
    public static final Type VALUE = new Type("Value");
    public static final Type BREAK = new Type("Break"); // Value stopping redundancy event value are identical.
    public static final Type NOT_SIGNIFICANT = new Type("Ponctuation");
    public static final Type KEYWORD = new Type("Keyword");

    private final Type parentType;
    private final String name;
    public Type(String name, Type parentType) {
        this.name = name;
        this.parentType = parentType;
    }

    public Type(String name) {
        this(name, null);
    }

    public boolean is(Type type) {
        return this.equals(type) || type.equals(parentType);
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}