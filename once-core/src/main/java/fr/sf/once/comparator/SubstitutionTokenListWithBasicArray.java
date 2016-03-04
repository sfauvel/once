package fr.sf.once.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import fr.sf.once.model.Token;

public class SubstitutionTokenListWithBasicArray extends AbstractSubstitutionList<Token> {

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
    
    private int MAX_TOKEN_IN_DUPLICATION = 10000;
    private Token[] tokenList = new Token[MAX_TOKEN_IN_DUPLICATION];
    private int elementNumberRef = 0;
    private int elementNumber = 0;

    public SubstitutionTokenListWithBasicArray() {
    }

    public SubstitutionTokenListWithBasicArray(SubstitutionTokenListWithBasicArray substitutionList) {
        elementNumberRef = substitutionList.elementNumber;
        for (int i = 0; i < elementNumberRef; i++) {
            tokenList[i] = substitutionList.tokenList[i];
        }
        elementNumber = elementNumberRef;
    }

    public void reinit() {
        elementNumber = elementNumberRef;
    }

    @Override
    public int getPosition(Token token) {
        for (int i = 0; i < elementNumber; i++) {
            if (comparator.compare(token, tokenList[i]) == 0) {
                return i;
            }
        }
        int position = elementNumber;
        elementNumber++;
        try {
            tokenList[position] = token;
        } catch (ArrayIndexOutOfBoundsException e) {
            tokenList = Arrays.copyOf(tokenList, tokenList.length*2);
            tokenList[position] = token;
        }
        return position;
    }

}
