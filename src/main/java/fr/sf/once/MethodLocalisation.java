package fr.sf.once;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodLocalisation {

    private String methodName;
    private Localisation localisationDebut;
    private Localisation localisationFin;
    private Set<Redondance> redondanceList = new HashSet<Redondance>();
    
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public Localisation getLocalisationDebut() {
        return localisationDebut;
    }
    public void setLocalisationDebut(Localisation localisationDebut) {
        this.localisationDebut = localisationDebut;
    }
    public Localisation getLocalisationFin() {
        return localisationFin;
    }
    public void setLocalisationFin(Localisation localisationFin) {
        this.localisationFin = localisationFin;
    }
    public Set<Redondance> getRedondanceList() {
        return redondanceList;
    }
    public void setRedondanceList(Set<Redondance> redondanceList) {
        this.redondanceList = redondanceList;
    }
    
//    public static MethodLocalisation findMethod(List<MethodLocalisation> methodList, int line) {
//        for (MethodLocalisation methodLocalisation : methodList) {            
//            if (methodLocalisation.getLocalisationDebut().ligne<= line 
//                    && methodLocalisation.getLocalisationFin().ligne>= line) {
//                return methodLocalisation;
//            }
//                    
//        }
//        return null;
//    }
    
    public static MethodLocalisation findMethod(List<MethodLocalisation> methodList, Token token) {
        for (MethodLocalisation methodLocalisation : methodList) {            
            if (methodLocalisation.localisationDebut.getNomFichier().equals(token.getlocalisation().getNomFichier())
                    && methodLocalisation.getLocalisationDebut().getLigne() <= token.getLigneDebut() 
                    && methodLocalisation.getLocalisationFin().getLigne() >=  token.getLigneDebut() ) {
                return methodLocalisation;
            }
                    
        }
        return null;
    }
    
    

}
 