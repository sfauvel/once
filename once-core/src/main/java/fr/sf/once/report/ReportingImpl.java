package fr.sf.once.report;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.sf.once.model.Code;
import fr.sf.once.model.Location;
import fr.sf.once.model.MethodLocation;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public class ReportingImpl implements Reporting {

    private Logger tokenLogger;

    public ReportingImpl() {
        this.tokenLogger = TRACE_TOKEN;
    }

    public void displayRedundancy(final Code code, final int minimalSize, List<Redundancy> redundancyList) {
         LOG_CSV.info("Redundancy size;Redundancy number;Note");

         redundancyList.stream()
             .sorted( new ComparatorRedundancyByTokenNumber())
//             .sorted( new ComparatorRedundancySubstitution())
             .forEach(redundancy ->  displayRedundancy(code, minimalSize, redundancy));
         
    }

    private void displayRedundancy(final Code code, final int minimalSize, Redundancy redudancy) {
        Collection<Integer> firstTokenList = redudancy.getStartRedundancyList();
        Integer firstTokenPosition = firstTokenList.iterator().next();
        if (isLineNumberGreaterThan(code, firstTokenPosition, redudancy.getDuplicatedTokenNumber(), 0)) {
            long score = computeScore(redudancy);
            if (redudancy.getDuplicatedTokenNumber() > 5 && score > minimalSize) {
                displayCsvRedundancy(code, redudancy, score);
                displayRedundantCode(code, redudancy);
            }
        }
    }

    private void displayCsvRedundancy(final Code code, Redundancy redundancy, long score) {
        if (LOG_CSV.isInfoEnabled()) {
            StringBuffer bufferCsv = new StringBuffer();
            appendCsvInformation(bufferCsv, code, redundancy, score);
            LOG_CSV.info(bufferCsv.toString());
        }
    }

    private void appendCsvInformation(StringBuffer bufferCsv, final Code code, Redundancy redundancy, long score) {
        Collection<Integer> firstTokenList = redundancy.getStartRedundancyList();
        int redundancyNumber = redundancy.getRedundancyNumber();

        bufferCsv.append(String.format("%s;%s;%s;", 
                redundancy.getDuplicatedTokenNumber(),
                redundancyNumber,
                score));
        
        for (Integer firstTokenPosition : firstTokenList) {
            Location startingLocation = code.getToken(firstTokenPosition).getLocation();
            Location endingLocation = code.getToken(firstTokenPosition + redundancy.getDuplicatedTokenNumber()).getLocation();

            bufferCsv.append(String.format("%s(%s/%s);", 
                    startingLocation.getFileName(),
                    startingLocation.getLine(),
                    endingLocation.getLine()));
        }
    }

    private int computeScore(Redundancy redondance) {
        int redundancyNumber = redondance.getRedundancyNumber();
        return redundancyNumber * redondance.getDuplicatedTokenNumber();
    }

    private List<String> getSubstitution(Redundancy redundancy) {
        return redundancy.getSubstitutionList().stream()
            .filter(substitution -> substitution.size() > 1)
            .map(substitution -> String.format("  %d values: %s", substitution.size(),  StringUtils.join(substitution, ", ")))
            .collect(Collectors.toList());
    }

    private boolean isLineNumberGreaterThan(Code code, Integer firstTokenPosition, int redundantTokenNumber, int minimalLineNumber) {
        Location startingLocation = code.getToken(firstTokenPosition).getLocation();
        Location edingLocation = code.getToken(firstTokenPosition + redundantTokenNumber - 1).getLocation();

        int lineNumber = edingLocation.getLine() - startingLocation.getLine();

        return lineNumber > minimalLineNumber;
    }

    public void displayRedundantCode(final Code code, Redundancy redundancy) {
        if (LOG_RESULT.isInfoEnabled()) {

            List<String> substitutionList = getSubstitution(redundancy);
            Collection<Integer> firstTokenList = redundancy.getStartRedundancyList();
            LOG_RESULT.info("Tokens number:" + redundancy.getDuplicatedTokenNumber() 
                + " Duplications number:" + firstTokenList.size() 
                + " Substitutions number:" + substitutionList.size());

            for (Integer firstTokenPosition : firstTokenList) {

                StringBuffer buffer = new StringBuffer();

                Token firstToken = code.getToken(firstTokenPosition);
                Integer startingLine = firstToken.getStartingLine();
                Token lastToken = code.getToken(firstTokenPosition + redundancy.getDuplicatedTokenNumber() - 1);
                Integer endingLine = lastToken.getStartingLine();
                if (LOG_RESULT.isTraceEnabled()) {
                    LOG_RESULT.trace("First position:" + firstTokenPosition + " start line:" + startingLine + " end line:" + endingLine);
                }
                appendOneRedundancyDescription(code, redundancy, firstTokenPosition, buffer, startingLine, endingLine);

                appendTokens(buffer, code, redundancy, firstTokenPosition);
                LOG_RESULT.info("  " + buffer.toString());

            }
            Collections.sort(substitutionList);
            for (String substitution : substitutionList) {
                LOG_RESULT.info("  " + substitution);
            }
            LOG_RESULT.info("");
        }
    }

    private void appendOneRedundancyDescription(final Code code, Redundancy redundancy, Integer firstTokenPosition, StringBuffer buffer, Integer startingLine,
            Integer endingLine) {
        MethodLocation method = code.getMethodAtTokenPosition(firstTokenPosition);
        if (method != null) {
            method.getRedundancyList().add(redundancy);
            int methodLineNumber = method.getEndingLocation().getLine() - method.getStartingLocation().getLine();
            int redundancyLineNumber = endingLine - startingLine;
            int pourcentage = computePercentage(redundancyLineNumber, methodLineNumber);

            buffer.append(pourcentage)
                    .append("% (")
                    .append(redundancyLineNumber)
                    .append(" of ")
                    .append(methodLineNumber)
                    .append(" lines)")
                    .append(method.getMethodName())
                    .append(" from line ")
                    .append(code.getToken(firstTokenPosition).getLocation().getLine())
                    .append(" to ")
                    .append(code.getToken(firstTokenPosition + redundancy.getDuplicatedTokenNumber()).getLocation().getLine())
                    .append(" ")

                    .append("(method from line ")
                    .append(method.getStartingLocation().getLine())
                    .append(" to ")
                    .append(method.getEndingLocation().getLine())
                    .append(")");

            displayVisualRedundancy(method, startingLine, endingLine);
        } else {
            buffer.append(" No method ");
        }
    }

    private void appendTokens(StringBuffer buffer, final Code code, Redundancy redundancy, Integer firstTokenPosition) {
     
        if (LOG_RESULT.isDebugEnabled()) {
            final int NB_MAX_DISPLAY = 200;
            int fin = firstTokenPosition + Math.min(NB_MAX_DISPLAY, redundancy.getDuplicatedTokenNumber());

            buffer.append(": ");
            for (int i = firstTokenPosition; i < fin; i++) {
                buffer.append(code.getToken(i).getTokenValue()).append(" ");
            }
            if (redundancy.getDuplicatedTokenNumber() >= NB_MAX_DISPLAY) {
                buffer.append("...");
            }
        }
    }

    public void displayDuplicatedMethodWithSubstitution(final Code code, Redundancy redundancy) {
        if (redundancy.getStartRedundancyList().size() > 0 && isFullMethodDuplicated(code, redundancy)) {
            displayRedundantCode(code, redundancy);
        }
    }

    private boolean isFullMethodDuplicated(final Code code, Redundancy redundancy) {
        Collection<Integer> firstTokenList = redundancy.getStartRedundancyList();
        for (Integer firstTokenPosition : firstTokenList) {
            Integer startingLine = code.getToken(firstTokenPosition).getStartingLine();
            int lastTokenPosition = firstTokenPosition + redundancy.getDuplicatedTokenNumber() - 1;
            Token lastToken = code.getToken(lastTokenPosition);
            Integer endingLine = lastToken.getStartingLine();
            MethodLocation method = code.getMethodAtTokenPosition(lastTokenPosition);
            if (method != null) {
                method.getRedundancyList().add(redundancy);
                int methodSize = method.getEndingLocation().getLine() - method.getStartingLocation().getLine();
                if (endingLine - startingLine == methodSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private int computePercentage(int value, int total) {
        if (total == 0) {
            return 0;
        } else {
            return value * 100 / total;
        }

    }

    private void displayVisualRedundancy(MethodLocation method, Integer stratingLine, Integer endingLine) {
        if (LOG_RESULT.isDebugEnabled()) {
            StringBuffer line = new StringBuffer();
            for (int i = method.getStartingLocation().getLine(); i < stratingLine; i++) {
                line.append(".");
            }
            for (int i = stratingLine; i <= endingLine; i++) {
                line.append("*");
            }
            for (int i = endingLine; i <= method.getEndingLocation().getLine(); i++) {
                line.append(".");
            }

            LOG_RESULT.debug(method.getMethodName() + "(" + method.getStartingLocation().getLine() + ")" + line.toString());
        }
    }

    @Override
    public void displayTokenList(final List<Token> tokenList) {
        int lastLine = 0;
        int lastColumn = 0;
        for (Token token : tokenList) {
            Location location = token.getLocation();

            display(token);

            int currentLine = location.getLine();
            int currentColumn = location.getColumn();
            if (currentLine < lastLine) {
                TRACE_TOKEN.error("Line number decrease");
            } else if (currentLine == lastLine && currentColumn < lastColumn) {
                TRACE_TOKEN.error("Column number decrease");
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
            Location location = token.getLocation();

            int currentLine = location.getLine();
            int currentColumn = location.getColumn();
            if (currentLine < lastLine) {
                LOG_RESULT.error("Line number decrease");
            } else if (currentLine == lastLine && currentColumn < lastColumn) {
                LOG_RESULT.error("Column number decrease");
            }
            lastLine = currentLine;
            lastColumn = currentColumn;
        }
    }

    public Logger getTokenLogger() {
        return tokenLogger;
    }

}
