package fr.sf.once.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.sf.once.model.Token;

public class SubstitutionTokenListWithArray extends AbstractSubstitutionList<Token> {

    private static TokenComparator comparator = new TokenComparator();
    private static class TokenComparator implements Comparator<Token> {
        @Override
        public int compare(Token token1, Token token2) {
            if (!token1.getType().equals(token2.getType())) {
                return token1.getType().toString().compareTo(token2.getType().toString());
            } else {
                return token1.getValeurToken().compareTo(token2.getValeurToken());
            }
        }
    };

    List<Token> tokenList = new ArrayList<Token>();

    public SubstitutionTokenListWithArray() {
    }
    
    public SubstitutionTokenListWithArray(SubstitutionTokenListWithArray substitutionList) {
        tokenList = new ArrayList<>(substitutionList.tokenList);
    }

    @Override
    public int getPosition(Token token) {
        int size = tokenList.size();
        for (int i = 0; i < size; i++) {
            if (comparator.compare(token, tokenList.get(i)) == 0) {
                return i;
            }
        }
        tokenList.add(token);
        return size;
    }

}
