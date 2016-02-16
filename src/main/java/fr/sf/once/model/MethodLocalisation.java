package fr.sf.once.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;


public class MethodLocalisation {

    private final String methodName;
    private final Localisation start;
    private final Localisation end;
    private final IntRange tokenRange;
    private final Set<Redundancy> redondanceList = new HashSet<Redundancy>();
    
    public MethodLocalisation(String methodName, Localisation localisationDebut, Localisation localisationFin) {
        this(methodName, localisationDebut, localisationFin, null);
    }
    public MethodLocalisation(String methodName, IntRange tokenRange) {
        this(methodName, null, null, tokenRange);
    }
    
    public MethodLocalisation(String methodName, Localisation localisationDebut, Localisation localisationFin, IntRange tokenRange) {
        this.methodName = methodName;
        this.start = localisationDebut;
        this.end = localisationFin;
        this.tokenRange = tokenRange;
    }
    public String getMethodName() {
        return methodName;
    }

    public Localisation getLocalisationDebut() {
        return start;
    }

    public Localisation getLocalisationFin() {
        return end;
    }

    public Set<Redundancy> getRedondanceList() {
        return redondanceList;
    }

    public void setRedondanceList(Set<Redundancy> redondanceList) {
        this.redondanceList.clear();
        this.redondanceList.addAll(redondanceList);
    }
       
    public static MethodLocalisation findMethod(List<MethodLocalisation> methodList, Token token) {
        for (MethodLocalisation methodLocalisation : methodList) {            
            if (methodLocalisation.start.getNomFichier().equals(token.getlocalisation().getNomFichier())
                    && methodLocalisation.getLocalisationDebut().getLigne() <= token.getLigneDebut() 
                    && methodLocalisation.getLocalisationFin().getLigne() >=  token.getLigneDebut() ) {
                return methodLocalisation;
            }
                    
        }
        return null;
    }
    public static MethodLocalisation findMethod(List<MethodLocalisation> methodList, Integer tokenPosition) {
        for (MethodLocalisation methodLocalisation : methodList) {
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
 