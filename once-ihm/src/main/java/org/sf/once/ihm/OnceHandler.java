package org.sf.once.ihm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import fr.sf.once.ast.ExtractCode;
import fr.sf.once.comparator.ComparateurAvecSubstitutionEtType;
import fr.sf.once.core.Configuration;
import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.launcher.Launcher.OnceProperties;
import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;

public class OnceHandler implements HttpHandler {

    public static final Logger LOG = Logger.getLogger(OnceHandler.class);

    private void parsePostParameters(HttpExchange exchange)
            throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            @SuppressWarnings("unchecked")
            Map parameters =
                    (Map) exchange.getAttribute("parameters");
            InputStreamReader isr =
                    new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            System.out.println(query);
        }
    }

    @SuppressWarnings("restriction")
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        new ParameterFilter().doFilter(exchange);

        if (requestMethod.equalsIgnoreCase("GET")) {
            Headers responseHeaders = exchange.getResponseHeaders();

            responseHeaders.set("Content-Type", "html");
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            StringBuffer buffer = getResponse(exchange);

            responseBody.write(buffer.toString().getBytes());

            responseBody.close();
        }

        if (requestMethod.equalsIgnoreCase("POST")) {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "html");
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();

            StringBuffer buffer = getResponse(exchange);

            responseBody.write(buffer.toString().getBytes());

            responseBody.close();
        }
    }

    private StringBuffer getResponse(HttpExchange exchange) {
        Map<String, Object> parameters = (Map<String, Object>) exchange.getHttpContext().getAttributes().get("parameters");
        String folder = (String) parameters.get("folder");

        StringBuffer buffer = new StringBuffer();

        buffer.append("<html><body>");
        buffer.append("<h1><a href=\"/once\">Once</a></h1>");
        buffer.append("<form method=\"post\" action=\"once\">");
        buffer.append("<input type=\"text\" name=\"folder\"><br>");
        buffer.append("<input type=\"submit\" value=\"Submit\">");
        buffer.append("</form>");
        try {
            buffer.append("<div>");
            // buffer.append(reponse);
            if (StringUtils.isNotEmpty(folder) && Files.exists(Paths.get(folder))) {
                compute(buffer, folder);
            }
            buffer.append("</div>");
        } catch (IOException e) {
            buffer.append("<div>Error:</div>");
            buffer.append("<div>");
            buffer.append(e.getMessage());
            buffer.append("</div>");
        }

        buffer.append("</body></html>");
        return buffer;
    }

    private Code code = null;
    
    public void compute(StringBuffer buffer, String srcDir) throws IOException {
        String ONCE_PROPERTY = "once.properties";
        // String srcDir = "D:\\Projets\\PQL\\Projets\\COLORIS\\COLORIS-G02R02C02P1\\coloris-web\\src\\main\\java\\com\\ft\\coloris\\web\\action\\profil";
        Properties applicationProperties = new Properties();

        File propertiesFile = new File(ONCE_PROPERTY);

        Properties applicationProps = new Properties();
        if (propertiesFile.exists()) {
            InputStream resourceAsStream = new FileInputStream(propertiesFile);
            applicationProperties.load(resourceAsStream);
            applicationProps = new Properties(applicationProperties);
        }

        LOG.debug("Properties:");
        for (Entry<Object, Object> entry : applicationProps.entrySet()) {
            LOG.debug(entry.getKey() + ":" + entry.getValue());
        }
        String sourceDir = applicationProps.getProperty(OnceProperties.SRC_DIR, srcDir);
        String sourceEncoding = applicationProps.getProperty(OnceProperties.SRC_ENCODING, "iso8859-1");
        boolean isVerbose = Boolean.parseBoolean(applicationProps.getProperty(OnceProperties.VERBOSE, "false"));

        LOG.info("Source directory:" + sourceDir);

        Class<ComparateurAvecSubstitutionEtType> comparator = ComparateurAvecSubstitutionEtType.class;
        int tailleMin = 20;

        code = new ExtractCode().extract(sourceDir, sourceEncoding);

        Configuration configuration = new Configuration(comparator).withMinimalTokenNumber(tailleMin);

        RedundancyFinder manager = new RedundancyFinder(code);
        List<FunctionalRedundancy> listeRedondance = manager.findRedundancies(configuration);

        LOG.info("Affichage des resultats...");

        Collections.sort(listeRedondance, new Comparator<Redundancy>() {
            @Override
            public int compare(Redundancy redondance1, Redundancy redondance2) {
                return redondance2.getDuplicatedTokenNumber() - redondance1.getDuplicatedTokenNumber();
            }
        });

        buffer.append("<div>Redundancy number:" + listeRedondance.size() + "</div>");

        List<Token> tokenList = code.getTokenList();
        for (Redundancy redondance : listeRedondance) {
            List<Integer> firstTokenList = redondance.getStartRedundancyList();
            Integer positionPremierToken = firstTokenList.get(0);
            if (isNombreLigneSuperieurA(tokenList, positionPremierToken, redondance.getDuplicatedTokenNumber(), 0)) {
                long duplicationScore = computeScore(redondance);
                if (redondance.getDuplicatedTokenNumber() > 5 && duplicationScore > tailleMin) {
                    // displayCsvRedundancy(tokenList, redondance, duplicationScore);
                    afficherCodeRedondant(buffer, code, redondance);
                }
            }
        }

        // displayCode(buffer, tokenList);
        displayRedondancy(buffer, code, listeRedondance.get(0));
    }

    // private void displayCsvRedundancy(final List<Token> tokenList, Redundancy redondance, long duplicationScore) {
    // if (LOG_CSV.isInfoEnabled()) {
    // StringBuffer bufferCsv = new StringBuffer();
    // appendCsvInformation(bufferCsv, tokenList, redondance, duplicationScore);
    // LOG_CSV.info(bufferCsv.toString());
    // }
    // }
    //
    // private void appendCsvInformation(StringBuffer bufferCsv, final List<Token> tokenList, Redundancy redondance, long duplicationScore) {
    // List<Integer> firstTokenList = redondance.getStartRedundancyList();
    // int redundancyNumber = redondance.getRedundancyNumber();
    //
    // bufferCsv.append(redondance.getDuplicatedTokenNumber())
    // .append(";")
    // .append(redundancyNumber)
    // .append(";")
    // .append(duplicationScore)
    // .append(";");
    // for (Integer firstTokenPosition : firstTokenList) {
    // Localisation localisationDebut = tokenList.get(firstTokenPosition).getlocalisation();
    // Localisation localisationFin = tokenList.get(firstTokenPosition + redondance.getDuplicatedTokenNumber()).getlocalisation();
    //
    // bufferCsv.append(localisationDebut.getNomFichier())
    // .append("(")
    // .append(localisationDebut.getLigne())
    // .append("/")
    // .append(localisationFin.getLigne())
    // .append(") ");
    // }
    // }

    private void displayCode(StringBuffer buffer, List<Token> tokenList) {
        int line = 0;
        buffer.append("<pre>");
        for (Token token : tokenList) {
            if (line < token.getLigneDebut()) {
                buffer.append("\n");
            }

            buffer.append(token.getValeurToken());
            buffer.append(" ");

            line = token.getLigneDebut();
        }
        buffer.append("</pre>");
    }

    private void displayRedondancy(StringBuffer buffer, final Code code, Redundancy redundancy) {
        int line = 0;

        List<Token> tokenList = code.getTokenList();
        List<MethodLocalisation> methodList = code.getMethodList();
        Integer firstToken = redundancy.getStartRedundancyList().get(0);
        int lastTokenPosition = firstToken + redundancy.getDuplicatedTokenNumber() - 1;
        Token lastToken = tokenList.get(lastTokenPosition);
        Integer ligneFin = lastToken.getLigneDebut();
        MethodLocalisation method = MethodLocalisation.findMethod(methodList, lastToken);

        buffer.append("<pre>");

        int i = 0;
        for (Token token : tokenList) {
            if (token.getLigneDebut() >= method.getLocalisationDebut().getLigne()-1
                    && token.getLigneDebut() <= method.getLocalisationFin().getLigne()) {
                if (line < token.getLigneDebut()) {
                    buffer.append("\n");
                    for (int j = 0; j < token.getColonneDebut(); j++) {
                        buffer.append(" ");
                    }
                }

                if (i==firstToken) {
                    buffer.append("<span style=\"background-color:AAA\">");
                }
                buffer.append(token.getValeurToken());
                buffer.append(" ");

                if (i==lastTokenPosition) {
                    buffer.append("</span>");
                }
            }
            line = token.getLigneDebut();
            i++;
        }
        buffer.append("</pre>");
    }

    public void afficherCodeRedondant(StringBuffer buffer, final Code code, Redundancy redondance) {
        List<Token> tokenList = code.getTokenList();
        List<MethodLocalisation> methodList = code.getMethodList();

        List<String> substitutionList = getSubstitution(tokenList, redondance);
        List<Integer> firstTokenList = redondance.getStartRedundancyList();
        int redundancyNumber = firstTokenList.size();
        buffer.append("<div>")
                .append("Tokens number:" + redondance.getDuplicatedTokenNumber() + " Duplications number:" + redundancyNumber + " Substitutions number:"
                        + substitutionList.size())
                .append("</div>");

        for (Integer firstTokenPosition : firstTokenList) {
            final int NB_MAX_DISPLAY = 200;
            int fin = firstTokenPosition + Math.min(NB_MAX_DISPLAY, redondance.getDuplicatedTokenNumber());

            // StringBuffer buffer = new StringBuffer();

            Token firstToken = tokenList.get(firstTokenPosition);
            Integer ligneDebut = firstToken.getLigneDebut();
            Token lastToken = tokenList.get(firstTokenPosition + redondance.getDuplicatedTokenNumber() - 1);
            Integer ligneFin = lastToken.getLigneDebut();
            // if (LOG_RESULTAT.isTraceEnabled()) {
            // LOG_RESULTAT.trace("First position:" + firstTokenPosition + " start line:" + ligneDebut + " end line:" + ligneFin);
            // }
            MethodLocalisation method = MethodLocalisation.findMethod(methodList, lastToken);
            if (method != null) {
                method.getRedondanceList().add(redondance);
                int methodLineNumber = method.getLocalisationFin().getLigne() - method.getLocalisationDebut().getLigne();
                int redundancyLineNumber = ligneFin - ligneDebut;
                int pourcentage = computePourcentage(redundancyLineNumber, methodLineNumber);
                buffer.append("<div style=\"padding-left:3em;\">");
                buffer.append(pourcentage)
                        .append("% (")
                        .append(redundancyLineNumber)
                        .append(" of ")
                        .append(methodLineNumber)
                        .append(" lines)")
                        .append(method.getMethodName())
                        .append(" from line ")
                        .append(tokenList.get(firstTokenPosition).getlocalisation().getLigne())
                        .append(" to ")
                        .append(tokenList.get(firstTokenPosition + redondance.getDuplicatedTokenNumber()).getlocalisation().getLigne())
                        .append(" ")

                .append("(method from line ")
                        .append(method.getLocalisationDebut().getLigne())
                        .append(" to ")
                        .append(method.getLocalisationFin().getLigne())
                        .append(")");

                // appendString(buffer, method.getLocalisationDebut());
                // buffer.append(" <-> ");
                // appendString(buffer, method.getLocalisationFin());

                // displayVisualRedondance(method, ligneDebut, ligneFin);
                buffer.append("</div>");
            } else {
                buffer.append(" No method ");
            }

            // if (LOG_RESULTAT.isDebugEnabled()) {
            // buffer.append(": ");
            // for (int i = firstTokenPosition; i < fin; i++) {
            // buffer.append(code.get(i).getValeurToken()).append(" ");
            // }
            // if (redondance.getDuplicatedTokenNumber() >= NB_MAX_DISPLAY) {
            // buffer.append("...");
            // }
            // }
            // LOG_RESULTAT.info(" " + buffer.toString());

        }
        // Collections.sort(substitutionList);
        // for (String substitution : substitutionList) {
        // buffer.append("<div>/ " + substitution + "</div>");
        // }
        // LOG_RESULTAT.info("");
    }

    private int computePourcentage(int value, int total) {
        if (total == 0) {
            return 0;
        } else {
            return value * 100 / total;
        }

    }

    private List<String> getSubstitution(final List<Token> tokenList, Redundancy redondance) {
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
                            .append(" values: ");

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

    private int computeScore(Redundancy redondance) {
        int redundancyNumber = redondance.getRedundancyNumber();
        return redundancyNumber * redondance.getDuplicatedTokenNumber();
    }

    private Map<String, List<String>> getAllRequestHeaderValues(Headers requestHeaders) {
        Set<String> keySet = requestHeaders.keySet();
        Iterator<String> iter = keySet.iterator();

        Map<String, List<String>> result = new HashMap<String, List<String>>();
        while (iter.hasNext()) {
            String key = iter.next();
            List<String> values = requestHeaders.get(key);
            result.put(key, values);
        }
        return result;
    }

    public Code getCode() {
                return code;
            }

}

class ParameterFilter {

    public void doFilter(HttpExchange exchange)
            throws IOException {
        parseGetParameters(exchange);
        parsePostParameters(exchange);
    }

    private void parseGetParameters(HttpExchange exchange)
            throws UnsupportedEncodingException {

        Map parameters = new HashMap();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        exchange.setAttribute("parameters", parameters);
    }

    private void parsePostParameters(HttpExchange exchange)
            throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            @SuppressWarnings("unchecked")
            Map parameters =
                    (Map) exchange.getAttribute("parameters");
            InputStreamReader isr =
                    new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseQuery(String query, Map parameters)
            throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");

            for (String pair : pairs) {
                String param[] = pair.split("[=]");

                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List) {
                        List values = (List) obj;
                        values.add(value);
                    } else if (obj instanceof String) {
                        List values = new ArrayList();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}