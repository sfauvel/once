package fr.sf.once.report;

import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public interface Reporting {

    Logger TRACE_TOKEN = Logger.getLogger("TOKEN");
    Logger LOG_RESULTAT = Logger.getLogger("RESULTAT");
    Logger LOG_CSV = Logger.getLogger("CSV");
    void afficherRedondance(final Code code, int tailleMin, List<Redundancy> listeRedondance);
    void afficherCodeRedondant(Code code, Redundancy redondance);
    void afficheListeToken(List<Token> listeToken);
    void display(final Code code);
}
