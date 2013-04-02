package fr.sf.once;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;


public abstract class Comparateur implements Comparator<Integer> {
    public static final Logger LOG = Logger.getLogger(Comparateur.class);

    private int profondeur;
    private Code code;
    private StringBuffer traceDebug = new StringBuffer();

    public Comparateur(List<Token> listeToken) {
        this.code = new Code(listeToken);
    }

    public Comparateur(Code code) {
        this.code = code;
    }

    /**
     * Point d'entrée pour la comparaison.
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Integer position1, Integer position2) {
        reinit();
        int result = compareEnProfondeur(position1, position2);

        return result;
    }

    protected void reinit() {
        traceDebug = new StringBuffer();
        profondeur = 0;
    }

    public void sortList(List<Integer> positionList) {
        Collections.sort(positionList, this);
    }

    /**
     * Compare uniquement les valeurs entre deux tokens.
     * 
     * @param token1
     * @param token2
     * @return
     */
    protected abstract int compareTokenValue(Token token1, Token token2);

    /**
     * Permet d'effectuer réellement la comparaison en parcourant les différents
     * tokens.
     * 
     * @param position1
     * @param position2
     * @return
     */
    protected int compareEnProfondeur(Integer position1, Integer position2) {
        if (position1 >= code.getSize() || position2 >= code.getSize()) {
            int result = position2 - position1;
            if (LOG.isDebugEnabled()) {
                LOG.debug("compare(" + position1 + ", " + position2 + ") hors limite = " + result + "  size:" + profondeur);
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
            profondeur++;
            if (LOG.isDebugEnabled()) {
                traceDebug.append(profondeur + ":" + token1.getValeurToken() + "=" + token2.getValeurToken() + " ");
            }
            result = compareEnProfondeur(position1 + 1, position2 + 1);
        } else {
            if (LOG.isDebugEnabled()) {
                traceDebug.append(profondeur + ":" + token1.getValeurToken() + "<>" + token2.getValeurToken() + " ");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("compare(" + position1 + ":" + code.getToken(position1).getValeurToken() + ", " + position2 + ":" + code.getToken(position2).getValeurToken() + ") = " + result + "  size:" + profondeur);
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
            traceDebug.append(profondeur + ":" + token1.getType() + "<>" + token2.getType() + " ");
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
        int[] tailleRedondance = new int[positionToSearch.size() - 1];
        for (int i = 0; i < tailleRedondance.length; i++) {
            tailleRedondance[i] = getRedundancySize(positionToSearch.get(i), positionToSearch.get(i + 1));
        }
        return tailleRedondance;
    }

    public int getRedundancySize(int position1, int position2) {
        compare(position1, position2);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nombre de redondance = " + profondeur + " entre les positions " + position1 + " et " + position2);
            LOG.debug(traceDebug.toString());
        }
        return profondeur;
    }

}
