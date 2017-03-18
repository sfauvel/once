package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Structure to handle source tokens.
 */
public class CodeAsATokenList implements Code {
    
    private final List<Token> tokenList;
    private final List<MethodLocation> methodList;
    
    public CodeAsATokenList() {
        this(Collections.<Token> emptyList());
    }

    public CodeAsATokenList(final List<Token> tokenList) {
        this(tokenList, Collections.<MethodLocation>emptyList());
    }
 
    public CodeAsATokenList(List<Token> tokenList, List<MethodLocation> methodList) {
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
