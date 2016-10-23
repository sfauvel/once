package fr.sf.once.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.Redundancy;

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

    List<Set<String>> getSubstitutionList(final Code code, Redundancy redondance) {
        List<Set<String>> substitutionListOfSubstitution = new ArrayList<Set<String>>();
        int duplicatedTokenNumber = redondance.getDuplicatedTokenNumber();
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        Set<String> substitutionList = new HashSet<String>();
        for (int i = 0; i < duplicatedTokenNumber; i++) {
            Set<String> listeValeur = new HashSet<String>();
            for (Integer firstPosition : firstTokenList) {
                int position = firstPosition + i;
                listeValeur.add(code.getToken(position).getValeurToken());
            }
            if (listeValeur.size() > 1) {
                String key = listeValeur.toString();
                if (!substitutionList.contains(key)) {
                    substitutionList.add(key);
                    substitutionListOfSubstitution.add(listeValeur);
                }
            }
        }
        return substitutionListOfSubstitution;
    }
  
}