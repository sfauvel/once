package fr.sf.once.comparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.model.Code;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;


public abstract class CodeComparator implements Comparator<Integer> {
    public static final Logger LOG = Logger.getLogger(CodeComparator.class);

    private int depth;
    private Code code;
    private StringBuffer traceDebug = new StringBuffer();

    public CodeComparator(List<Token> listeToken) {
        this.code = new Code(listeToken);
    }

    public CodeComparator(Code code) {
        this.code = code;
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Integer position1, Integer position2) {
        reinit();
        int result = deepCompare(position1, position2);

        return result;
    }

    protected void reinit() {
        traceDebug = new StringBuffer();
        depth = 0;
    }

    /**
     * Sort a sub-list of positions. 
     * @param positionList
     */
    public void sortList(List<Integer> positionList) {
        Collections.sort(positionList, this);
    }

    /**
     * Compare only value between two tokens.
     * 
     * @param token1
     * @param token2
     * @return
     */
    protected abstract int compareTokenValue(Token token1, Token token2);

    /**
     * Make the comparison travellling over tokens beginning from given positions. 
     * 
     * @param position1
     * @param position2
     * @return
     */
    protected int deepCompare(Integer position1, Integer position2) {
        if (position1 >= code.getSize() || position2 >= code.getSize()) {
            int result = position2 - position1;
            if (LOG.isDebugEnabled()) {
                LOG.debug("compare(" + position1 + ", " + position2 + ") over limit = " + result + "  size:" + depth);
            }
            return result;
        }
        Token token1 = code.getToken(position1);
        Token token2 = code.getToken(position2);

        if (token1.getType().is(Type.BREAK) || token2.getType().is(Type.BREAK)) {
            return breakReturn(token1, token2);
        }
        int result = compareTokenValue(token1, token2);
        if (result == 0) {
            depth++;
            if (LOG.isDebugEnabled()) {
                traceDebug.append(depth + ":" + token1.getTokenValue() + "=" + token2.getTokenValue() + " ");
            }
            result = deepCompare(position1 + 1, position2 + 1);
        } else {
            if (LOG.isDebugEnabled()) {
                traceDebug.append(depth + ":" + token1.getTokenValue() + "<>" + token2.getTokenValue() + " ");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("compare(" + position1 + ":" + code.getToken(position1).getTokenValue() + ", " + position2 + ":" + code.getToken(position2).getTokenValue() + ") = " + result + "  size:" + depth);
        }
        return result;
    }

    /**
     * Manage the value when a one of the token is a break.
     * 
     * @param token1
     * @param token2
     * @return
     */
    public int breakReturn(Token token1, Token token2) {
        if (LOG.isDebugEnabled()) {
            traceDebug.append(depth + ":" + token1.getType() + "<>" + token2.getType() + " ");
        }
        if (token1.getType().is(Type.BREAK)) {
            if (token2.getType().is(Type.BREAK)) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }

    public int[] getRedundancySize(List<Integer> positionToSearch) {
        int[] redundancySize = new int[positionToSearch.size() - 1];
        for (int i = 0; i < redundancySize.length; i++) {
            redundancySize[i] = getRedundancySize(positionToSearch.get(i), positionToSearch.get(i + 1));
        }
        return redundancySize;
    }

    public int getRedundancySize(int position1, int position2) {
        compare(position1, position2);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Redundancy number = " + depth + " between position " + position1 + " and " + position2);
            LOG.debug(traceDebug.toString());
        }
        return depth;
    }

}
