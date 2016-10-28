package fr.sf.once.model;

import static fr.sf.commons.CollectionsShortcuts.mapToSet;

import java.util.ArrayList;
import java.util.Collections;
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
        List<Set<String>> substitutionList = new ArrayList<Set<String>>();
        for (int index = 0; index < getDuplicatedTokenNumber(); index++) {
            Set<String> substitution = getSubstitutions(getStartRedundancyList(), index);
            if (isASubstitutionToAdd(substitutionList, substitution)) {
                substitutionList.add(substitution);
            }             
        }
        substitutionListOfSubstitution = Collections.unmodifiableList(substitutionList);
    }

    private boolean isASubstitutionToAdd(List<Set<String>> substitutionList, Set<String> listeValeur) {
        return listeValeur.size() > 1 && !substitutionList.contains(listeValeur);
    }

    private Set<String> getSubstitutions(List<Integer> firstTokenList, int index) {
        return mapToSet(firstTokenList, position -> code.getToken(position + index).getValeurToken());
    }
    

}
