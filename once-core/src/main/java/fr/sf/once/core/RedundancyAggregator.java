package fr.sf.once.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public class RedundancyAggregator {

    private final Code code;

    public RedundancyAggregator(Code code) {
        this.code = code;
    }

    public Code aggregate(Redundancy... redundancyList) {
        List<Token> tokenList = new ArrayList<Token>();
        tokenList.addAll(buildOneRedundancyKeepOrder(code, 0, redundancyList));
        tokenList.addAll(buildOneRedundancyKeepOrder(code, 1, redundancyList));
        return new Code(tokenList);
    }

    private List<Token> buildOneRedundancyKeepOrder(Code code, int redundancyIndex, Redundancy... redundancyList) {
        List<Token> tokenList = new ArrayList<Token>();
        List<Redundancy> orderRedundancy = Arrays.asList(redundancyList);
        orderRedundancy.sort(new Comparator<Redundancy>() {
            @Override
            public int compare(Redundancy r1, Redundancy r2) {
                return r1.getStartRedundancyList().get(redundancyIndex) - r2.getStartRedundancyList().get(redundancyIndex);
            }
        });
        
        for (Redundancy redundancy : orderRedundancy) {
            Integer start = redundancy.getStartRedundancyList().get(redundancyIndex);
            for (int position = start; position < start+redundancy.getDuplicatedTokenNumber(); position++) {
                tokenList.add(code.getToken(position));
            }
        }
        return tokenList;
    }

    
    private List<Token> buildOneRedundancyNonOrder(Code code, int redundancyIndex, Redundancy... redundancyList) {
        List<Token> tokenList = new ArrayList<Token>();
        
        for (Redundancy redundancy : redundancyList) {
            Integer start = redundancy.getStartRedundancyList().get(redundancyIndex);
            for (int position = start; position < start+redundancy.getDuplicatedTokenNumber(); position++) {
                tokenList.add(code.getToken(position));
            }
        }
        return tokenList;
    }

}
