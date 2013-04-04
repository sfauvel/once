package fr.sf.once;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ReportingImpl implements Reporting {

    private ArrayList<MethodLocalisation> methodList;

    public ReportingImpl(ArrayList<MethodLocalisation> methodList) {
        this.methodList = methodList;
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
            List<Integer> firstTokenList = redondance.getFirstTokenList();
            Integer positionPremierToken = firstTokenList.get(0);
            if (isNombreLigneSuperieurA(tokenList, positionPremierToken, redondance.getDuplicatedTokenNumber(), 0)) {

                int redundancyNumber = firstTokenList.size();
                long duplicationScore = redundancyNumber * redondance.getDuplicatedTokenNumber();
                if (redondance.getDuplicatedTokenNumber() > 5 && duplicationScore > tailleMin) {
                    StringBuffer bufferCsv = new StringBuffer();

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
                    LOG_CSV.info(bufferCsv.toString());

                    afficherCodeRedondant(tokenList, redondance);
                }
            }
        }
        // displayMethod(tokenList, listeTokenTrie, listeRedondance);
    }

    private List<String> getSubstitution(final List<Token> tokenList, Redondance redondance) {
        List<String> substitutionList = new ArrayList<String>();
        int duplicatedTokenNumber = redondance.getDuplicatedTokenNumber();
        List<Integer> firstTokenList = redondance.getFirstTokenList();
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
            List<Integer> firstTokenList = redondance.getFirstTokenList();
            int redundancyNumber = firstTokenList.size();
            LOG_RESULTAT.info("Taille:" + redondance.getDuplicatedTokenNumber() + " Longueur:" + redundancyNumber + " Substitutions:" + substitutionList.size());

            for (Integer firstTokenPosition : firstTokenList) {
                final int NB_MAX_DISPLAY = 200;
                int fin = firstTokenPosition + Math.min(NB_MAX_DISPLAY, redondance.getDuplicatedTokenNumber());

                StringBuffer buffer = new StringBuffer();

                Integer ligneDebut = tokenList.get(firstTokenPosition).getLigneDebut();
                Token lastToken = tokenList.get(firstTokenPosition + redondance.getDuplicatedTokenNumber() - 1);
                Integer ligneFin = lastToken.getLigneDebut();
                if (LOG_RESULTAT.isTraceEnabled()) {
                    LOG_RESULTAT.trace("First position:" + firstTokenPosition + " ligne debut:" + ligneDebut + " ligne fin:" + ligneFin);
                }
                MethodLocalisation method = MethodLocalisation.findMethod(methodList, lastToken);
                if (method != null) {
                    method.getRedondanceList().add(redondance);
                    int methodSize = method.getLocalisationFin().getLigne() - method.getLocalisationDebut().getLigne();
                    int pourcentage = computePourcentage(ligneFin - ligneDebut, methodSize);

                    buffer.append(pourcentage)
                            .append("% ")
                            .append(ligneFin - ligneDebut)
                            .append(" lignes sur ")
                            .append(method.getLocalisationFin().getLigne() - method.getLocalisationDebut().getLigne());

                    String nomFichier = method.getLocalisationDebut().getNomFichier();
                    appendFile(buffer, nomFichier, ligneDebut);
                    buffer.append(" <-> ");
                    appendFile(buffer, nomFichier, ligneFin);

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

            for (String substitution : substitutionList) {
                LOG_RESULTAT.info("  " + substitution);
            }
        }
    }

    public void afficherMethodeDupliqueAvecSubtitution(final List<Token> tokenList, Redondance redondance) {
        if (redondance.getFirstTokenList().size() > 0 && isFullMethodDuplicated(tokenList, redondance)) {
            afficherCodeRedondant(tokenList, redondance);
        }
    }

    private boolean isFullMethodDuplicated(final List<Token> tokenList, Redondance redondance) {
        List<Integer> firstTokenList = redondance.getFirstTokenList();
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
            Localisation localisation = token.getlocalisation();

            StringBuffer buffer = new StringBuffer();
            buffer.append(StringUtils.rightPad(token.getValeurToken(), 25));
            appendFile(buffer, localisation.getNomFichier(), localisation.getLigne());
            buffer.append(" col:")
                    // .append(localisation.getColonne())
                    .append(StringUtils.rightPad(Integer.toString(localisation.getColonne()), 5))
                    .append(" type:")
                    .append(token.getType().toString());
            TRACE_TOKEN.info(buffer.toString());
        }
    }

    public void display(final Code code) {
        for (Token token : code.getTokenList()) {
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

    public void appendFile(StringBuffer buffer, String fileName, int line) {
        buffer.append("(")
                .append(fileName)
                .append(":")
                .append(line)
                .append(")");
    }

}
