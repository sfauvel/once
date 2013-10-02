package fr.sf.once.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import fr.sf.once.ast.ExtractTokenFileVisitor;
import fr.sf.once.comparator.ComparateurAvecSubstitutionEtType;
import fr.sf.once.core.ManagerToken;
import fr.sf.once.model.Code;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Redondance;
import fr.sf.once.model.Token;

public class ReportingImplBeta implements Reporting {

    public static final Logger LOG = Logger.getLogger(ReportingImplBeta.class);

    private Logger tokenLogger;

    private List<MethodLocalisation> methodList;

    public ReportingImplBeta(List<MethodLocalisation> methodList) {
        this.methodList = methodList;
        this.tokenLogger = TRACE_TOKEN;
    }

    public void afficherRedondance(final ManagerToken manager, final int tailleMin, List<Redondance> listeRedondance) {
        afficherRedondance(manager.getTokenList(), tailleMin, listeRedondance);
        displayFullDuplicationBetweenMethods(manager);
            
    }

    public void afficherRedondance(final List<Token> tokenList, final int tailleMin, List<Redondance> listeRedondance) {
        LOG_CSV.info("Taille Redondance;Nombre redondance;Note");
        Collections.sort(listeRedondance, new Comparator<Redondance>() {
            @Override
            public int compare(Redondance redondance1, Redondance redondance2) {
                return redondance2.getDuplicatedTokenNumber() - redondance1.getDuplicatedTokenNumber();
            }
        });

        for (Redondance redondance : listeRedondance) {
            List<Integer> firstTokenList = redondance.getStartRedundancyList();
            Integer positionPremierToken = firstTokenList.get(0);
            if (isNombreLigneSuperieurA(tokenList, positionPremierToken, redondance.getDuplicatedTokenNumber(), 0)) {
                long duplicationScore = computeScore(redondance);
                if (redondance.getDuplicatedTokenNumber() > 5 && duplicationScore > tailleMin) {
                    displayCsvRedundancy(tokenList, redondance, duplicationScore);
                    afficherCodeRedondant(tokenList, redondance);
                }
            }
        }
        // displayMethod(tokenList, listeTokenTrie, listeRedondance);
    }

    private void displayCsvRedundancy(final List<Token> tokenList, Redondance redondance, long duplicationScore) {
        if (LOG_CSV.isInfoEnabled()) {
            StringBuffer bufferCsv = new StringBuffer();
            appendCsvInformation(bufferCsv, tokenList, redondance, duplicationScore);
            LOG_CSV.info(bufferCsv.toString());
        }
    }

    private void appendCsvInformation(StringBuffer bufferCsv, final List<Token> tokenList, Redondance redondance, long duplicationScore) {
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        int redundancyNumber = redondance.getRedundancyNumber();

        bufferCsv.append(redondance.getDuplicatedTokenNumber())
                .append(";")
                .append(redundancyNumber)
                .append(";")
                .append(duplicationScore)
                .append(";");
        for (Integer firstTokenPosition : firstTokenList) {
            Localisation localisationDebut = tokenList.get(firstTokenPosition).getlocalisation();
            Localisation localisationFin = tokenList.get(firstTokenPosition + redondance.getDuplicatedTokenNumber()).getlocalisation();

            bufferCsv.append(localisationDebut.getNomFichier())
                    .append("(")
                    .append(localisationDebut.getLigne())
                    .append("/")
                    .append(localisationFin.getLigne())
                    .append(") ");
        }
    }

    private int computeScore(Redondance redondance) {
        int redundancyNumber = redondance.getRedundancyNumber();
        return redundancyNumber * redondance.getDuplicatedTokenNumber();
    }

    private List<String> getSubstitution(final List<Token> tokenList, Redondance redondance) {
        List<String> substitutionList = new ArrayList<String>();
        int duplicatedTokenNumber = redondance.getDuplicatedTokenNumber();
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        Set<String> substitution = new HashSet<String>();
        for (int i = 0; i < duplicatedTokenNumber; i++) {
            Set<String> listeValeur = new HashSet<String>();
            for (Integer firstPosition : firstTokenList) {
                int position = firstPosition + i;
                listeValeur.add(tokenList.get(position).getValeurToken());
            }
            if (listeValeur.size() > 1) {
                String join = StringUtils.join(listeValeur, ", ");
                if (!substitution.contains(join)) {
                    substitution.add(join);
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("  ")
                            .append(listeValeur.size())
                            .append(" valeurs: ");

                    buffer.append(join);
                    substitutionList.add(buffer.toString());
                }
            }
        }
        return substitutionList;
    }

    private boolean isNombreLigneSuperieurA(List<Token> tokenList, Integer positionPremierToken, int nombreTokenRedondant, int nombreLigneMin) {
        Localisation localisationDebut = tokenList.get(positionPremierToken).getlocalisation();
        Localisation localisationFin = tokenList.get(positionPremierToken + nombreTokenRedondant - 1).getlocalisation();

        int nombreLigne = localisationFin.getLigne() - localisationDebut.getLigne();

        return nombreLigne > nombreLigneMin;
    }

    public void afficherCodeRedondant(final List<Token> tokenList, Redondance redondance) {
        if (LOG_RESULTAT.isInfoEnabled()) {

            List<String> substitutionList = getSubstitution(tokenList, redondance);
            List<Integer> firstTokenList = redondance.getStartRedundancyList();
            int redundancyNumber = firstTokenList.size();
            LOG_RESULTAT.info("Taille:" + redondance.getDuplicatedTokenNumber() + " Longueur:" + redundancyNumber + " Substitutions:" + substitutionList.size());

            for (Integer firstTokenPosition : firstTokenList) {
                final int NB_MAX_DISPLAY = 200;
                int fin = firstTokenPosition + Math.min(NB_MAX_DISPLAY, redondance.getDuplicatedTokenNumber());

                StringBuffer buffer = new StringBuffer();

                Token firstToken = tokenList.get(firstTokenPosition);
                Integer ligneDebut = firstToken.getLigneDebut();
                Token lastToken = tokenList.get(firstTokenPosition + redondance.getDuplicatedTokenNumber() - 1);
                Integer ligneFin = lastToken.getLigneDebut();
                if (LOG_RESULTAT.isTraceEnabled()) {
                    LOG_RESULTAT.trace("First position:" + firstTokenPosition + " ligne debut:" + ligneDebut + " ligne fin:" + ligneFin);
                }
                MethodLocalisation method = MethodLocalisation.findMethod(methodList, lastToken);
                if (method != null) {
                    method.getRedondanceList().add(redondance);
                    int methodLineNumber = method.getLocalisationFin().getLigne() - method.getLocalisationDebut().getLigne();
                    int redundancyLineNumber = ligneFin - ligneDebut;
                    int pourcentage = computePourcentage(redundancyLineNumber, methodLineNumber);

                    buffer.append(pourcentage)
                            .append("% ")
                            .append(redundancyLineNumber)
                            .append(" lignes sur ")
                            .append(methodLineNumber);

                    firstToken.getlocalisation().appendLocalisation(buffer);
                    buffer.append(" <-> ");
                    lastToken.getlocalisation().appendLocalisation(buffer);

                    buffer.append(" ")
                            .append(method.getMethodName())
                            .append("(")
                            .append(method.getLocalisationDebut().getLigne())
                            .append(" <-> ")
                            .append(method.getLocalisationFin().getLigne())
                            .append(")")
                            .append(" ");
                    displayVisualRedondance(method, ligneDebut, ligneFin);
                } else {
                    buffer.append(" No method ");
                }

                if (LOG_RESULTAT.isDebugEnabled()) {
                    buffer.append(": ");
                    for (int i = firstTokenPosition; i < fin; i++) {
                        buffer.append(tokenList.get(i).getValeurToken()).append(" ");
                    }
                    if (redondance.getDuplicatedTokenNumber() >= NB_MAX_DISPLAY) {
                        buffer.append("...");
                    }
                }
                LOG_RESULTAT.info("  " + buffer.toString());

            }
            Collections.sort(substitutionList);
            for (String substitution : substitutionList) {
                LOG_RESULTAT.info("  " + substitution);
            }
        }
    }

    public void afficherMethodeDupliqueAvecSubtitution(final List<Token> tokenList, Redondance redondance) {
        if (redondance.getStartRedundancyList().size() > 0 && isFullMethodDuplicated(tokenList, redondance)) {
            afficherCodeRedondant(tokenList, redondance);
        }
    }

    private boolean isFullMethodDuplicated(final List<Token> tokenList, Redondance redondance) {
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        for (Integer firstTokenPosition : firstTokenList) {
            Integer ligneDebut = tokenList.get(firstTokenPosition).getLigneDebut();
            Token lastToken = tokenList.get(firstTokenPosition + redondance.getDuplicatedTokenNumber() - 1);
            Integer ligneFin = lastToken.getLigneDebut();
            MethodLocalisation method = MethodLocalisation.findMethod(methodList, lastToken);
            if (method != null) {
                method.getRedondanceList().add(redondance);
                int methodSize = method.getLocalisationFin().getLigne() - method.getLocalisationDebut().getLigne();
                if (ligneFin - ligneDebut == methodSize) {
                    return true;
                }
            }
        }
        return false;
    }

    public int computePourcentage(int value, int total) {
        if (total == 0) {
            return 0;
        } else {
            return value * 100 / total;
        }

    }

    private void displayVisualRedondance(MethodLocalisation method, Integer ligneDebut, Integer ligneFin) {
        if (LOG_RESULTAT.isDebugEnabled()) {
            StringBuffer line = new StringBuffer();
            for (int i = method.getLocalisationDebut().getLigne(); i < ligneDebut; i++) {
                line.append(".");
            }
            for (int i = ligneDebut; i <= ligneFin; i++) {
                line.append("*");
            }
            for (int i = ligneFin; i <= method.getLocalisationFin().getLigne(); i++) {
                line.append(".");
            }

            LOG_RESULTAT.debug(method.getMethodName() + "(" + method.getLocalisationDebut().getLigne() + ")" + line.toString());
        }
    }

    @Override
    public void afficheListeToken(final List<Token> listeToken) {
        int lastLine = 0;
        int lastColumn = 0;
        for (Token token : listeToken) {
            Localisation localisation = token.getlocalisation();

            display(token);

            int currentLine = localisation.getLigne();
            int currentColumn = localisation.getColonne();
            if (currentLine < lastLine) {
                getTokenLogger().error("Le numéro de ligne diminue");
            } else if (currentLine == lastLine && currentColumn < lastColumn) {
                getTokenLogger().error("Le numéro de colonne diminue");
            }
            lastLine = currentLine;
            lastColumn = currentColumn;
        }
    }

    public void display(final Token token) {
        if (getTokenLogger().isInfoEnabled()) {
            getTokenLogger().info(token.format());
        }
    }

    public void display(final List<Token> tokenList) {
        for (Token token : tokenList) {
            display(token);
        }
    }

    // public void display(final Code code) {
    // display(code.getTokenList());
    // }

    public void display(final Code code) {
        List<MethodLocalisation> methodList = code.getMethodList();
        if (methodList.size() == 0) {
            display(code.getTokenList());
        } else {
            int tokenNumber = code.getTokenList().size();

            Iterator<MethodLocalisation> iteratorMethod = methodList.iterator();
            MethodLocalisation currentMethod = iteratorMethod.next();
            for (int tokenPosition = 0; tokenPosition < tokenNumber; tokenPosition++) {
                Token token = code.getToken(tokenPosition);

                if (currentMethod != null && tokenPosition >= currentMethod.getTokenRange().getMinimumInteger()) {
                    getTokenLogger().info(tokenPosition + " " + token.format() + " " + currentMethod.getMethodName() + " " + currentMethod.getTokenRange().getMinimumInteger() + "-" + currentMethod.getTokenRange().getMaximumInteger());
                } else {
                    getTokenLogger().info(tokenPosition + " " + token.format());
                }

                if (currentMethod != null && tokenPosition >= currentMethod.getTokenRange().getMaximumInteger()) {
                    if (iteratorMethod.hasNext()) {
                        currentMethod = iteratorMethod.next();
                    } else {
                        currentMethod = null;
                    }
                }
            }
        }
    }

    public void checkLocalisation(final Code code) {
        int lastLine = 0;
        int lastColumn = 0;
        for (Token token : code.getTokenList()) {
            Localisation localisation = token.getlocalisation();

            int currentLine = localisation.getLigne();
            int currentColumn = localisation.getColonne();
            if (currentLine < lastLine) {
                LOG_RESULTAT.error("Le numéro de ligne diminue");
            } else if (currentLine == lastLine && currentColumn < lastColumn) {
                LOG_RESULTAT.error("Le numéro de colonne diminue");
            }
            lastLine = currentLine;
            lastColumn = currentColumn;
        }
    }

    public Logger getTokenLogger() {
        return tokenLogger;
    }

    public Map<MethodLocalisation, Set<IntRange>> getDuplicationWithMethod(MethodLocalisation methodA) {
        Map<MethodLocalisation, Set<IntRange>> duplicatedMethodMap = new HashMap<MethodLocalisation, Set<IntRange>>();
        Set<Redondance> methodARedondanceList = methodA.getRedondanceList();
        for (Redondance redondance : methodARedondanceList) {
            IntRange duplicatedRange = findDuplicatedRange(methodA, redondance);
            LOG.debug(methodA.getMethodName() + " " + methodA.getTokenRange().getMinimumInteger());
            LOG.debug("Redondance :" + redondance.getDuplicatedTokenNumber() + " " + redondance.getRedundancyNumber());

            for (Integer firstToken : redondance.getStartRedundancyList()) {
                Set<IntRange> duplicatedMethodList = getDuplicatedMethodList(duplicatedMethodMap, firstToken);
                if (duplicatedMethodList != null) {
                    LOG.debug("   " + duplicatedRange);
                    duplicatedMethodList.add(duplicatedRange);
                }
            }
        }
        duplicatedMethodMap.remove(methodA);
        return duplicatedMethodMap;

    }

    public static class CommunRange {
        public IntRange rangeA;
        public IntRange rangeB;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((rangeA == null) ? 0 : rangeA.hashCode());
            result = prime * result + ((rangeB == null) ? 0 : rangeB.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CommunRange other = (CommunRange) obj;
            if (rangeA == null) {
                if (other.rangeA != null)
                    return false;
            } else if (!rangeA.equals(other.rangeA))
                return false;
            if (rangeB == null) {
                if (other.rangeB != null)
                    return false;
            } else if (!rangeB.equals(other.rangeB))
                return false;
            return true;
        }

    }

    public Map<MethodLocalisation, Set<CommunRange>> getDuplicationWithMethodFull(MethodLocalisation methodA) {
        Map<MethodLocalisation, Set<CommunRange>> duplicatedMethodMap = new HashMap<MethodLocalisation, Set<CommunRange>>();
        Set<Redondance> methodARedondanceList = methodA.getRedondanceList();
        for (Redondance redondance : methodARedondanceList) {
            IntRange duplicatedRange = findDuplicatedRange(methodA, redondance);
            LOG.debug(methodA.getMethodName() + " " + methodA.getTokenRange().getMinimumInteger());
            LOG.debug("Redondance :" + redondance.getDuplicatedTokenNumber() + " " + redondance.getRedundancyNumber());

            for (Integer firstToken : redondance.getStartRedundancyList()) {
                Set<CommunRange> duplicatedMethodList = getDuplicatedMethodListCommunRange(duplicatedMethodMap, firstToken);
                if (duplicatedMethodList != null) {
                    LOG.debug("   " + duplicatedRange);
                    CommunRange communRange = new CommunRange();
                    communRange.rangeA = duplicatedRange;
                    communRange.rangeB = new IntRange(firstToken.intValue(), firstToken + redondance.getRedundancyNumber() - 1);
                    duplicatedMethodList.add(communRange);
                }
            }
        }
        duplicatedMethodMap.remove(methodA);
        return duplicatedMethodMap;

    }

    private Set<CommunRange> getDuplicatedMethodListCommunRange(Map<MethodLocalisation, Set<CommunRange>> duplicatedMethodMap, Integer firstToken) {
        MethodLocalisation findMethod = MethodLocalisation.findMethod(methodList, firstToken);
        if (findMethod == null) {
            LOG.debug("No method at " + firstToken);
            return null;
        }
        LOG.debug("Add duplication to " + findMethod.getMethodName());
        if (!duplicatedMethodMap.containsKey(findMethod)) {
            duplicatedMethodMap.put(findMethod, new HashSet<CommunRange>());
        }
        Set<CommunRange> duplicatedMethodList = duplicatedMethodMap.get(findMethod);
        return duplicatedMethodList;
    }

    private Set<IntRange> getDuplicatedMethodList(Map<MethodLocalisation, Set<IntRange>> duplicatedMethodMap, Integer firstToken) {
        MethodLocalisation findMethod = MethodLocalisation.findMethod(methodList, firstToken);
        if (findMethod == null) {
            LOG.debug("No method at " + firstToken);
            return null;
        }
        LOG.debug("Add duplication to " + findMethod.getMethodName());
        if (!duplicatedMethodMap.containsKey(findMethod)) {
            duplicatedMethodMap.put(findMethod, new HashSet<IntRange>());
        }
        Set<IntRange> duplicatedMethodList = duplicatedMethodMap.get(findMethod);
        return duplicatedMethodList;
    }

    public IntRange findDuplicatedRange(MethodLocalisation methodLocalisation, Redondance redondance) {
        for (Integer firstTokenPosition : redondance.getStartRedundancyList()) {
            if (methodLocalisation.containsPosition(firstTokenPosition)) {
                return new IntRange(firstTokenPosition.intValue(), firstTokenPosition + redondance.getDuplicatedTokenNumber() - 1);
            }
        }
        return null;
    }

    public List<IntRange> sortRangeByMinimum(Set<IntRange> listRange) {
        ArrayList<IntRange> resultList = new ArrayList<IntRange>(listRange);
        Collections.sort(resultList, new Comparator<IntRange>() {

            @Override
            public int compare(IntRange o1, IntRange o2) {
                return (o2.getMaximumInteger() - o2.getMinimumInteger())
                        - (o1.getMaximumInteger() - o1.getMinimumInteger());
            }

        });

        ArrayList<IntRange> finalList = new ArrayList<IntRange>();

        IntRange maxRange = resultList.get(0);
        for (IntRange intRange : resultList) {
            if (!isOverlaps(intRange, finalList)) {
                finalList.add(intRange);
            }
        }
        Collections.sort(finalList, new RangeOrder());
        return finalList;
    }

    public List<CommunRange> sortRangeByMinimumFull(Set<CommunRange> listRange) {
        ArrayList<CommunRange> resultList = new ArrayList<CommunRange>(listRange);
        Collections.sort(resultList, new Comparator<CommunRange>() {

            @Override
            public int compare(CommunRange o1, CommunRange o2) {
                return (o2.rangeA.getMaximumInteger() - o2.rangeA.getMinimumInteger())
                        - (o1.rangeA.getMaximumInteger() - o1.rangeA.getMinimumInteger());
            }

        });

        ArrayList<CommunRange> finalList = new ArrayList<CommunRange>();

        CommunRange maxRange = resultList.get(0);
        for (CommunRange intRange : resultList) {
            if (!isOverlapsFull(intRange, finalList)) {
                finalList.add(intRange);
            }
        }
        Collections.sort(resultList, new Comparator<CommunRange>() {

            @Override
            public int compare(CommunRange o1, CommunRange o2) {
                int compare = o1.rangeA.getMinimumInteger() - o2.rangeA.getMinimumInteger();
                if (compare == 0) {
                    compare = o1.rangeA.getMaximumInteger() - o2.rangeA.getMaximumInteger();
                }
                return compare;
            }

        });

        return finalList;
    }

    private boolean isOverlapsFull(CommunRange range, List<CommunRange> rangeList) {
        for (CommunRange intRange : rangeList) {
            if (range.rangeA.overlapsRange(intRange.rangeA)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverlaps(IntRange range, List<IntRange> rangeList) {
        for (IntRange intRange : rangeList) {
            if (range.overlapsRange(intRange)) {
                return true;
            }
        }
        return false;
    }

    public void displayFullDuplicationBetweenMethods(ManagerToken manager) {
        List<MethodLocalisation> methodList = manager.getMethodList();
        for (MethodLocalisation methodLocalisation : methodList) {
            if (methodLocalisation.getMethodName().equals("getPrerequisList")) {
                LOG.debug("Début de recherche sur la méthode " + methodLocalisation.getMethodName());
                Map<MethodLocalisation, Set<IntRange>> duplicationWithMethod = getDuplicationWithMethod(methodLocalisation);
                Map<MethodLocalisation, Set<ReportingImplBeta.CommunRange>> duplicationWithMethodFull = getDuplicationWithMethodFull(methodLocalisation);
                IntRange tokenRange = methodLocalisation.getTokenRange();

                LOG.debug("Affichage des duplication sur la méthode " + methodLocalisation.getMethodName());
                // for (Entry<MethodLocalisation, Set<IntRange>>
                // duplicationMethod : duplicationWithMethod.entrySet()) {
                for (Entry<MethodLocalisation, Set<ReportingImplBeta.CommunRange>> duplicationMethod : duplicationWithMethodFull.entrySet()) {
                    Set<ReportingImplBeta.CommunRange> value = duplicationMethod.getValue();
                    List<ReportingImplBeta.CommunRange> sortedRangeList = sortRangeByMinimumFull(value);
                    int totalDuplication = 0;
                    int rangeNumber = 0;
                    List<Integer> sizeList = new ArrayList<Integer>();
                    int methodSize = tokenRange.getMaximumInteger() - tokenRange.getMinimumInteger();
                    for (ReportingImplBeta.CommunRange intRange : sortedRangeList) {
                        int duplicationSize = intRange.rangeA.getMaximumInteger() - intRange.rangeA.getMinimumInteger();
                        totalDuplication += duplicationSize;
                        sizeList.add(computePourcentage(duplicationSize, methodSize));
                        rangeNumber++;
                        LOG.info("  " + intRange.rangeA + " " + intRange.rangeB + " " + duplicationSize);
                    }
                    LOG.debug("  Methode range " + tokenRange);
                    int pourcentage = computePourcentage(totalDuplication, methodSize);
                    LOG.info(methodLocalisation.getMethodName() + " with " + duplicationMethod.getKey().getMethodName() + ": "
                            + totalDuplication + " tokens " + pourcentage + "%   range number:" + rangeNumber + " (" + StringUtils.join(sizeList, "% ") + "%)");

                    ComparateurAvecSubstitutionEtType comparator = new ComparateurAvecSubstitutionEtType(manager);
                    for (ReportingImplBeta.CommunRange intRange : sortedRangeList) {
                        int redundancy = comparator.getRedundancySizeWithPreviousSubstitution(intRange.rangeA.getMinimumInteger(), intRange.rangeB.getMinimumInteger());
                        int redundancyWithNew = new ComparateurAvecSubstitutionEtType(manager).getRedundancySizeWithPreviousSubstitution(intRange.rangeA.getMinimumInteger(), intRange.rangeB.getMinimumInteger());
                        LOG.info("redundancy: " + redundancy + "/" + redundancyWithNew);
                    }
                }
            }
        }
    }

}
