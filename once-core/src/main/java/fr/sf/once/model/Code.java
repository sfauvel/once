package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Structure to handle source tokens.
 */
public class Code implements ICode {
    
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


    @Override
    public List<Token> getTokenList() {
        return tokenList;
    }
    
    @Override
    public Token getToken(int position) {
        return tokenList.get(position);
    }

    @Override
    public int getSize() {
        return tokenList.size();
    }

    @Override
    public List<MethodLocation> getMethodList() {
        return methodList;
    }

    @Override
    public MethodLocation getMethodAtTokenPosition(int tokenPosition) {
        return MethodLocation.findMethod(methodList, tokenPosition);
    }

}
