package fr.sf.once.ast;

import fr.sf.once.model.Type;

public class TypeJava {

    public static final Type STRING = new Type("String", Type.VALEUR);
    public static final Type VARIABLE = new Type("Variable", Type.VALEUR);
    public static final Type METHOD = new Type("Method", Type.VALEUR);
    public static final Type CLASS = new Type("Class", Type.VALEUR);
}
