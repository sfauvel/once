package fr.sf.once.report;

import java.util.List;

import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.model.MethodDefinition;
import fr.sf.once.model.Redundancy;

public class ReportingMethodPurcentage {

    private final RedundancyFinder manager;
    private final List<Redundancy> redundancyList;

    public ReportingMethodPurcentage(RedundancyFinder manager, List<Redundancy> redundancyList) {
        this.manager = manager;
        this.redundancyList = redundancyList;
    }

    public int getPurcentageBetween(MethodDefinition methodA, MethodDefinition methodB) {
        int totalTokenRedundancy = 0;
        for (Redundancy redundancy : redundancyList) {
            if (isRedundancyBetweenMethods(methodA, methodB, redundancy)) {
                totalTokenRedundancy += redundancy.getDuplicatedTokenNumber();
            }
        }
        return totalTokenRedundancy * 100 / methodA.tokenNumber();
    }

    private boolean isRedundancyBetweenMethods(MethodDefinition methodA, MethodDefinition methodB, Redundancy redundancy) {
        boolean isInMethodA = false;
        boolean isInMethodB = false;
        for (Integer startToken : redundancy.getStartRedundancyList()) {
            isInMethodA |= isRedundancyIsInMethod(methodA, startToken);
            isInMethodB |= isRedundancyIsInMethod(methodB, startToken);
        }

        return isInMethodA && isInMethodB;
    }

    private boolean isRedundancyIsInMethod(MethodDefinition method, Integer startToken) {
        return startToken >= method.getStartToken() && startToken <= method.getEndToken();
    }

}
