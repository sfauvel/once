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
import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.report.ReportingImpl;

public class ManagerToken {

    public static final Logger LOG = Logger.getLogger(ManagerToken.class);
    private final Code code;
    
    public ManagerToken(final List<Token> tokenList) {
        this(new Code(tokenList));
    }

    public ManagerToken(Code code) {
        this.code = code;
    }

    public List<Redundancy> getRedondance(int tailleMin) {
        return getRedondance(new Configuration(ComparatorWithSubstitution.class)
                .withTailleMin(tailleMin));
    }

    public List<Redundancy> getRedondance(Configuration configuration) {
        List<Integer> positionList = getPositionToManage();
        CodeComparator comparator = configuration.getComparateur(code);
        LOG.info("Tri des " + positionList.size() + " tokens...");
        sortPositionList(positionList, comparator);
        traceSortedToken(positionList);
        LOG.info("Calcul des tailles de redondance...");
        int[] redundancySize = comparator.getRedundancySize(positionList);
        traceTailleRedondance(positionList, redundancySize);

        LOG.info("Calcul des redondances...");
        List<Redundancy> listeRedondance = calculerRedondance(positionList, redundancySize, configuration.getTailleMin());
        LOG.info("Suppression des chevauchements...");
        listeRedondance = removeOverlap(listeRedondance);
        LOG.info("Suppression des doublons...");
        listeRedondance = supprimerDoublon(listeRedondance);
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
    
    public Token getToken(Integer position) {
        return code.getToken(position);
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

    public List<Redundancy> supprimerDoublon(List<Redundancy> redundancyList) {
        LOG.info("Nombre de redondance avant suppression des doublons: " + redundancyList.size());
        Redundancy.removeDuplicatedList(redundancyList);
        LOG.info("Nombre de redondance après suppression des doublons: " + redundancyList.size());
        return redundancyList;
    }

    public void sortPositionList(List<Integer> positionList, CodeComparator comparator) {
        ReportingImpl report = null;
        if (ReportingImpl.TRACE_TOKEN.isDebugEnabled()) {
            report = new ReportingImpl();
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

    public List<Redundancy> calculerRedondance(List<Integer> positionList, int[] tailleRedondance, int tailleMin) {
        List<Redundancy> listeRedondance = new ArrayList<Redundancy>();
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

    public void ajouterRedondanceInterne(List<Integer> positionList, List<Redundancy> listeRedondance, int[] listeTailleRedondance, int indexDepart, int indexCourant, int tailleMin) {
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

        ajouterRedondanceInterne(positionList, listeRedondance, listeTailleRedondance, indexDepart, indexCourant, tailleMin);
    }

    public Redundancy createRedundancy(int redondanceSize, List<Integer> subList) {
        Redundancy redundancy = new Redundancy(redondanceSize)
                .withStartingCodeAt(subList);

        registerRedundancyToMethod(subList, redundancy);
        return redundancy;
    }

    protected void registerRedundancyToMethod(List<Integer> subList, Redundancy redundancy) {
        for (Integer firstTokenPosition : subList) {

            Token lastToken = code.getToken(firstTokenPosition + redundancy.getDuplicatedTokenNumber() - 1);

            MethodLocalisation method = MethodLocalisation.findMethod(code.getMethodList(), lastToken);
            if (method != null) {
                method.getRedondanceList().add(redundancy);
            }
        }
    }

    public int min(int[] tableauValeur, int debut, int fin) {
        return Arrays.stream(tableauValeur, debut, fin+1).min().orElse(0);
    }

}
