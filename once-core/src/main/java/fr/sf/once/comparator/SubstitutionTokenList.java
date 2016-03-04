package fr.sf.once.comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import fr.sf.once.model.Token;

public class SubstitutionTokenList extends AbstractSubstitutionList<Token> {

    public SubstitutionTokenList() {
    }
    
    public SubstitutionTokenList(SubstitutionTokenList substitutionList) {
        positionMap = new TreeMap<Token, Integer>(new TokenComparator());
        positionMap.putAll(substitutionList.positionMap);
    }

    private Map<Token, Integer> positionMap = new TreeMap<Token, Integer>(new TokenComparator());

    private class TokenComparator implements Comparator<Token> {
        @Override
        public int compare(Token token1, Token token2) {
            if (!token1.getType().equals(token2.getType())) {
                return token1.getType().toString().compareTo(token2.getType().toString());
            } else {
                return token1.getValeurToken().compareTo(token2.getValeurToken());
            }
        }
    };

    @Override
    public int getPosition(Token token) {
        Integer position = positionMap.get(token);
        if (position == null) {
            position = positionMap.size();
            positionMap.put(token, position);
        }
        return position;
    }

}
