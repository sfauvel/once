package fr.sf.once.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Classe de manipulation du code source.
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
        for (MethodLocalisation methodLocalisation : methodList) {
            if (methodLocalisation.containsPosition(tokenPosition)) {
                return methodLocalisation;
            }
        }
        return null;
    }


}
