package fr.sf.once;

import java.util.ArrayList;
import java.util.List;

import fr.sf.once.Token.Type;

public class LecteurTokens extends Lecteur {
	
	List<Token> _listeTokens = new ArrayList<Token>();
	
	public LecteurTokens(String nomFichier) {
		super(nomFichier);
	}
	
	@Override
	protected void traiterLigne(String ligneDonnee) {
		super.traiterLigne(ligneDonnee);
		String[] listeElement = ligneDonnee.split(":");
		Localisation position = new Localisation(listeElement[0], 0, 0);
		this._listeTokens.add(new Token(position, listeElement[1], Type.VALEUR));
	}

	public List<Token> getTokens() {
		return _listeTokens;
	}

}
