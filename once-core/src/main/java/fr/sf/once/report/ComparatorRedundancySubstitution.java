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
    public int compare(FunctionalRedundancy redondance1, FunctionalRedundancy redondance2) {
        int size1 = redondance1.getSubstitutionList().size();
        int size2 = redondance2.getSubstitutionList().size();
        
        return (redondance2.getDuplicatedTokenNumber() / (size2 + 1)) - (redondance1.getDuplicatedTokenNumber() / (size1 + 1));
    }

}