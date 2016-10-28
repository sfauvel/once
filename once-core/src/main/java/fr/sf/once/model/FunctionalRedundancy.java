package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        List<Set<String>> tmpSubstitutionList = new ArrayList<Set<String>>();
        Set<String> substitutionList = new HashSet<String>();
        for (int index = 0; index < getDuplicatedTokenNumber(); index++) {
            Set<String> substitution = getSubstitutions(getStartRedundancyList(), index);
            if (isASubstitutionToAdd(substitutionList, substitution)) {
                substitutionList.add(substitution.toString());
                tmpSubstitutionList.add(substitution);
            }             
        }
        substitutionListOfSubstitution = Collections.unmodifiableList(tmpSubstitutionList);
    }

    private boolean isASubstitutionToAdd(Set<String> substitutionList, Set<String> listeValeur) {
        return listeValeur.size() > 1 && !substitutionList.contains(listeValeur.toString());
    }

    private Set<String> getSubstitutions(List<Integer> firstTokenList, int index) {
        return mapToSet(firstTokenList, position -> code.getToken(position + index).getValeurToken());
    }
    
    private <T, R> Set<R> mapToSet(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    } 
}
