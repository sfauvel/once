package fr.sf.once.report;

import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redondance;
import fr.sf.once.model.Token;

public interface Reporting {

    Logger TRACE_TOKEN = Logger.getLogger("TOKEN");
    Logger LOG_RESULTAT = Logger.getLogger("RESULTAT");
    Logger LOG_CSV = Logger.getLogger("CSV");
    void afficherRedondance(final ManagerToken manager, int tailleMin, List<Redondance> listeRedondance);
    void afficherCodeRedondant(final List<Token> tokenList, Redondance redondance);
    void afficheListeToken(List<Token> listeToken);
    void display(final Code code);
}
