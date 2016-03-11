package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.math.IntRange;


/**
 * Structure to handle source tokens.
 */
public class Code {
    
    private final List<Token> tokenList;
    private final List<MethodLocalisation> methodList;
    
    public Code(final List<Token> tokenList) {
        this(tokenList, Collections.<MethodLocalisation>emptyList());
    }
 
    public Code(List<Token> tokenList, List<MethodLocalisation> methodList) {
        this.tokenList = Collections.unmodifiableList(new ArrayList<Token>(tokenList));
        this.methodList = Collections.unmodifiableList(new ArrayList<MethodLocalisation>(methodList));
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

    public List<MethodLocalisation> getMethodList() {
        return methodList;
    }

    public MethodLocalisation getMethodAtTokenPosition(int tokenPosition) {
        return methodList.parallelStream()
                .filter(m -> m.containsPosition(tokenPosition))
                .findFirst()
                .orElse(null);
    }

    public Code removeFromTo(int first, int last) {
        return removeCode(new IntRange(first, last));
    }

    public Code removeCode(IntRange... intRangeList) {
        List<Token> newTokenList = tokenList;
        for (int i = intRangeList.length-1; i >= 0; i--) {
            newTokenList = removeOneRangeFromCode(newTokenList, intRangeList[i]);
        }
        return new Code(newTokenList);
    }

    private List<Token> removeOneRangeFromCode(List<Token> tokenList, IntRange intRange) {
        List<Token> newTokenList = new ArrayList<Token>();
        newTokenList.addAll(tokenList.subList(0,  intRange.getMinimumInteger()));
        newTokenList.addAll(tokenList.subList(intRange.getMaximumInteger()+1, tokenList.size()));
        return newTokenList;
    }

    public Code getMethodCode(String... methodNameList) {
        ArrayList<Token> methodTokens = new ArrayList<Token>();
        for (String methodName : methodNameList) {
            MethodLocalisation method = methodList.stream().filter(m -> m.getMethodName().equals(methodName)).findFirst().get();
            methodTokens.addAll(tokenList.subList(method.getTokenRange().getMinimumInteger(), method.getTokenRange().getMaximumInteger()));
        }
        return new Code(methodTokens);
    }


}
