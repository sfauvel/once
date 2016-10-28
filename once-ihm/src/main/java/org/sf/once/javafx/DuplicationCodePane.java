package org.sf.once.javafx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fr.sf.once.model.MethodLocation;
import fr.sf.once.model.Redundancy;
import fr.sf.once.model.Token;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DuplicationCodePane extends VBox {

    private static final String CSS_STYLESHEET = "css/once.css";
    private static final EventHandler<MouseEvent> NO_EVENT = null;
    private TextFlow textFlow = new TextFlow();

    private boolean isDetailDisplay = false;
    
    public DuplicationCodePane() {
        
        getStylesheets().add(CSS_STYLESHEET);

        textFlow.setLayoutX(40);
        textFlow.setLayoutY(40);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(textFlow);

        getChildren().add(scrollPane);
    }

    public void displayCode(List<Redundancy> redundancyList, int start, int end) {
        for (Redundancy redundancy : redundancyList) {
            int duplication = redundancy.getDuplicatedTokenNumber();            
            Text text = new Text(duplication + " ");
            textFlow.getChildren().add(text);
        }
    }

    public void display(final List<Token> tokenList, List<MethodLocation> methodList, List<? extends Redundancy> redundancies) {
        for (Redundancy redundancy : redundancies) {
            display(tokenList, methodList, redundancy);
        }
    }
    
    public void display(final List<Token> tokenList, List<MethodLocation> methodList, Redundancy redundancy) {

            List<String> substitutionList = getSubstitution(tokenList, redundancy);           
            
            addTextLine("Tokens number:" + redundancy.getDuplicatedTokenNumber() + " Duplications number:" + redundancy.getRedundancyNumber() + " Substitutions number:" + substitutionList.size());        
            
            for (Integer firstTokenPosition : redundancy.getStartRedundancyList()) {
                String redundancyDescription = getRedundancyDescription(tokenList, methodList, redundancy, firstTokenPosition);
                addTextLine("  " + redundancyDescription, openCodeEvent(tokenList, redundancy, firstTokenPosition));              
            }
           
            Collections.sort(substitutionList);
            for (String substitution : substitutionList) {
                addTextLine("  " + substitution);
            }
            addTextLine("");
    }

    private String getRedundancyDescription(final List<Token> tokenList, List<MethodLocation> methodList, Redundancy redundancy, Integer firstTokenPosition) {
        Integer firstLine = tokenList.get(firstTokenPosition).getStartingLine();
        Token lastToken = tokenList.get(firstTokenPosition + redundancy.getDuplicatedTokenNumber() - 1);
        Integer lastLine = lastToken.getStartingLine();
         
        MethodLocation method = MethodLocation.findMethod(methodList, lastToken);
        if (method != null) {
            method.getRedundancyList().add(redundancy);
            StringBuffer buffer = new StringBuffer();
            addRedundancyDescription(tokenList, redundancy, firstTokenPosition, buffer, firstLine, lastLine, method);                                       
            displayVisualRedondance(method, firstLine, lastLine);
            return buffer.toString();
        } else {
            return " No method ";
        }
    }

    private EventHandler<MouseEvent> openCodeEvent(final List<Token> tokenList, Redundancy redundancy, Integer firstTokenPosition) {
        return new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent event) {
                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                VBox dialogVbox = new VBox(20);
                SourceCodePane sourceCodePane = new SourceCodePane();
                List<Integer> startRedundancyList = redundancy.getStartRedundancyList();
                sourceCodePane.displayCode(tokenList, firstTokenPosition, startRedundancyList, redundancy.getDuplicatedTokenNumber());
                dialogVbox.getChildren().add(sourceCodePane);
                Scene dialogScene = new Scene(dialogVbox, 500, 400);
                dialog.setScene(dialogScene);

                dialog.setHeight(500);
                dialog.setWidth(1000);
                dialog.show();
            }
        };
    }

    private void addRedundancyDescription(final List<Token> tokenList, Redundancy redundancy, Integer firstTokenPosition, StringBuffer buffer,
            Integer ligneDebut, Integer ligneFin, MethodLocation method) {
        int methodLineNumber = method.getEndingLocation().getLine() - method.getStartingLocation().getLine();
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
                .append(tokenList.get(firstTokenPosition).getLocation().getLine())
                .append(" to ")
                .append(tokenList.get(firstTokenPosition+redundancy.getDuplicatedTokenNumber()).getLocation().getLine())
                .append(" ")
                
                .append("(method from line ")
                .append(method.getStartingLocation().getLine())
                .append(" to ")
                .append(method.getEndingLocation().getLine())
                .append(")");
    }

    private void displayVisualRedondance(MethodLocation method, Integer ligneDebut, Integer ligneFin) {
        if (isDetailDisplay) {
            StringBuffer line = new StringBuffer();
            for (int i = method.getStartingLocation().getLine(); i < ligneDebut; i++) {
                line.append(".");
            }
            for (int i = ligneDebut; i <= ligneFin; i++) {
                line.append("*");
            }
            for (int i = ligneFin; i <= method.getEndingLocation().getLine(); i++) {
                line.append(".");
            }

            addTextLine(method.getMethodName() + "(" + method.getStartingLocation().getLine() + ")" + line.toString());
        }
    }
    
    private void addTextLine(String textLine) {
        addTextLine(textLine, NO_EVENT);
    }

    private void addTextLine(String textLine, EventHandler<MouseEvent> eventHandler) {
        Text text = new Text(textLine + "\n");
        if (eventHandler != null) {
            text.setOnMouseClicked(eventHandler);
            text.setOnMouseEntered(e -> {                
                    text.setStyle("-fx-font-weight: bold;");
                    textFlow.getScene().setCursor(Cursor.DEFAULT);
                });
            text.setOnMouseExited(e -> {                
                text.setStyle("-fx-font-weight: normal;");
                textFlow.getScene().setCursor(Cursor.TEXT);
            });

        }
        textFlow.getChildren().add(text);
        
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
                listeValeur.add(tokenList.get(position).getTokenValue());
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

}
