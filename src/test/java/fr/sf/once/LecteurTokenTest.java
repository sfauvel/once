package fr.sf.once;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import fr.sf.once.test.UtilsFichier;


public class LecteurTokenTest {
	

	static final String NOM_FICHIER = "./target/fichier.test";
	
	@Test
	public void testFichierVide() throws IOException {

		UtilsFichier.creerFichierTest(NOM_FICHIER, "");
		LecteurTokens lecteur = new LecteurTokens(NOM_FICHIER);
		lecteur.lire();
		List<Token> listeTokens = lecteur.getTokens();
		assertEquals(0, listeTokens.size());		
	}

	@Test
	public void testGetTokens() throws IOException {

		UtilsFichier.creerFichierTest(NOM_FICHIER, "Fichier:Token");
		LecteurTokens lecteur = new LecteurTokens(NOM_FICHIER);
		lecteur.lire();
		List<Token> listeTokens = lecteur.getTokens();
		assertEquals(1, listeTokens.size());
		Token token = listeTokens.get(0);
		assertEquals("Fichier", token.getlocalisation().getNomFichier());
		assertEquals("Token", token.getValeurToken());
		
	}

	@Test
	public void testGetLigneIncorrect() throws IOException {

		UtilsFichier.creerFichierTest(NOM_FICHIER, "Fichier");
		LecteurTokens lecteur = new LecteurTokens(NOM_FICHIER);
		try {
			lecteur.lire();
			fail("Une exception aurait du être levée");
		} catch (Exception e) {
			// On doit passer par là;
		}
		
	}
	

}
