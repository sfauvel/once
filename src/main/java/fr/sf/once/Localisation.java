package fr.sf.once;


public class Localisation {
    private final String nomFichier;
    private final int ligne;
    private final int colonne;
    
    public Localisation(String nomFichier, int ligne, int colonne) {
        super();
        this.nomFichier = nomFichier;
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    public String getNomFichier() {
        return nomFichier;
    }
    public int getLigne() {
        return ligne;
    }
    public int getColonne() {
        return colonne;
    }

}