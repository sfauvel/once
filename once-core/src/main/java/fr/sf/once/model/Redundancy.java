package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class Redundancy {

    private class CodeRange {
        private int start;
        private int end;
    }

    private class CodePart {
        List<CodeRange> codeRangeList = new ArrayList<CodeRange>();
    }

    private int duplicatedTokenNumber;
    private TreeSet<Integer> firstTokenList = new TreeSet<Integer>();

    public Redundancy(int duplicatedTokenNumber) {
        this.duplicatedTokenNumber = duplicatedTokenNumber;
    }

    public int getDuplicatedTokenNumber() {
        return this.duplicatedTokenNumber;
    }

    public List<Integer> getStartRedundancyList() {
        return Collections.unmodifiableList(new ArrayList<Integer>(firstTokenList));
    }

    public boolean contains(Redundancy includedRedundancy) {
        return containsWithSortedRedundancy(includedRedundancy);
    }

    public boolean containsWithSortedRedundancy(Redundancy includedRedundancy) {

        if (includedRedundancy.duplicatedTokenNumber <= duplicatedTokenNumber) {
            if (firstTokenList.size() < includedRedundancy.firstTokenList.size()) {
                return false;
            }

            Iterator<Integer> iteratorReference = firstTokenList.iterator();
            Iterator<Integer> iteratorIncluded = includedRedundancy.firstTokenList.iterator();

            for (; iteratorReference.hasNext() && iteratorIncluded.hasNext();) {
                int valueReference = iteratorReference.next();
                int valueIncluded = iteratorIncluded.next();
                if (valueReference + duplicatedTokenNumber != valueIncluded + includedRedundancy.duplicatedTokenNumber) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean containsInList(List<Redundancy> redundancyList, Redundancy redundancyToCheck) {
        for (Redundancy redundancy : redundancyList) {
            if (redundancy.containsWithSortedRedundancy(redundancyToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static void sort(List<Redundancy> redundancyList) {
        Collections.sort(redundancyList, new Comparator<Redundancy>() {

            @Override
            public int compare(Redundancy redondance1, Redundancy redondance2) {
                return redondance2.getDuplicatedTokenNumber() - redondance1.getDuplicatedTokenNumber();
            }

        });
    }

    /**
     * Supprime les redondances incluses les unes dans les autres.
     * A noter, que cette méthode ne détecte pas si une redondance est identique
     * mais avec moins de occurrence. Ce cas ne doit pas exister fonctionnellement.
     * 
     * @param redundancyList
     */
    public static void removeDuplicatedList(List<Redundancy> redundancyList) {
        sort(redundancyList);

        Map<String, Redundancy> searchRedundancy = new HashMap<String, Redundancy>();

        for (Redundancy redundancy : redundancyList) {
            searchRedundancy.putIfAbsent(getRedundancyKey(redundancy), redundancy);
        }

        redundancyList.clear();
        redundancyList.addAll(searchRedundancy.values());
        sort(redundancyList);
    }

    public static String getRedundancyKey(Redundancy redondance) {
        List<Integer> lastTokenList = new ArrayList<Integer>(redondance.firstTokenList);
        StringBuffer buffer = new StringBuffer();
        for (Integer value : lastTokenList) {
            int tokenEndPosition = value + redondance.getDuplicatedTokenNumber();
            buffer.append(tokenEndPosition);
            buffer.append(",");
        }
        return buffer.toString();
    }

    /**
     * Remove duplication starting before the end of the last one.
     */
    public void removeOverlapRedundancy() {
        int lastFirstValue = Integer.MAX_VALUE;
        // Travel in reverse order
        for (Iterator<Integer> tokenIterator = firstTokenList.descendingIterator(); tokenIterator.hasNext();) {
            Integer tokenPosition = tokenIterator.next();
            if (tokenPosition + duplicatedTokenNumber > lastFirstValue) {
                tokenIterator.remove();
            } else {
                lastFirstValue = tokenPosition;
            }
        }
    }

    public int getRedundancyNumber() {
        return firstTokenList.size();
    }

    public Redundancy withStartingCodeAt(int... tokenPositionList) {
        Arrays.stream(tokenPositionList).forEach(t -> firstTokenList.add(t));
        return this;
    }

    public Redundancy withStartingCodeAt(List<Integer> tokenPositionList) {
        tokenPositionList.forEach(t -> firstTokenList.add(t));
        return this;
    }

}
