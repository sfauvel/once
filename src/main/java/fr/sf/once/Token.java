/**
 * 
 */
package fr.sf.once;

import java.util.Comparator;

public class Token {
	
	static class ComparateurValeurToken implements Comparator<Token> {
		@Override
		public int compare(Token token1, Token token2) {
			return token1.getValeurToken().compareTo(token2.getValeurToken());
		}
		
	}
	public static class Type {
	    public static final Type VALEUR = new Type();
	    public static final Type BREAK = new Type(); // Valeur coupant la redonance même si le caractère est identique..
	    public static final Type NON_SIGNIFICATIF = new Type();
	}
	
	private final String valeurToken;
	private final Type type;
	private final Localisation localisation;
	
	public Token(Localisation localisation, String token, Type type) {
		this.localisation = localisation;
		this.valeurToken = token;
        this.type = type;
	}
	
	public String getValeurToken() {
		return this.valeurToken;
	}

    public Localisation getlocalisation() {
        return localisation;
    }

    public Type getType() {
        return type;
    }
    
    public Integer getLigneDebut() {
        return localisation.getLigne();
    }
    public Integer getColonneDebut() {
        return localisation.getColonne();
    }

    public boolean isType(Type value) {
        return type.equals(value);
    }
}