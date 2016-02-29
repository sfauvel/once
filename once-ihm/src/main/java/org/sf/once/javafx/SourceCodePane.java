package org.sf.once.javafx;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.sf.once.model.Token;
import fr.sf.once.model.Type;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SourceCodePane extends VBox {

    private static final String CSS_STYLESHEET = "css/once.css";
    private TextFlow textFlow = new TextFlow();

    public SourceCodePane() {

        getStylesheets().add(CSS_STYLESHEET);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(textFlow);

        getChildren().add(scrollPane);

    }

    private int lastSelect = 1;
    public void displayCode(List<Token> tokenList, Integer firstTokenPosition, List<Integer> startRedundancyList, int tokenNumber) {
        
        int line = 0;
        int tokenIndex = 0;
        int redundancyIndex = 0;
        Integer nextRedundancyFirstToken = -tokenNumber;
        for (Token token : tokenList) {
            tokenIndex++;
            if (tokenIndex > nextRedundancyFirstToken + tokenNumber) {
                nextRedundancyFirstToken = getNextRedundancyStart(tokenList, startRedundancyList, redundancyIndex, nextRedundancyFirstToken);
                redundancyIndex++;
                lastSelect = (lastSelect+1)%2;
            }
            addNewLineIfNecessary(line, token);

            Node text = tokenToText(token);
            addSelectionStyle(text, firstTokenPosition, nextRedundancyFirstToken, nextRedundancyFirstToken + tokenNumber, tokenIndex);
            textFlow.getChildren().add(text);
            line = token.getLigneDebut();
        }

    }

    private Integer getNextRedundancyStart(List<Token> tokenList, List<Integer> startRedundancyList, int redundancyIndex, Integer nextRedundancyFirstToken) {
        if (redundancyIndex >= startRedundancyList.size()) {
            nextRedundancyFirstToken = tokenList.size();
        } else {
            System.out.println(nextRedundancyFirstToken + " " + redundancyIndex + "/" + startRedundancyList.size());
            nextRedundancyFirstToken = startRedundancyList.get(redundancyIndex);
        }
        return nextRedundancyFirstToken;
    }

    private void addSelectionStyle(Node text, int selectedRedundancyStart, int start, int end, int index) {
        boolean selectedToken = start <= index && index <= end;
        if (selectedToken) {
            if (selectedRedundancyStart == start) {
                text.getStyleClass().add("selected" + lastSelect);
            }
            else {
                text.getStyleClass().add("selectedHighlight");                
            }
        } 
    }

    private Type lastType = Type.NON_SIGNIFICATIF;
    private Node tokenToText(Token token) {
        System.out.println(token.getValeurToken() +" => " + token.getType());
        String code = "";
        if (isText(lastType) && isText(token.getType())) {
            code += " ";
        }
        code += token.getValeurToken();
        
        Label text = new Label(code);
        lastType = token.getType();
        text.getStyleClass().add("code");
        text.getStyleClass().add(getStye(token));
        return text;
    }

    private boolean isText(Type type) {
        return type == Type.KEYWORD || type == Type.VALEUR;
    }

    private String getStye(Token token) {
        if (Type.KEYWORD == token.getType()) {
            return "keyword";
        } else if (token.getValeurToken().startsWith("\"") && token.getValeurToken().endsWith("\"")) {
            return "string";
        } else {
            return "no-style";
        }
    }

    private void addNewLineIfNecessary(int line, Token token) {
        if (line < token.getLigneDebut()) {
            String newLine = StringUtils.rightPad("\n ", token.getColonneDebut());
            textFlow.getChildren().add(new Text(newLine));
              
        } 
    }

}
