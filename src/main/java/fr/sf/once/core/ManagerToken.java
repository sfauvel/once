package fr.sf.once.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.comparator.Comparateur;
import fr.sf.once.comparator.ComparateurAvecSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redondance;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.report.ReportingImpl;

public class ManagerToken extends Code {

    public static final Logger LOG = Logger.getLogger(ManagerToken.class);

    public ManagerToken(final List<Token> tokenList) {
        super(tokenList);
    }

    public List<Redondance> getRedondance(int tailleMin) {
        return getRedondance(new Configuration(ComparateurAvecSubstitution.class)
                .withTailleMin(tailleMin));
    }

    public List<Redondance> getRedondance(Configuration configuration) {
        List<Integer> positionList = getPositionToManage();
        Comparateur comparator = configuration.getComparateur(this);
        LOG.info("Tri des " + positionList.size() + " tokens...");
        sortPositionList(positionList, comparator);
        traceSortedToken(positionList);
        LOG.info("Calcul des tailles de redondance...");
        int[] redundancySize = comparator.getRedundancySize(positionList);
        traceTailleRedondance(positionList, redundancySize);

        LOG.info("Calcul des redondances...");
        List<Redondance> listeRedondance = calculerRedondance(positionList, redundancySize, configuration.getTailleMin());
        LOG.info("Suppression des chevauchements...");
        listeRedondance = removeOverlap(listeRedondance);
        LOG.info("Suppression des doublons...");
        listeRedondance = supprimerDoublon(listeRedondance);
        LOG.info("Fin du traitement");
        return listeRedondance;

    }

    private List<Redondance> removeOverlap(List<Redondance> redundancyList) {
        LOG.info("Nombre de redondance avant suppression des chevauchements: " + redundancyList.size());
        for (Iterator<Redondance> iterator = redundancyList.iterator(); iterator.hasNext();) {
            Redondance redondance = iterator.next();
            redondance.removeOverlapRedundancy();
            if (redondance.getFirstTokenList().size() <= 1) {
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

    private void traceTailleRedondance(List<Integer> positionList, int[] tailleRedondance) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nXXXX\n  traceTailleRedondance");
            int position = 0;
            for (Integer tokenPosition : positionList) {
                traceToken(tokenPosition);
                if (position < tailleRedondance.length) {
                    LOG.debug("Taille redondance:" + tailleRedondance[position]);
                }
                position++;
            }
        }
    }

    public List<Redondance> supprimerDoublon(List<Redondance> redundancyList) {
        LOG.info("Nombre de redondance avant suppression des doublons: " + redundancyList.size());
        Redondance.removeDuplicatedList(redundancyList);
        LOG.info("Nombre de redondance après suppression des doublons: " + redundancyList.size());
        return redundancyList;
    }

    public void sortPositionList(List<Integer> positionList, Comparateur comparator) {
        ReportingImpl report = null;
        if (ReportingImpl.TRACE_TOKEN.isDebugEnabled()) {
            report = new ReportingImpl(null);
            ReportingImpl.TRACE_TOKEN.debug("\nXXXX\n  getListeTokenTrier non trié");
            for (Token token : getTokenList()) {
                report.display(token);
            }
        }
        Collections.sort(positionList, comparator);

        if (ReportingImpl.TRACE_TOKEN.isDebugEnabled()) {
            ReportingImpl.TRACE_TOKEN.debug("\nXXXX\n  getListeTokenTrier trié");
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

    public List<Redondance> calculerRedondance(List<Integer> positionList, int[] tailleRedondance, int tailleMin) {
        List<Redondance> listeRedondance = new ArrayList<Redondance>();
        ajouterRedondanceInterne(positionList, listeRedondance, tailleRedondance, 0, 0, 0);
        for (int i = 1; i < tailleRedondance.length; i++) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("index : " + i);
            }
            if (tailleRedondance[i] > tailleRedondance[i - 1]) {
                ajouterRedondanceInterne(positionList, listeRedondance, tailleRedondance, i, i, Math.max(tailleMin, tailleRedondance[i - 1]));
            }
        }
        return listeRedondance;
    }

    public void ajouterRedondanceInterne(List<Integer> positionList, List<Redondance> listeRedondance, int[] listeTailleRedondance, int indexDepart, int indexCourant, int tailleMin) {
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

        listeRedondance.add(createRedondance(tailleInitiale, positionList.subList(indexDepart, indexCourant + 1)));

        if (LOG.isDebugEnabled()) {
            for (Integer i : positionList.subList(indexDepart, indexCourant + 1)) {
                LOG.debug("  position:" + i);
            }
        }

        ajouterRedondanceInterne(positionList, listeRedondance, listeTailleRedondance, indexDepart, indexCourant, tailleMin);
    }

    Redondance createRedondance(int redondanceSize, List<Integer> subList) {
        Redondance redondance = new Redondance(redondanceSize);
        redondance.getFirstTokenList().addAll(subList);
        return redondance;
    }

    public int min(int[] tableauValeur, int debut, int fin) {
        int min = tableauValeur[debut];
        for (int i = debut + 1; i <= fin; i++) {
            min = Math.min(min, tableauValeur[i]);
        }
        return min;
    }

}
