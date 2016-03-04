package fr.sf.once.comparator;

import java.util.HashMap;
import java.util.Map;

public class SubstitutionStringList extends AbstractSubstitutionList<String> {

    private Map<String, Integer> positionMap = new HashMap<String, Integer>();

    public SubstitutionStringList() {
        
    }
    
    public SubstitutionStringList(SubstitutionStringList substitutionList) {
        positionMap = new HashMap<String, Integer>(substitutionList.positionMap);
    }

    @Override
    public int getPosition(String token) {
        Integer position = positionMap.get(token);
        if (position == null) {
            position = positionMap.size();
            positionMap.put(token, position);
        }
        return position;
    }

}
