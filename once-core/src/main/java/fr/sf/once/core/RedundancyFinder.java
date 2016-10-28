package fr.sf.once.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.report.ReportingImpl;


/**
 * Find redundancy into the code. 
 */
public class RedundancyFinder {

    public static final Logger LOG = Logger.getLogger(RedundancyFinder.class);
    private final Code code;

    public RedundancyFinder(Code code) {
        this.code = code;
    }

    public List<FunctionalRedundancy> findRedundancies(Configuration configuration) {
        LOG.info("Global token number: " + code.getTokenList().size());
        List<Integer> positionList = getPositionToManage();
        LOG.info("Significant token number: " + positionList.size());
        LOG.info("Sort tokens...");
        CodeComparator comparator = configuration.getComparator(code);
        sortPositionList(positionList, comparator);
        traceSortedToken(positionList);
        LOG.info("Compute redundancies size...");
        int[] redundancySize = comparator.getRedundancySize(positionList);
        traceRedundancySize(positionList, redundancySize);

        LOG.info("Build redundancies...");
        List<Redundancy> listeRedondance = computeRedundancy(positionList, redundancySize, configuration.getMinimalTokenNumber());
        LOG.info("Remove overlap between redundancies...");
        listeRedondance = removeOverlap(listeRedondance);
        LOG.info("Remove duplicate redundancies...");
        listeRedondance = removeRedundancyIncludedInAnotherOne(listeRedondance);
        LOG.info("Finished");
        
        // TODO Transform redundancy into FunctionalRedundancy. It could be suppress when the 2 classes are merged.
        return listeRedondance.stream().map(r -> new FunctionalRedundancy(code,  r)).collect(Collectors.toList());

    }

    private List<Redundancy> removeOverlap(List<Redundancy> redundancyList) {
        LOG.info("Redundancy number before removing overlap: " + redundancyList.size());
        for (Iterator<Redundancy> iterator = redundancyList.iterator(); iterator.hasNext();) {
            Redundancy redondance = iterator.next();
            redondance.removeOverlapRedundancy();
            if (redondance.getRedundancyNumber() <= 1) {
                iterator.remove();
            }
        }
        LOG.info("Redundancy number after removing overlap: " + redundancyList.size());

        return redundancyList;
    }

    /**
     * Remove from list token that not significant.
     * 
     * It's all separator token like ';', '{', '}', '<', ...
     * That's reduce the number of token to compute.
     * 
     * @return
     */
    private List<Integer> getPositionToManage() {
        List<Token> tokenList = getTokenList();
        List<Integer> positionList = new ArrayList<Integer>();
        for (int position = 0; position < tokenList.size(); position++) {
            if (isTokenIsSignificatifForRedundancy(tokenList.get(position))) {
                positionList.add(position);
            }           
        }
        return positionList;
    }

    private boolean isTokenIsSignificatifForRedundancy(Token token) {
        return !Type.NON_SIGNIFICATIF.equals(token.getType());
    }

    private List<Token> getTokenList() {
        return code.getTokenList();
    }

    private Token getToken(Integer position) {
        return code.getToken(position);
    }

    private void traceRedundancySize(List<Integer> positionList, int[] redundanciesSize) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nXXXX\n  traceRedundancySize");
            int position = 0;
            for (Integer tokenPosition : positionList) {
                traceToken(tokenPosition);
                if (position < redundanciesSize.length) {
                    LOG.debug("Redundancy size:" + redundanciesSize[position]);
                }
                position++;
            }
        }
    }

    public List<Redundancy> removeRedundancyIncludedInAnotherOne(List<Redundancy> redundancyList) {
        LOG.info("Redundancy number before removing duplicate ones: " + redundancyList.size());
        Redundancy.removeDuplicatedList(redundancyList);
        LOG.info("Redundancy number after removing duplicate ones: " + redundancyList.size());
        return redundancyList;
    }

    public void sortPositionList(List<Integer> positionList, CodeComparator comparator) {
        traceTokens(positionList, "\nXXXX\n  sortPositionList before sort");
        Collections.sort(positionList, comparator);
        traceTokens(positionList, "\nXXXX\n  sortPositionList after sort");
    }

    private void traceTokens(List<Integer> positionList, String message) {
        if (ReportingImpl.TRACE_TOKEN.isDebugEnabled()) {
            ReportingImpl report = new ReportingImpl(null);
            ReportingImpl.TRACE_TOKEN.debug(message);
            for (Integer position : positionList) {
                report.display(getToken(position));
            }
        }
    }

    private void traceSortedToken(List<Integer> positionList) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sorted list");
            for (Integer tokenPosition : positionList) {
                traceToken(tokenPosition);
            }
        }
    }

    private void traceToken(int position) {
        if (LOG.isDebugEnabled()) {
            Token token = getToken(position);
            LOG.debug(position + "\t" + token.getlocalisation().getFileName() + " line:" + token.getlocalisation().getLine() + "\t N° token:" + position);
            StringBuffer buffer = new StringBuffer();

            int size = getTokenList().size();
            for (int i = position; i < size; i++) {
                buffer.append(getToken(i).getTokenValue());
                buffer.append(" ");
            }
            LOG.debug(buffer.toString());
        }
    }

    public List<Redundancy> computeRedundancy(List<Integer> positionList, int[] redundancySize, int minimalSize) {
        List<Redundancy> redondancyList = new ArrayList<Redundancy>();
        addRedundancyInternal(positionList, redondancyList, redundancySize, 0, 0, 0);
        for (int i = 1; i < redundancySize.length; i++) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("index : " + i);
            }
            if (redundancySize[i] > redundancySize[i - 1]) {
                addRedundancyInternal(positionList, redondancyList, redundancySize, i, i, Math.max(minimalSize, redundancySize[i - 1]));
            }
        }
        return redondancyList;
    }

    private void addRedundancyInternal(List<Integer> positionList, List<Redundancy> redundancyList, int[] redundancySizeList, int startingIndex,
            int currentIndex, int minimalSize) {
        if (currentIndex >= redundancySizeList.length) {
            return;
        }
        int initialSize = redundancySizeList[currentIndex];
        if (initialSize <= minimalSize) {
            return;
        }
        while (currentIndex < redundancySizeList.length && initialSize <= redundancySizeList[currentIndex]) {
            currentIndex++;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("add starting=" + startingIndex + " number=" + (currentIndex - startingIndex + 1) + " size=" + initialSize);
        }

        redundancyList.add(createRedundancy(initialSize, positionList.subList(startingIndex, currentIndex + 1)));

        if (LOG.isDebugEnabled()) {
            for (Integer i : positionList.subList(startingIndex, currentIndex + 1)) {
                LOG.debug("  position:" + i);
            }
        }

        addRedundancyInternal(positionList, redundancyList, redundancySizeList, startingIndex, currentIndex, minimalSize);
    }

    private Redundancy createRedundancy(int redondanceSize, List<Integer> subList) {
        return new Redundancy(redondanceSize, subList);
    }

}
