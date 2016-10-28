package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Structure to handle source tokens.
 */
public class Code {
    
    private final List<Token> tokenList;
    private final List<MethodLocation> methodList;
    
    public Code() {
        this(Collections.<Token> emptyList());
    }

    public Code(final List<Token> tokenList) {
        this(tokenList, Collections.<MethodLocation>emptyList());
    }
 
    public Code(List<Token> tokenList, List<MethodLocation> methodList) {
        this.tokenList = Collections.unmodifiableList(new ArrayList<Token>(tokenList));
        this.methodList = Collections.unmodifiableList(new ArrayList<MethodLocation>(methodList));
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

    public List<MethodLocation> getMethodList() {
        return methodList;
    }

    public MethodLocation getMethodAtTokenPosition(int tokenPosition) {
        return methodList.parallelStream()
                .filter(m -> m.containsPosition(tokenPosition))
                .findFirst()
                .orElse(null);
    }


}
