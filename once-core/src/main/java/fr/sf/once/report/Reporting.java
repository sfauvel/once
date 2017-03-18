package fr.sf.once.report;

import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.model.CodeAsATokenList;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public interface Reporting {

    Logger TRACE_TOKEN = Logger.getLogger("TOKEN");
    Logger LOG_RESULT = Logger.getLogger("RESULT");
    Logger LOG_CSV = Logger.getLogger("CSV");
    void displayRedundancy(final CodeAsATokenList code, int minimalSize, List<Redundancy> redundancyList);
    void displayRedundantCode(final CodeAsATokenList code, Redundancy redundancy);
    void displayTokenList(List<Token> tokenList);
    void display(final CodeAsATokenList code);
}
