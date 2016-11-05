package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Redundancy {

    private final int duplicatedTokenNumber;
    private final SortedSet<Integer> firstTokenList;

    public Redundancy(final int duplicatedTokenNumber, final Collection<Integer> firstTokenList) {
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

    public static String getRedundancyKey(Redundancy redondance) {
        return redondance.getEndRedundancyList().toString();
    }

    /**
     * Remove duplication starting before the end of the last one.
     */
    public Redundancy removeOverlapRedundancy() {
        // TODO The first token list should be always sorted.
        ArrayList<Integer> tokenList = new ArrayList<Integer>(firstTokenList);
        Collections.sort(tokenList, Collections.reverseOrder());
        int lastFirstValue = Integer.MAX_VALUE;
        for (Iterator<Integer> tokenIterator = tokenList.iterator(); tokenIterator.hasNext();) {
            Integer tokenPosition = tokenIterator.next();
            if (tokenPosition + duplicatedTokenNumber > lastFirstValue) {
                tokenIterator.remove();
            } else {
                lastFirstValue = tokenPosition;
            }
        }
        return new Redundancy(duplicatedTokenNumber, tokenList);
    }

    public int getRedundancyNumber() {
        return firstTokenList.size();
    }

    public static int compareByDuplicatedTokenNumber(Redundancy redundancyA, Redundancy redundancyB) {
        return redundancyB.getDuplicatedTokenNumber() - redundancyA.getDuplicatedTokenNumber();
    }

}
