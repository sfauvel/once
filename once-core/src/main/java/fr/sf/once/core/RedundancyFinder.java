package fr.sf.once.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.report.ReportingImpl;


/**
 * Find redundancy from the code. 
 */
public class RedundancyFinder {

    public static final Logger LOG = Logger.getLogger(RedundancyFinder.class);
    private final Code code;

    public RedundancyFinder(Code code) {
        this.code = code;
    }

    public List<Redundancy> getRedundancies(int tailleMin) {
        return getRedundancies(new Configuration(ComparatorWithSubstitution.class)
                .withTailleMin(tailleMin));
    }

    public List<Redundancy> getRedundancies(Configuration configuration) {
        List<Integer> positionList = getPositionToManage();
        CodeComparator comparator = configuration.getComparateur(code);
        LOG.info("Tri des " + positionList.size() + " tokens...");
        sortPositionList(positionList, comparator);
        traceSortedToken(positionList);
        LOG.info("Calcul des tailles de redondance...");
        int[] redundancySize = comparator.getRedundancySize(positionList);
        traceRedundancySize(positionList, redundancySize);

        LOG.info("Calcul des redondances...");
        List<Redundancy> listeRedondance = computeRedundancy(positionList, redundancySize, configuration.getTailleMin());
        LOG.info("Suppression des chevauchements...");
        listeRedondance = removeOverlap(listeRedondance);
        LOG.info("Suppression des doublons...");
        listeRedondance = removeRedundancyIncludedInAnotherOne(listeRedondance);
        LOG.info("Fin du traitement");
        return listeRedondance;

    }

    private List<Redundancy> removeOverlap(List<Redundancy> redundancyList) {
        LOG.info("Nombre de redondance avant suppression des chevauchements: " + redundancyList.size());
        for (Iterator<Redundancy> iterator = redundancyList.iterator(); iterator.hasNext();) {
            Redundancy redondance = iterator.next();
            redondance.removeOverlapRedundancy();
            if (redondance.getStartRedundancyList().size() <= 1) {
                iterator.remove();
            }
        }
        LOG.info("Nombre de redondance après suppression des chevauchements: " + redundancyList.size());

        return redundancyList;
    }

    /**
     * Permet de limiter la liste des tokens qui seront utilisés comme point de
     * départ.
     * 
     * @return
     */
    private List<Integer> getPositionToManage() {
        List<Token> tokenList = getTokenList();
        List<Integer> positionList = new ArrayList<Integer>();
        int position = 0;
        for (Token token : tokenList) {
            if (!Type.NON_SIGNIFICATIF.equals(token.getType())) {
                positionList.add(position);
            }
            position++;
        }
        return positionList;
    }

    private List<Token> getTokenList() {
        return code.getTokenList();
    }

    private Token getToken(Integer position) {
        return code.getToken(position);
    }

    private void traceRedundancySize(List<Integer> positionList, int[] redundanciesSize) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nXXXX\n  traceTailleRedondance");
            int position = 0;
            for (Integer tokenPosition : positionList) {
                traceToken(tokenPosition);
                if (position < redundanciesSize.length) {
                    LOG.debug("Taille redondance:" + redundanciesSize[position]);
                }
                position++;
            }
        }
    }

    public List<Redundancy> removeRedundancyIncludedInAnotherOne(List<Redundancy> redundancyList) {
        LOG.info("Nombre de redondance avant suppression des doublons: " + redundancyList.size());
        Redundancy.removeDuplicatedList(redundancyList);
        LOG.info("Nombre de redondance après suppression des doublons: " + redundancyList.size());
        return redundancyList;
    }

    public void sortPositionList(List<Integer> positionList, CodeComparator comparator) {
        traceTokens(positionList, "\nXXXX\n  getListeTokenTrier non trié");
        Collections.sort(positionList, comparator);
        traceTokens(positionList, "\nXXXX\n  getListeTokenTrier trié");
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

    private void traceSortedToken(List<Integer> listePosition) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Liste triée");
            for (Integer tokenPosition : listePosition) {
                traceToken(tokenPosition);
            }
        }
    }

    private void traceToken(int position) {
        if (LOG.isDebugEnabled()) {
            Token token = getToken(position);
            LOG.debug(position + "\t" + token.getlocalisation().getNomFichier() + " ligne:" + token.getlocalisation().getLigne() + "\t N° token:" + position);
            StringBuffer buffer = new StringBuffer();

            int size = getTokenList().size();
            for (int i = position; i < size; i++) {
                buffer.append(getToken(i).getValeurToken());
                buffer.append(" ");
            }
            LOG.debug(buffer.toString());
        }
    }

    public List<Redundancy> computeRedundancy(List<Integer> positionList, int[] tailleRedondance, int tailleMin) {
        List<Redundancy> listeRedondance = new ArrayList<Redundancy>();
        addRedundancyInternal(positionList, listeRedondance, tailleRedondance, 0, 0, 0);
        for (int i = 1; i < tailleRedondance.length; i++) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("index : " + i);
            }
            if (tailleRedondance[i] > tailleRedondance[i - 1]) {
                addRedundancyInternal(positionList, listeRedondance, tailleRedondance, i, i, Math.max(tailleMin, tailleRedondance[i - 1]));
            }
        }
        return listeRedondance;
    }

    private void addRedundancyInternal(List<Integer> positionList, List<Redundancy> listeRedondance, int[] listeTailleRedondance, int indexDepart,
            int indexCourant, int tailleMin) {
        if (indexCourant >= listeTailleRedondance.length) {
            return;
        }
        int tailleInitiale = listeTailleRedondance[indexCourant];
        if (tailleInitiale <= tailleMin) {
            return;
        }
        while (indexCourant < listeTailleRedondance.length && tailleInitiale <= listeTailleRedondance[indexCourant]) {
            indexCourant++;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("ajout depart=" + indexDepart + " nombre=" + (indexCourant - indexDepart + 1) + " taille=" + tailleInitiale);
        }

        listeRedondance.add(createRedundancy(tailleInitiale, positionList.subList(indexDepart, indexCourant + 1)));

        if (LOG.isDebugEnabled()) {
            for (Integer i : positionList.subList(indexDepart, indexCourant + 1)) {
                LOG.debug("  position:" + i);
            }
        }

        addRedundancyInternal(positionList, listeRedondance, listeTailleRedondance, indexDepart, indexCourant, tailleMin);
    }

    private Redundancy createRedundancy(int redondanceSize, List<Integer> subList) {
        return new Redundancy(redondanceSize, subList);
    }

    public int min(int[] tableauValeur, int debut, int fin) {
        return Arrays.stream(tableauValeur, debut, fin + 1).min().orElse(0);
    }

}
