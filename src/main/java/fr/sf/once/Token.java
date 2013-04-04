/**
 * 
 */
package fr.sf.once;


public class Token {
	
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
}