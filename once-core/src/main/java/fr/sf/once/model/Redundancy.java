package fr.sf.once.model;

import static fr.sf.commons.CollectionsShortcuts.mapToSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Redundancy {

    private final int duplicatedTokenNumber;
    private final SortedSet<Integer> firstTokenList;
    private final Code code;
    private List<Set<String>> substitutionListOfSubstitution = null;
    
    public Redundancy(final Code code, final int duplicatedTokenNumber, final Collection<Integer> firstTokenList) {
        this.code = code;
        this.duplicatedTokenNumber = duplicatedTokenNumber;
        this.firstTokenList = Collections.unmodifiableSortedSet(new TreeSet<Integer>(firstTokenList));
    }

    public int getDuplicatedTokenNumber() {
        return this.duplicatedTokenNumber;
    }

    public Collection<Integer> getStartRedundancyList() {
        return firstTokenList;
    }

    public Collection<Integer> getEndRedundancyList() {
        return firstTokenList.stream()
                .map(position -> position + this.duplicatedTokenNumber)
                .collect(Collectors.toList());
    }

    public boolean contains(Redundancy includedRedundancy) {
        return containsWithSortedRedundancy(includedRedundancy);
    }

    public boolean containsWithSortedRedundancy(Redundancy includedRedundancy) {

        if (includedRedundancy.duplicatedTokenNumber > duplicatedTokenNumber) {
            return false;
        }
        if (firstTokenList.size() < includedRedundancy.firstTokenList.size()) {
            return false;
        }

        Iterator<Integer> iteratorReference = firstTokenList.iterator();
        Iterator<Integer> iteratorIncluded = includedRedundancy.firstTokenList.iterator();

        while (iteratorReference.hasNext() && iteratorIncluded.hasNext()) {
            int valueReference = iteratorReference.next();
            int valueIncluded = iteratorIncluded.next();
            if (valueReference + duplicatedTokenNumber != valueIncluded + includedRedundancy.duplicatedTokenNumber) {
                return false;
            }
        }
        return true;
    }

    public int getRedundancyNumber() {
        return firstTokenList.size();
    }

    public static int compareByDuplicatedTokenNumber(Redundancy redundancyA, Redundancy redundancyB) {
        return redundancyB.getDuplicatedTokenNumber() - redundancyA.getDuplicatedTokenNumber();
    }

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

    private boolean isASubstitutionToAdd(List<Set<String>> substitutionList, Set<String> valueList) {
        return valueList.size() > 1 && !substitutionList.contains(valueList);
    }

    private Set<String> getSubstitutions(Collection<Integer> firstTokenList, int index) {
        return mapToSet(firstTokenList, position -> code.getToken(position + index).getTokenValue());
    }
    
}
