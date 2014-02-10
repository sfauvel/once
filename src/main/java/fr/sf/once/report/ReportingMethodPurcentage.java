package fr.sf.once.report;

import java.util.Arrays;
import java.util.List;

import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.MethodDefinition;
import fr.sf.once.model.Redundancy;

public class ReportingMethodPurcentage {

    private final ManagerToken manager;
    private final List<Redundancy> redundancyList;

    public ReportingMethodPurcentage(ManagerToken manager, List<Redundancy> redundancyList) {
        this.manager = manager;
        this.redundancyList = redundancyList;
    }

    public int getPurcentageBetween(MethodDefinition methodA, MethodDefinition methodB) {
        int redundancyNumber = 0; 
        for (Redundancy redundancy : redundancyList) {
            boolean isInMethodA = false;
            boolean isInMethodB = false;
            for (Integer startToken : redundancy.getStartRedundancyList()) {
                isInMethodA |= isRedundancyIsInMethod(methodA, startToken);
                isInMethodB |= isRedundancyIsInMethod(methodB, startToken);
            }
            
            if (isInMethodA && isInMethodB) {
                redundancyNumber = redundancy.getDuplicatedTokenNumber() * 100 / methodA.tokenNumber();
            }
        }
        return redundancyNumber;
    }

    private boolean isRedundancyIsInMethod(MethodDefinition method, Integer startToken) {
        return startToken >= method.getStartToken() && startToken <= method.getEndToken();
    }

}
