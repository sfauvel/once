package fr.sf.once.report;

import java.util.Comparator;

import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;

class ComparatorRedundancySubstitution implements Comparator<Redundancy> {
    /**
     * 
     */
    final Code code;
    public ComparatorRedundancySubstitution(final Code code) {
        this.code = code;
    }
    
    @Override
    public int compare(Redundancy redundancy1, Redundancy redundancy2) {
        int size1 = redundancy1.getSubstitutionList().size();
        int size2 = redundancy2.getSubstitutionList().size();
        
        return (redundancy2.getDuplicatedTokenNumber() / (size2 + 1)) - (redundancy1.getDuplicatedTokenNumber() / (size1 + 1));
    }

}