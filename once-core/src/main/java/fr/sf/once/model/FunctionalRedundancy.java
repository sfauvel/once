package fr.sf.once.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionalRedundancy extends Redundancy {

    private final Code code;

    public FunctionalRedundancy(final Code code, final Redundancy redundancy) {
        super(redundancy.getDuplicatedTokenNumber(), redundancy.getStartRedundancyList());
        this.code = code;
    }
    
    private List<Set<String>> substitutionListOfSubstitution = null;

    public List<Set<String>> getSubstitutionList() {
        if (substitutionListOfSubstitution == null) {
            initSubstitutionList();
        }
        return substitutionListOfSubstitution;
    }

    private void initSubstitutionList() {
        substitutionListOfSubstitution = new ArrayList<Set<String>>();
        int duplicatedTokenNumber = getDuplicatedTokenNumber();
        List<Integer> firstTokenList = getStartRedundancyList();
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
    }   
}
