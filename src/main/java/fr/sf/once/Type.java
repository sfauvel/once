package fr.sf.once;

public class Type {
    public static final Type VALEUR = new Type();
    public static final Type BREAK = new Type(); // Valeur coupant la redonance
                                                 // même si le caractère est
                                                 // identique..
    public static final Type NON_SIGNIFICATIF = new Type();

    private final Type parentType;
    public Type(Type parentType) {
        this.parentType = parentType;
    }

    public Type() {
        this(null);
    }

    public boolean is(Type type) {
        return this.equals(type) || type.equals(parentType);
    }
}