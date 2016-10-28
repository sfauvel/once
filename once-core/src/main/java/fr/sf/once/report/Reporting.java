package fr.sf.once.report;

import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public interface Reporting {

    Logger TRACE_TOKEN = Logger.getLogger("TOKEN");
    Logger LOG_RESULT = Logger.getLogger("RESULT");
    Logger LOG_CSV = Logger.getLogger("CSV");
    void displayRedundancy(final Code code, int minimalSize, List<FunctionalRedundancy> redundancyList);
    void displayRedundantCode(final Code code, FunctionalRedundancy redundancy);
    void displayTokenList(List<Token> tokenList);
    void display(final Code code);
}
