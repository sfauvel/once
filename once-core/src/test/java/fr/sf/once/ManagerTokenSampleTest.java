package fr.sf.once;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.ClassRule;
import org.junit.Test;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.model.Code;
import fr.sf.once.model.Localisation;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import fr.sf.once.test.LogRule;
import fr.sf.once.test.UtilsToken;

public class ManagerTokenSampleTest {

    // Méthode de ReportingImpl
    // public void display(final Token token) {
    // if (TRACE_TOKEN.isInfoEnabled()) {
    // Localisation localisation = token.getlocalisation();
    // TRACE_TOKEN.info("tokenList.add(new Token(new Localisation(\"" +
    // localisation.getNomFichier() + "\", " + localisation.getLigne() + ", " +
    // localisation.getColonne() + "), \"" + token.getValeurToken() +
    // "\", Type." + token.getType().toString() + "));");
    // }
    // }

//    private static Logger LOG = Logger.getLogger(ManagerTokenSampleTest.class);

    @ClassRule
    public static final LogRule LOG_RULE = new LogRule();

    /**
     * Exemple sur un cas présentant une anomalie. Il y a 4 tokens "(", ils
     * devraient être à suivre car il s'agit de caractères non substituable
     */
    @Test
    public void testTrierListeTokenSansModifierListeOrigine() {

//        LOG.addAppender(new ConsoleAppender(new SimpleLayout()));
//        LOG.setLevel(Level.DEBUG);
//        LOG.setAdditivity(false);

//        List<Token> listeTokenOrigine = createTokenList();
        Code code = createCode();
        RedundancyFinder manager = new RedundancyFinder(code);

        CodeComparator comparator = new ComparatorWithSubstitution(code);

        List<Integer> positionList = UtilsToken.createPositionList(code.getSize());

        manager.sortPositionList(positionList, comparator);

        int nombreParentheseOuvrante = 0;
        int premiereParenthese = -1;

        int index = 0;
        for (Integer tokenPosition : positionList) {
            Token token = code.getToken(tokenPosition);
//            LOG.debug(tokenPosition + ":\t" + token.getValeurToken());

            if ("(".equals(token.getValeurToken())) {
                nombreParentheseOuvrante++;
                if (premiereParenthese == -1) {
                    premiereParenthese = index;
                }
            }
            index++;
        }

        assertEquals(4, nombreParentheseOuvrante);
        for (int i = premiereParenthese; i < premiereParenthese + nombreParentheseOuvrante; i++) {
            assertEquals("Le token n°" + i + " n'est pas '(':", "(", code.getToken(positionList.get(i)).getValeurToken());

        }

    }

    private Code createCode() {
        return new Code(Arrays.asList(
                createToken(27, 1, "class", Type.VALEUR),
                createToken(27, 6, "GestionGroupe", Type.VALEUR),
                createToken(29, 8, "{", Type.NON_SIGNIFICATIF),
                createToken(29, 23, "Vector", Type.VALEUR),
                createToken(29, 29, "lireListeGroupeOrderByCode", Type.VALEUR),
                createToken(29, 56, "(", Type.NON_SIGNIFICATIF),
                createToken(29, 57, ")", Type.NON_SIGNIFICATIF),
                createToken(30, 48, "[METHOD LIMIT]", Type.BREAK),
                createToken(30, 48, "{", Type.NON_SIGNIFICATIF),
                createToken(31, 17, "Vector", Type.VALEUR),
                createToken(31, 24, "listeGroupe", Type.VALEUR),
                createToken(31, 35, "=", Type.NON_SIGNIFICATIF),
                createToken(31, 38, "new", Type.VALEUR),
                createToken(31, 42, "Vector", Type.VALEUR),
                createToken(31, 48, "(", Type.NON_SIGNIFICATIF),
                createToken(31, 49, ")", Type.NON_SIGNIFICATIF),
                createToken(31, 50, ";", Type.NON_SIGNIFICATIF),
                createToken(41, 9, "}", Type.NON_SIGNIFICATIF),
                createToken(41, 10, "[METHOD LIMIT]", Type.BREAK),
                createToken(43, 23, "Vector", Type.VALEUR),
                createToken(43, 29, "lireListeGroupeActiveOrderByCode", Type.VALEUR),
                createToken(43, 62, "(", Type.NON_SIGNIFICATIF),
                createToken(43, 63, ")", Type.NON_SIGNIFICATIF),
                createToken(44, 48, "[METHOD LIMIT]", Type.BREAK),
                createToken(44, 48, "{", Type.NON_SIGNIFICATIF),
                createToken(45, 17, "Vector", Type.VALEUR),
                createToken(45, 24, "listeGroupe", Type.VALEUR),
                createToken(45, 35, "=", Type.NON_SIGNIFICATIF),
                createToken(45, 38, "new", Type.VALEUR),
                createToken(45, 42, "Vector", Type.VALEUR),
                createToken(45, 48, "(", Type.NON_SIGNIFICATIF),
                createToken(45, 49, ")", Type.NON_SIGNIFICATIF),
                createToken(45, 50, ";", Type.NON_SIGNIFICATIF),
                createToken(55, 9, "}", Type.NON_SIGNIFICATIF),
                createToken(55, 10, "[METHOD LIMIT]", Type.BREAK),
                createToken(57, 1, "}", Type.NON_SIGNIFICATIF))
                );
    }

    private Token createToken(int line, int column, String tokenValue, Type type) {
        return new Token(new Localisation("", line, column), tokenValue, type);
    }

    public List<Token> createFullTokenList() {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 1), "package",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 9), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 11), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 12), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 15), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 16), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 22), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 23), "bp",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 1, 25), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 9, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 9, 8), "java",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 9, 12), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 9, 13), "sql",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 9, 16), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 9, 17), "Connection",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 9, 27), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 10, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 10, 8), "java",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 10, 12), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 10, 13), "sql",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 10, 16), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 10, 17),
        // "PreparedStatement", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 10, 34), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 11, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 11, 8), "java",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 11, 12), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 11, 13), "sql",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 11, 16), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 11, 17), "ResultSet",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 11, 26), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 12, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 12, 8), "java",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 12, 12), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 12, 13), "sql",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 12, 16), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 12, 17), "SQLException",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 12, 29), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 13, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 13, 8), "java",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 13, 12), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 13, 13), "sql",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 13, 16), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 13, 17), "Statement",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 13, 26), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 14, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 14, 8), "java",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 14, 12), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 14, 13), "util",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 14, 17), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 14, 18), "Hashtable",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 14, 27), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 15, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 15, 8), "java",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 15, 12), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 15, 13), "util",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 15, 17), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 15, 18), "Vector",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 15, 24), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 22), "bo",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 24), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 25),
        // "CoupleADVDISE", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 17, 38), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 22), "bo",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 24), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 25), "DateRegard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 18, 35), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 22), "bo",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 24), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 25), "Groupe",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 19, 31), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 22), "bo",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 24), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 25), "GroupeAdv",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 20, 34), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 22), "bo",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 24), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 25),
        // "GroupeCorresp", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 21, 38), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 22), "bo",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 24), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 25), "ParamDivers",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 22, 36), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 22), "exception",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 31), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 32),
        // "CodeRetourTechniqueException", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 23, 60), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 22), "exception",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 31), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 32),
        // "ExceptionTechnique", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 24, 50), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 1), "import",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 8), "fr",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 10), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 11), "ftm",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 14), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 15), "regard",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 21), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 22), "exception",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 31), ".",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 32),
        // "RegardException", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 25, 47), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 27, 1), "public",
        // Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 27, 1), "class", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 27, 6), "GestionGroupe", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 27, 28), "extends",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 27, 36), "ContexteDAO",
        // Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 29, 8), "{", Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 29, 9), "public static",
        // Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 29, 23), "Vector", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 29, 29), "lireListeGroupeOrderByCode", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 29, 56), "(", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 29, 57), ")", Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 30, 25), "throws",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 30, 32),
        // "RegardException", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 30, 48), "[METHOD LIMIT]", Type.BREAK));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 30, 48), "{", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 17), "Vector", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 24), "listeGroupe", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 35), "=", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 38), "new", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 42), "Vector", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 48), "(", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 49), ")", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 31, 50), ";", Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 34, 17), "Connection",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 34, 28), "conn",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 34, 32), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 34, 35), "null",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 34, 39), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 35, 17), "Statement",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 35, 27), "stmt",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 35, 31), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 35, 34), "null",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 35, 38), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 36, 17), "ResultSet",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 36, 27), "rs",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 36, 29), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 36, 32), "null",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 36, 36), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 37, 17), "String",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 37, 24),
        // "requete_groupe", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 37, 38), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 37, 41),
        // "\"SELECT CODE, DESCRIPTION, OBLIGATOIRE, DOUBLON, ARBRE, MODIFIABLE, SAISIE, CONDITION1, CONDITION2, ORDRE, SUPPRIME \"",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 37, 158), "+",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 38, 35),
        // "\"FROM GROUPE  order by decode(supprime, -1, 1,0),ordre\"",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 38, 90), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 40, 17), "return",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 40, 24), "listeGroupe",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 40, 35), ";",
        // Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 41, 9), "}", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 41, 10), "[METHOD LIMIT]", Type.BREAK));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 43, 9), "public static",
        // Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 43, 23), "Vector", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 43, 29), "lireListeGroupeActiveOrderByCode", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 43, 62), "(", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 43, 63), ")", Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 44, 25), "throws",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 44, 32),
        // "RegardException", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 44, 48), "[METHOD LIMIT]", Type.BREAK));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 44, 48), "{", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 17), "Vector", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 24), "listeGroupe", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 35), "=", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 38), "new", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 42), "Vector", Type.VALEUR));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 48), "(", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 49), ")", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 45, 50), ";", Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 48, 17), "Connection",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 48, 28), "conn",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 48, 32), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 48, 35), "null",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 48, 39), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 49, 17), "Statement",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 49, 27), "stmt",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 49, 31), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 49, 34), "null",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 49, 38), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 50, 17), "ResultSet",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 50, 27), "rs",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 50, 29), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 50, 32), "null",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 50, 36), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 51, 17), "String",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 51, 24),
        // "requete_groupe", Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 51, 38), "=",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 51, 41),
        // "\"SELECT CODE, DESCRIPTION, OBLIGATOIRE, DOUBLON, ARBRE, MODIFIABLE, SAISIE, CONDITION1, CONDITION2, ORDRE, SUPPRIME \"",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 51, 158), "+",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 52, 35),
        // "\"FROM GROUPE where supprime != -1 order by decode(supprime, -1, 1,0),ordre\"",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 52, 110), ";",
        // Type.NON_SIGNIFICATIF));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 54, 17), "return",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 54, 24), "listeGroupe",
        // Type.VALEUR));
        // tokenList.add(new Token(new
        // Localisation("GestionGroupeUltraLight.java", 54, 35), ";",
        // Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 55, 9), "}", Type.NON_SIGNIFICATIF));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 55, 10), "[METHOD LIMIT]", Type.BREAK));
        tokenList.add(new Token(new Localisation("GestionGroupeUltraLight.java", 57, 1), "}", Type.NON_SIGNIFICATIF));
        return tokenList;
    }
}
