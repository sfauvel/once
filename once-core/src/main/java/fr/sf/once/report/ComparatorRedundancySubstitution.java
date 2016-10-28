package fr.sf.once.report;

import java.util.Comparator;

import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;

class ComparatorRedundancySubstitution implements Comparator<FunctionalRedundancy> {
    /**
     * 
     */
    final Code code;
    public ComparatorRedundancySubstitution(final Code code) {
        this.code = code;
    }
    
    @Override
    public int compare(FunctionalRedundancy redundancy1, FunctionalRedundancy redundancy2) {
        int size1 = redundancy1.getSubstitutionList().size();
        int size2 = redundancy2.getSubstitutionList().size();
        
        return (redundancy2.getDuplicatedTokenNumber() / (size2 + 1)) - (redundancy1.getDuplicatedTokenNumber() / (size1 + 1));
    }

}