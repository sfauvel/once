package fr.sf.once.model;

public class Type {
    public static final Type VALEUR = new Type("Value");
    public static final Type BREAK = new Type("Break"); // Valeur coupant la redonance
                                                 // même si le caractère est
                                                 // identique..
    public static final Type NON_SIGNIFICATIF = new Type("Ponctuation");

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