package fr.sf.once;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.sf.once.test.UtilsFichier;


public class LecteurTest {
	
	static final String NOM_FICHIER = "./target/fichier.test";
	
	class MockLecteur extends Lecteur {

		public List<String> _lignesLues = new ArrayList<String>();
		
		public MockLecteur(String nomFichier) {
			super(nomFichier);
		}

		@Override
		protected void traiterLigne(String ligneDonnee) {
			super.traiterLigne(ligneDonnee);
			this._lignesLues.add(ligneDonnee);
		}
			
	}
	
	@Test
	public void testFichierInexistant() {
		try {
			Lecteur lecteur = new Lecteur("FichierInexistant");
			lecteur.lire();
			fail("Une exception aurait du être levée");
		} catch (Exception e ){
			// On doit passer par là
		}
		
	}

	@Test
	public void testFichierVide() throws IOException {
		
		UtilsFichier.creerFichierTest(NOM_FICHIER, "");
		Lecteur lecteur = new Lecteur(NOM_FICHIER);
		lecteur.lire();
		assertEquals(0, lecteur.getNombreLigne());
	}

	@Test
	public void testLectureUneLigne() throws IOException {
		UtilsFichier.creerFichierTest(NOM_FICHIER, "Fichier de test");
		MockLecteur lecteur = new MockLecteur(NOM_FICHIER);
		lecteur.lire();
		assertEquals(1, lecteur.getNombreLigne());
		assertEquals("Fichier de test", lecteur._lignesLues.get(0));
	}
	
	@Test
	public void testAppelLecteurPlusieursLignes() throws IOException {
		UtilsFichier.creerFichierTest(NOM_FICHIER, "Fichier de test\nLigne 2\nLigne 3");
		MockLecteur lecteur = new MockLecteur(NOM_FICHIER);
		lecteur.lire();
		assertEquals(3, lecteur.getNombreLigne());
		assertEquals("Fichier de test", lecteur._lignesLues.get(0));
		assertEquals("Ligne 2", lecteur._lignesLues.get(1));
		assertEquals("Ligne 3", lecteur._lignesLues.get(2));
	}
	

}
