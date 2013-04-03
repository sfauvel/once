package fr.sf.once;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

    private Map<Token, Integer> mapTokenTokenPosition = new TreeMap<Token, Integer>(new Comparator<Token>() {
        @Override
        public int compare(Token token1, Token token2) {
            if (!token1.getType().equals(token2.getType())) {
                return token1.getType().toString().compareTo(token2.getType().toString());
            } else {
                return token1.getValeurToken().compareTo(token2.getValeurToken());
            }
        }
    });

    public int getPosition(Token token) {
        Integer position = mapTokenTokenPosition.get(token);
        if (position == null) {
            position = mapTokenTokenPosition.size();
            mapTokenTokenPosition.put(token, position);
        }
        return position;
    }

}
