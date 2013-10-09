package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Redundancy {

    private int duplicatedTokenNumber;
	private List<Integer> firstTokenList = new ArrayList<Integer>();
	
	public Redundancy(int duplicatedTokenNumber) {
	    this.duplicatedTokenNumber = duplicatedTokenNumber;
	}
	
	public int getDuplicatedTokenNumber() {
		return this.duplicatedTokenNumber;
	}

    public List<Integer> getStartRedundancyList() {
        return firstTokenList;
    }
    
    public boolean contains(Redundancy includedRedundancy) {
        Collections.sort(firstTokenList);
        Collections.sort(includedRedundancy.firstTokenList);
        
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
     * @param redundancyList
     */
    public static void removeDuplicatedList(List<Redundancy> redundancyList) {
        sort(redundancyList);

        Map<String, Redundancy> searchRedundancy = new HashMap<String, Redundancy>();
        for (Iterator<Redundancy> iterator = redundancyList.iterator(); iterator.hasNext();) {
            Redundancy redondance = iterator.next();
            Collections.sort(redondance.firstTokenList);
            String key = getRedundancyKey(redondance);
            if (!searchRedundancy.containsKey(key)) {
                searchRedundancy.put(key,  redondance);   
            }
            
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
        // TODO The first token list should be always sorted.
        Collections.sort(firstTokenList, Collections.reverseOrder());
        int lastFirstValue = Integer.MAX_VALUE;
        for (Iterator<Integer> tokenIterator = firstTokenList.iterator(); tokenIterator.hasNext();) {
            Integer tokenPosition = tokenIterator.next();            
            if (tokenPosition + duplicatedTokenNumber > lastFirstValue) {
                tokenIterator.remove();
            } else {
                lastFirstValue = tokenPosition;
            }
        }
        Collections.sort(firstTokenList);
    }

    public int getRedundancyNumber() {
        return firstTokenList.size();
    }

}
