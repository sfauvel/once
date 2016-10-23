package fr.sf.once.report;

import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public interface Reporting {

    Logger TRACE_TOKEN = Logger.getLogger("TOKEN");
    Logger LOG_RESULTAT = Logger.getLogger("RESULTAT");
    Logger LOG_CSV = Logger.getLogger("CSV");
    void afficherRedondance(final Code code, int tailleMin, List<FunctionalRedundancy> listeRedondance);
    void afficherCodeRedondant(final Code code, FunctionalRedundancy redondance);
    void afficheListeToken(List<Token> listeToken);
    void display(final Code code);
}
