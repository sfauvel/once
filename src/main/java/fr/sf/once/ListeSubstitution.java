package fr.sf.once;

import java.util.HashMap;
import java.util.Map;

/**
 * Liste gérant les subsitutions. 
 */
public class ListeSubstitution {

    public static final int NON_TROUVE = -1;
    private Map<String, Integer> mapTokenPosition = new HashMap<String, Integer>();
    
    public int getPosition(String token) {
        Integer position = mapTokenPosition.get(token);
        if (position == null) {
            position = mapTokenPosition.size();
            mapTokenPosition.put(token, position);
        }
        return position;
    }

}
