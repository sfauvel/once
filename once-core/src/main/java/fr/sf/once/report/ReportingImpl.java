package fr.sf.once.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public class ReportingImpl implements Reporting {

    private Logger tokenLogger;

    private List<MethodLocalisation> methodList;

    public ReportingImpl(List<MethodLocalisation> methodList) {
        this.methodList = methodList;
        this.tokenLogger = TRACE_TOKEN;
    }

    public void afficherRedondance(final Code code, final int tailleMin, List<FunctionalRedundancy> listeRedondance) {
         LOG_CSV.info("Taille Redondance;Nombre redondance;Note");

//        Collections.sort(listeRedondance, new ComparatorRedundancyByTokenNumber());
        Collections.sort(listeRedondance, new ComparatorRedundancySubstitution(code));

        for (FunctionalRedundancy redondance : listeRedondance) {
            List<Integer> firstTokenList = redondance.getStartRedundancyList();
            Integer positionPremierToken = firstTokenList.get(0);
            if (isNombreLigneSuperieurA(code, positionPremierToken, redondance.getDuplicatedTokenNumber(), 0)) {
                long duplicationScore = computeScore(redondance);
                if (redondance.getDuplicatedTokenNumber() > 5 && duplicationScore > tailleMin) {
                    displayCsvRedundancy(code, redondance, duplicationScore);
                    afficherCodeRedondant(code, redondance);
                }
            }
        }
        // displayMethod(tokenList, listeTokenTrie, listeRedondance);
    }

    private void displayCsvRedundancy(final Code code, Redundancy redondance, long duplicationScore) {
        if (LOG_CSV.isInfoEnabled()) {
            StringBuffer bufferCsv = new StringBuffer();
            appendCsvInformation(bufferCsv, code, redondance, duplicationScore);
            LOG_CSV.info(bufferCsv.toString());
        }
    }

    private void appendCsvInformation(StringBuffer bufferCsv, final Code code, Redundancy redondance, long duplicationScore) {
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        int redundancyNumber = redondance.getRedundancyNumber();

        bufferCsv.append(redondance.getDuplicatedTokenNumber())
                .append(";")
                .append(redundancyNumber)
                .append(";")
                .append(duplicationScore)
                .append(";");
        for (Integer firstTokenPosition : firstTokenList) {
            Localisation localisationDebut = code.getToken(firstTokenPosition).getlocalisation();
            Localisation localisationFin = code.getToken(firstTokenPosition + redondance.getDuplicatedTokenNumber()).getlocalisation();

            bufferCsv.append(localisationDebut.getNomFichier())
                    .append("(")
                    .append(localisationDebut.getLigne())
                    .append("/")
                    .append(localisationFin.getLigne())
                    .append(") ");
        }
    }

    private int computeScore(Redundancy redondance) {
        int redundancyNumber = redondance.getRedundancyNumber();
        return redundancyNumber * redondance.getDuplicatedTokenNumber();
    }

    private List<String> getSubstitution(final Code code, FunctionalRedundancy redondance) {
        List<Set<String>> substitutionListOfSubstitution = redondance.getSubstitutionList();
        List<String> substitutionList = new ArrayList<String>();
        Set<String> substitution = new HashSet<String>();
        for (Set<String> listeValeur : substitutionListOfSubstitution) {
            if (listeValeur.size() > 1) {
                String join = StringUtils.join(listeValeur, ", ");
                if (!substitution.contains(join)) {
                    substitution.add(join);
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("  ")
                            .append(listeValeur.size())
                            .append(" values: ");

                    buffer.append(join);
                    substitutionList.add(buffer.toString());
                }
            }

        }
        return substitutionList;
    }

    private List<Set<String>> getSubstitutionList(final List<Token> tokenList, Redundancy redondance) {
        List<Set<String>> substitutionListOfSubstitution = new ArrayList<Set<String>>();
        int duplicatedTokenNumber = redondance.getDuplicatedTokenNumber();
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        Set<String> substitutionList = new HashSet<String>();
        for (int i = 0; i < duplicatedTokenNumber; i++) {
            Set<String> listeValeur = new HashSet<String>();
            for (Integer firstPosition : firstTokenList) {
                int position = firstPosition + i;
                listeValeur.add(tokenList.get(position).getTokenValue());
            }
            if (listeValeur.size() > 1) {
                String key = listeValeur.toString();
                if (!substitutionList.contains(key)) {
                    substitutionList.add(key);
                    substitutionListOfSubstitution.add(listeValeur);
                }
            }
        }
        return substitutionListOfSubstitution;
    }
    
    List<Set<String>> getSubstitutionList(final Code code, Redundancy redondance) {
        List<Set<String>> substitutionListOfSubstitution = new ArrayList<Set<String>>();
        int duplicatedTokenNumber = redondance.getDuplicatedTokenNumber();
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        Set<String> substitutionList = new HashSet<String>();
        for (int i = 0; i < duplicatedTokenNumber; i++) {
            Set<String> listeValeur = new HashSet<String>();
            for (Integer firstPosition : firstTokenList) {
                int position = firstPosition + i;
                listeValeur.add(code.getToken(position).getTokenValue());
            }
            if (listeValeur.size() > 1) {
                String key = listeValeur.toString();
                if (!substitutionList.contains(key)) {
                    substitutionList.add(key);
                    substitutionListOfSubstitution.add(listeValeur);
                }
            }
        }
        return substitutionListOfSubstitution;
    }

    private boolean isNombreLigneSuperieurA(Code code, Integer positionPremierToken, int nombreTokenRedondant, int nombreLigneMin) {
        Localisation localisationDebut = code.getToken(positionPremierToken).getlocalisation();
        Localisation localisationFin = code.getToken(positionPremierToken + nombreTokenRedondant - 1).getlocalisation();

        int nombreLigne = localisationFin.getLigne() - localisationDebut.getLigne();

        return nombreLigne > nombreLigneMin;
    }

    public void afficherCodeRedondant(final Code code, FunctionalRedundancy redondance) {
        if (LOG_RESULTAT.isInfoEnabled()) {

            List<String> substitutionList = getSubstitution(code, redondance);
            List<Integer> firstTokenList = redondance.getStartRedundancyList();
            int redundancyNumber = firstTokenList.size();
            LOG_RESULTAT.info("Tokens number:" + redondance.getDuplicatedTokenNumber() + " Duplications number:" + redundancyNumber + " Substitutions number:"
                    + substitutionList.size());

            for (Integer firstTokenPosition : firstTokenList) {
                final int NB_MAX_DISPLAY = 200;
                int fin = firstTokenPosition + Math.min(NB_MAX_DISPLAY, redondance.getDuplicatedTokenNumber());

                StringBuffer buffer = new StringBuffer();

                Token firstToken = code.getToken(firstTokenPosition);
                Integer ligneDebut = firstToken.getLigneDebut();
                Token lastToken = code.getToken(firstTokenPosition + redondance.getDuplicatedTokenNumber() - 1);
                Integer ligneFin = lastToken.getLigneDebut();
                if (LOG_RESULTAT.isTraceEnabled()) {
                    LOG_RESULTAT.trace("First position:" + firstTokenPosition + " start line:" + ligneDebut + " end line:" + ligneFin);
                }
                MethodLocalisation method = MethodLocalisation.findMethod(methodList, lastToken);
                if (method != null) {
                    method.getRedondanceList().add(redondance);
                    int methodLineNumber = method.getLocalisationFin().getLigne() - method.getLocalisationDebut().getLigne();
                    int redundancyLineNumber = ligneFin - ligneDebut;
                    int pourcentage = computePourcentage(redundancyLineNumber, methodLineNumber);

                    buffer.append(pourcentage)
                            .append("% (")
                            .append(redundancyLineNumber)
                            .append(" of ")
                            .append(methodLineNumber)
                            .append(" lines)")
                            .append(method.getMethodName())
                            .append(" from line ")
                            .append(code.getToken(firstTokenPosition).getlocalisation().getLigne())
                            .append(" to ")
                            .append(code.getToken(firstTokenPosition + redondance.getDuplicatedTokenNumber()).getlocalisation().getLigne())
                            .append(" ")

                            .append("(method from line ")
                            .append(method.getLocalisationDebut().getLigne())
                            .append(" to ")
                            .append(method.getLocalisationFin().getLigne())
                            .append(")");

                    // appendString(buffer, method.getLocalisationDebut());
                    // buffer.append(" <-> ");
                    // appendString(buffer, method.getLocalisationFin());

                    displayVisualRedondance(method, ligneDebut, ligneFin);
                } else {
                    buffer.append(" No method ");
                }

                if (LOG_RESULTAT.isDebugEnabled()) {
                    buffer.append(": ");
                    for (int i = firstTokenPosition; i < fin; i++) {
                        buffer.append(code.getToken(i).getTokenValue()).append(" ");
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
            LOG_RESULTAT.info("");
        }
    }

    private void appendString(StringBuffer buffer, Localisation localisation) {
        buffer.append(localisation.getLigne())
                .append(":")
                .append(localisation.getColonne());
    }

    public void afficherMethodeDupliqueAvecSubtitution(final Code code, FunctionalRedundancy redondance) {
        if (redondance.getStartRedundancyList().size() > 0 && isFullMethodDuplicated(code, redondance)) {
            afficherCodeRedondant(code, redondance);
        }
    }

    private boolean isFullMethodDuplicated(final Code code, Redundancy redondance) {
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        for (Integer firstTokenPosition : firstTokenList) {
            Integer ligneDebut = code.getToken(firstTokenPosition).getLigneDebut();
            Token lastToken = code.getToken(firstTokenPosition + redondance.getDuplicatedTokenNumber() - 1);
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

    private int computePourcentage(int value, int total) {
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
                TRACE_TOKEN.error("Le numéro de ligne diminue");
            } else if (currentLine == lastLine && currentColumn < lastColumn) {
                TRACE_TOKEN.error("Le numéro de colonne diminue");
            }
            lastLine = currentLine;
            lastColumn = currentColumn;
        }
    }

    public void display(final Token token) {
        if (TRACE_TOKEN.isInfoEnabled()) {
            TRACE_TOKEN.info(token.format());
        }
    }

    public void display(final Code code) {
        display(code.getTokenList());
    }

    public void display(final List<Token> tokenList) {
        for (Token token : tokenList) {
            display(token);
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

}
