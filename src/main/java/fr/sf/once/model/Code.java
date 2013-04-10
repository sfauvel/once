package fr.sf.once.model;

import java.util.Collections;
import java.util.List;


/**
 * Classe de manipulation du code source.
 */
public class Code {
    
    private final List<Token> tokenList;
    
    public Code(final List<Token> tokenList) {
        this.tokenList = Collections.unmodifiableList(tokenList);
    }
 
    public List<Token> getTokenList() {
        return tokenList;
    }
    
    public Token getToken(int position) {
        return tokenList.get(position);
    }

    public int getSize() {
        return tokenList.size();
    }


}
