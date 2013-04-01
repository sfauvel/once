package fr.sf.once;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Lecteur {

	private String _nomFichier;
	private int _nombreLigne = 0;
	
	public Lecteur(String nomFichier) {
		this._nomFichier = nomFichier;

	}

	public void lire() throws IOException {
		BufferedReader lecteurBufferise = null;
		
		boolean eof = false;
		try {
			lecteurBufferise = new BufferedReader(new FileReader(this._nomFichier));
			while (eof != true) {
		
				String ligneDonnee = lecteurBufferise.readLine();
				if (ligneDonnee != null) {
					traiterLigne(ligneDonnee);
				} else {
					eof = true;
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Fichier Non Trouvé !!");
			throw ex;
		} catch (IOException ex) {
			System.out.println("Erreur lecture ligne fichier !!");
			throw ex;
		} finally {
			try {
				if (lecteurBufferise != null) {
					lecteurBufferise.close();
				}
			} catch (IOException ex1) {
				System.out.println("Erreur fermeture fichier !!");
			}
		}
	}
	

	protected void traiterLigne(String ligneDonnee) {
		this._nombreLigne ++;
		
	}

	public int getNombreLigne() {
		return this._nombreLigne;
	}

}
