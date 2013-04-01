package fr.sf.once;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Redondance {

    private int duplicatedTokenNumber;
	private List<Integer> firstTokenList = new ArrayList<Integer>();
	
	public Redondance(int duplicatedTokenNumber) {
	    this.duplicatedTokenNumber = duplicatedTokenNumber;
	}
	
	public int getDuplicatedTokenNumber() {
		return this.duplicatedTokenNumber;
	}

    public List<Integer> getFirstTokenList() {
        return firstTokenList;
    }
    
    public boolean contains(Redondance includedRedundancy) {
        Collections.sort(firstTokenList);
        Collections.sort(includedRedundancy.firstTokenList);
        
        return containsWithSortedRedundancy(includedRedundancy);
    }
    
    public boolean containsWithSortedRedundancy(Redondance includedRedundancy) {
        
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
    
    private static boolean containsInList(List<Redondance> redundancyList, Redondance redundancyToCheck) {
        for (Redondance redundancy : redundancyList) {
            if (redundancy.containsWithSortedRedundancy(redundancyToCheck)) {
                return true;
            }
        }
        return false;
    }


    public static void sort(List<Redondance> redundancyList) {
        Collections.sort(redundancyList, new Comparator<Redondance>() {

            @Override
            public int compare(Redondance redondance1, Redondance redondance2) {
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
    public static void removeDuplicatedList(List<Redondance> redundancyList) {
        sort(redundancyList);

        Map<String, Redondance> searchRedundancy = new HashMap<String, Redondance>();
        for (Iterator<Redondance> iterator = redundancyList.iterator(); iterator.hasNext();) {
            Redondance redondance = iterator.next();
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

    public static String getRedundancyKey(Redondance redondance) {
        List<Integer> lastTokenList = new ArrayList<Integer>(redondance.firstTokenList);
        StringBuffer buffer = new StringBuffer();
        for (Integer value : lastTokenList) {
            buffer.append(value + redondance.getDuplicatedTokenNumber());
            buffer.append(",");
        }
        return buffer.toString();
    }

}
