package fr.sf.once.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.Code;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;

public final class UtilsToken {

    private static final Logger LOG = Logger.getLogger(UtilsToken.class);
    
    private UtilsToken() {
        
    }
    
    /**
     * Création d'une liste de token non modifiable.
     * Les tokens sont de type "VALEUR".
     * 
     * @param tokenValueList La liste des valeurs contenu dans les tokens
     * @return
     */
    public static List<Token> createUnmodifiableTokenList(String... tokenValueList) {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        int ligne = 1;
        for (String tokenValue : tokenValueList) {
            tokenList.add(new Token(new Localisation("", ligne, 0), tokenValue, Type.VALEUR));
            ligne++;
        }
        //return tokenList;
        return Collections.unmodifiableList(tokenList);
    }
    
    public static void afficher(List<Token> tokenList, List<Integer> positionList) {
        for (Integer tokenPosition : positionList) {
            LOG.info(tokenList.get(tokenPosition).getValeurToken());
            LOG.info(tokenPosition);
        }
    }

    /**
     * Création d'une liste de positions.
     * Il s'agit d'un tableau contenant les valeurs 0, 1, 2, ....
     * @param positionListSize
     * @return
     */
    public static List<Integer> createPositionList(int positionListSize) {
        List<Integer> positionList = new ArrayList<Integer>();
        for (int i = 0; i < positionListSize; i++) {
            positionList.add(i);
        }
        return positionList;
    }
    
    public static ManagerToken createManagerToken(final List<Token> tokenList) {
        return new ManagerToken(tokenList);
    }
    
    public static ManagerToken createManagerToken(String... tokenValueList) {
        return createManagerToken(createUnmodifiableTokenList(tokenValueList));
    }
    
}
