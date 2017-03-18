package fr.sf.once.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.IntRange;

import fr.sf.commons.CollectionsShortcuts;

/**
 * Create a code that is compose of part of another code.
 */
public class PartialCode implements Code {
    private CodeAsATokenList code;
    private List<Integer> mapToken = new ArrayList<>();
    
    PartialCode(CodeAsATokenList code, List<IntRange> rangeList) {
        this.code = code;
        for (IntRange range : rangeList) {
            for (int originalPosition = range.getMinimumInteger(); originalPosition <= range.getMaximumInteger(); originalPosition++) {
                mapToken.add(originalPosition);
            }
        }
    }

    @Override
    public List<Token> getTokenList() {
        return CollectionsShortcuts.mapToList(mapToken, 
                originalPosition -> code.getToken(originalPosition));
    }

    @Override
    public Token getToken(int position) {
        return code.getToken(mapToken.get(position));
    }

    @Override
    public int getSize() {
        return mapToken.size();
    }

    @Override
    public List<MethodLocation> getMethodList() {
        return code.getMethodList();
    }

    @Override
    public MethodLocation getMethodAtTokenPosition(int tokenPosition) {
        return code.getMethodAtTokenPosition(tokenPosition);
    }

    public int getOriginalPosition(int position) {
        return mapToken.get(position);
    }
}