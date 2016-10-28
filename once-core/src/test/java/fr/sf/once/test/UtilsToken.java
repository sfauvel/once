package fr.sf.once.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.model.Code;
import fr.sf.once.model.Location;
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
            tokenList.add(new Token(new Location("", ligne, 0), tokenValue, Type.VALUE));
            ligne++;
        }
        return Collections.unmodifiableList(tokenList);
    }
    
    public static void afficher(List<Token> tokenList, List<Integer> positionList) {
        for (Integer tokenPosition : positionList) {
            LOG.info(tokenList.get(tokenPosition).getTokenValue());
            LOG.info(tokenPosition);
        }
    }

    /**
     * Création d'une liste de positions.
     * Il s'agit d'un tableau contenant les valeurs 0, 1, 2, ....
     * @param size
     * @return
     */
    public static List<Integer> createPositionList(int size) {
        List<Integer> positionList = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            positionList.add(i);
        }
        return positionList;
    }    
    
    public static Integer[] createPositionArray(int size) {
        return IntStream.rangeClosed(0, size-1).mapToObj(n -> n).toArray(s -> new Integer[s]);
    }
    
    public static RedundancyFinder createManagerToken(final List<Token> tokenList) {
        return new RedundancyFinder(new Code(tokenList));
    }
    
    public static RedundancyFinder createManagerToken(String... tokenValueList) {
        return createManagerToken(createUnmodifiableTokenList(tokenValueList));
    }
    
    /**
     * Create a token list from a string array. Each string can contains tokens separates by space.
     * @param stringTokenList
     * @return
     */
    public static List<Token> createTokenList(String... stringTokenList) {
        ArrayList<Token> resultatList = new ArrayList<Token>();
        for (String oneStringToken : stringTokenList) {
            for (String tokenValue : oneStringToken.split(" ")) {
                resultatList.add(createToken(tokenValue, resultatList.size()));
            }
        }
        return resultatList;
    }

    private static Token createToken(String tokenValue, int index) {
        Type type = "BREAK".equalsIgnoreCase(tokenValue) ? Type.BREAK : Type.VALUE;
        return new Token(new Location("", index, 0), String.valueOf(tokenValue), type);
    }
}
