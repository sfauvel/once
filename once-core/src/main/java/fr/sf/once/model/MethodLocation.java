package fr.sf.once.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;


public class MethodLocation {

    private final String methodName;
    private final Location start;
    private final Location end;
    private final IntRange tokenRange;
    private final Set<Redundancy> redondanceList = new HashSet<Redundancy>();
    
    public MethodLocation(String methodName, Location startingLocation, Location endingLocation) {
        this(methodName, startingLocation, endingLocation, null);
    }
    public MethodLocation(String methodName, IntRange tokenRange) {
        this(methodName, null, null, tokenRange);
    }
    
    public MethodLocation(String methodName, Location startingLocation, Location endingLocation, IntRange tokenRange) {
        this.methodName = methodName;
        this.start = startingLocation;
        this.end = endingLocation;
        this.tokenRange = tokenRange;   
    }
    public String getMethodName() {
        return methodName;
    }

    public Location getStartingLocation() {
        return start;
    }

    public Location getEndingLocation() {
        return end;
    }

    public Set<Redundancy> getRedundancyList() {
        return redondanceList;
    }

    public void setRedondanceList(Set<Redundancy> redondanceList) {
        this.redondanceList.clear();
        this.redondanceList.addAll(redondanceList);
    }
       
    public static MethodLocation findMethod(List<MethodLocation> methodList, Token token) {
        for (MethodLocation methodLocalisation : methodList) {            
            if (methodLocalisation.start.getFileName().equals(token.getLocation().getFileName())
                    && methodLocalisation.getStartingLocation().getLine() <= token.getStartingLine() 
                    && methodLocalisation.getEndingLocation().getLine() >=  token.getStartingLine() ) {
                return methodLocalisation;
            }
                    
        }
        return null;
    }
    
    public static MethodLocation findMethod(List<MethodLocation> methodList, Integer tokenPosition) {
        for (MethodLocation methodLocalisation : methodList) {
            if (methodLocalisation.containsPosition(tokenPosition)) {
                return methodLocalisation;
            }
        }
        return null;
    }
    
    public boolean containsPosition(int tokenPosition) {
        return tokenRange.containsInteger(tokenPosition);
    }
    public IntRange getTokenRange() {
        return tokenRange;
    }
    
}
 