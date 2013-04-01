package fr.sf.once;

import java.util.List;

import org.apache.log4j.Logger;

public interface Reporting {

    Logger TRACE_TOKEN = Logger.getLogger("TOKEN");
    Logger LOG_RESULTAT = Logger.getLogger("RESULTAT");
    Logger LOG_CSV = Logger.getLogger("CSV");
    void afficherRedondance(final List<Token> tokenList, int tailleMin, List<Redondance> listeRedondance);
    void afficherCodeRedondant(final List<Token> tokenList, Redondance redondance);
    void afficheListeToken(List<Token> listeToken);
    void display(final Code code);    
}
