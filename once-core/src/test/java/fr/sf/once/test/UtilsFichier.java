package fr.sf.once.test;

import java.io.FileWriter;
import java.io.IOException;

public class UtilsFichier {
	static public void creerFichierTest(String fichier, String contenu) {
		FileWriter sortie = null;
		try {
			sortie= new FileWriter(fichier);
			sortie.write(contenu);
		}
		catch (Exception e) {
			
		}
		finally {
			if (sortie != null) {				
				try {
					sortie.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
