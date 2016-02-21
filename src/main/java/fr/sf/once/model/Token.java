/**
 * 
 */
package fr.sf.once.model;

import org.apache.commons.lang.StringUtils;



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

    public int getColonneFin() {
        return getColonneDebut() + valeurToken.length();
    }
    
    public String format() {

        StringBuffer buffer = new StringBuffer();
        appendToken(buffer);
        return buffer.toString();
    }

    public void appendToken(StringBuffer buffer) {
        Localisation localisation = getlocalisation();
        buffer.append(StringUtils.rightPad(getValeurToken(), 25));
        localisation.appendLocalisation(buffer);
        buffer.append(" col:")
                .append(StringUtils.rightPad(Integer.toString(localisation.getColonne()), 5))
                .append(" type:")
                .append(getType().toString());
    }

}