package fr.sf.once.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.IntRange;

import fr.sf.once.model.Code;
import fr.sf.once.model.MethodLocation;
import fr.sf.once.model.Redundancy;

public class ReportingCrossMethod extends ReportingImpl {
    
    @Override
    public void displayRedundancy(final Code code, final int minimalSize, List<Redundancy> redundancyList) {
        Map<Set<String>, List<Redundancy>> mapMethodRedundancy = getRedundanciesByMethodGroup(code, redundancyList);
        List<RedundancyMultiPart> multiPartRedundancyList = new ArrayList<>();
        for (Entry<Set<String>, List<Redundancy>> methodRedundancies : mapMethodRedundancy.entrySet()) {
            List<Redundancy> nonOverlapRedundancyList = removeOverlap(methodRedundancies.getValue());
//            if (nonOverlapRedundancyList.size() > 1) {
                multiPartRedundancyList.add(new RedundancyMultiPart(nonOverlapRedundancyList));
//            }
        }
        
        multiPartRedundancyList.stream()
            .sorted( new ComparatorRedundancyMultiPartByTokenNumber())
            .forEach(redundancy ->  displayCrossMethodRedundancies(code, redundancy));
        
        super.displayRedundancy(code, minimalSize, redundancyList);
    }

    private List<Redundancy> removeOverlap(List<Redundancy> redundancyList) {
        List<Redundancy> nonOverlapRedundancy = new ArrayList<>();
        for (Redundancy currentRedundancy : redundancyList) {
            if (nonOverlapRedundancy.stream().noneMatch(r -> currentRedundancy.isOverlap(r))) {
                nonOverlapRedundancy.add(currentRedundancy);
            }
        }
        
        return nonOverlapRedundancy;
    }

    private Map<Set<String>, List<Redundancy>> getRedundanciesByMethodGroup(final Code code, List<Redundancy> redundancyList) {
        Map<Set<String>, List<Redundancy>> mapMethodRedundancy = new HashMap<>();
        for (Redundancy redundancy : redundancyList) {
            Set<String> methodList = getMethods(code, redundancy);
            if (!mapMethodRedundancy.containsKey(methodList)) {
                mapMethodRedundancy.put(methodList, new ArrayList<>());
            }
            mapMethodRedundancy.get(methodList).add(redundancy);
        }
        return mapMethodRedundancy;
    }

    static class ComparatorRedundancyMultiPartByTokenNumber implements Comparator<RedundancyMultiPart> {
        @Override
        public int compare(RedundancyMultiPart redundancy1, RedundancyMultiPart redundancy2) {
            return redundancy2.getDuplicatedTokenNumber() - redundancy1.getDuplicatedTokenNumber();
        }
    }
    
    static class RedundancyMultiPart {
        private final List<Redundancy> redundancyList;
        

        public RedundancyMultiPart(List<Redundancy> redundancyList) {
            this.redundancyList = Collections.unmodifiableList(redundancyList);
        }

        public List<Redundancy> getRedundancyList() {
            return redundancyList;
        }
        
        public Set<MethodLocation> getMethodList() {
            return getMethodIntoRedundancies();
        }
        
        public int getDuplicatedTokenNumber() {
            return getRedundancyList().stream().mapToInt(Redundancy::getDuplicatedTokenNumber).sum();
        }
        

        private Set<MethodLocation> getMethodIntoRedundancies() {
            Set<MethodLocation> methodList = new HashSet<>();
            Redundancy redundancy = getRedundancyList().get(0);
            for (int firstTokenPosition : redundancy.getStartRedundancyList()) {
                MethodLocation method = redundancy.getMethodAtTokenPosition(firstTokenPosition);
                methodList.add(method);
            }
            return methodList;
        }

        public Object getPartNumber() {
            return getRedundancyList().size();
        }

    }
    
    private void displayCrossMethodRedundancies(Code code, RedundancyMultiPart redundancyMultiPart) {
        Set<MethodLocation> methodList = redundancyMultiPart.getMethodList();

        int totalDuplicatedNumber = redundancyMultiPart.getDuplicatedTokenNumber();
        LOG_RESULT.info(String.format("Tokens number:%d Parts number:%d", totalDuplicatedNumber, redundancyMultiPart.getPartNumber()));

        for (MethodLocation method : methodList) {
            long tokenNumber = method.getTokenRange().getMaximumLong() - method.getTokenRange().getMinimumLong();
            long tokenPercentage = computePercentage(totalDuplicatedNumber, tokenNumber);
            String methodDescription = String.format("  %d%% (%d of %d tokens) %s", tokenPercentage, totalDuplicatedNumber, tokenNumber, method.getMethodName());
            LOG_RESULT.info(methodDescription + " " + tokenRangeDescription(code, redundancyMultiPart.getRedundancyList(), method));
        }
    }

    private String tokenRangeDescription(Code code, List<Redundancy> redundancyList, MethodLocation method) {
        List<IntRange> partList = new ArrayList<>();
        for (Redundancy redundancy : redundancyList) {
            partList.addAll(redundancy.getStartRedundancyList().parallelStream()
                .filter(start -> method.equals(code.getMethodAtTokenPosition(start)))
                .map(start -> new IntRange((int)start, (int)(start+redundancy.getDuplicatedTokenNumber())))
                .collect(Collectors.toSet()));
        }
        
        Collections.sort(partList, new Comparator<IntRange>() {
        @Override
            public int compare(IntRange o1, IntRange o2) {
                return (int)(o1.getMaximumLong() - o2.getMinimumLong());
            }   
        });
        
        StringBuffer buffer = new StringBuffer();
        partList.forEach(range -> { 
                long firstMethodToken = method.getTokenRange().getMinimumLong();
                buffer.append(", " + "[" + (code.getToken(range.getMinimumInteger()).getStartingLine()) + "-" + (code.getToken(range.getMaximumInteger()).getStartingLine()) + "]");
//                buffer.append(", " + "[" + (range.getMinimumLong() - firstMethodToken) + "-" + (range.getMaximumLong() - firstMethodToken) + "]");
            }
        );
        
        return buffer.toString().substring(2);
    }

    private void appendCodeDetails(Code code, StringBuffer buffer, Redundancy redundancy, Integer start) {
        if (LOG_RESULT.isDebugEnabled()) {
            for (int i = start; i < start + redundancy.getDuplicatedTokenNumber(); i++) {
                buffer.append(" " + code.getToken(i).getTokenValue());
            }
            buffer.append("\n    ");
        }
    }

    private Set<MethodLocation> getMethodIntoRedundancies(Code code, List<Redundancy> redundancyList) {
        Set<MethodLocation> methodList = new HashSet<>();
        // for (Redundancy redundancy : redundancyList) {
        Redundancy redundancy = redundancyList.get(0);
        for (int firstTokenPosition : redundancy.getStartRedundancyList()) {
            MethodLocation method = code.getMethodAtTokenPosition(firstTokenPosition);
            methodList.add(method);
        }
        // }
        return methodList;
    }

    private Set<String> getMethods(Code code, Redundancy redundancy) {
        return redundancy.getStartRedundancyList().stream()
                .map(position -> code.getMethodAtTokenPosition(position).getMethodName())
                .collect(Collectors.toSet());
    }
}
