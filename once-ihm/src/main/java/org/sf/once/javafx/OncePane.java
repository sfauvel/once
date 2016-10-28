package org.sf.once.javafx;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sf.once.ihm.OnceHandler;

import fr.sf.once.comparator.ComparateurWithSubstitutionAndType;
import fr.sf.once.core.Configuration;
import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.model.Code;
import fr.sf.once.model.FunctionalRedundancy;
import fr.sf.once.model.MethodLocalisation;
import fr.sf.once.model.Token;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OncePane extends StackPane {

    private static final String CSS_STYLESHEET = "css/once.css";

    private BoxFileChoice boxFileResultChoice;
    private Button runButton;
    private List<Token> tokenList = Collections.<Token> emptyList();
    private DuplicationCodePane duplicationPane = new DuplicationCodePane();

    public BoxFileChoice getBoxFileResultChoice() {
        return boxFileResultChoice;
    }

    public Button getRunButton() {
        return runButton;
    }

    public OncePane(Stage primaryStage) {
        String defaultPath = new File("").getAbsolutePath();
        initPane(primaryStage, defaultPath);

        getStylesheets().add(CSS_STYLESHEET);

        boxFileResultChoice.setFile(new File(""));
    }

    private void initPane(final Stage primaryStage, String defaultPath) {

        VBox box = new VBox(); 
        box.setSpacing(0);
        box.setPadding(new Insets(0, 0, 0, 10));

        {

            GridPane grid = new GridPane();
            // grid.setGridLinesVisible(true);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));

            runButton = createButton(primaryStage, "Start");
            int currentLine = 0;
            {
                {
                    boxFileResultChoice = new BoxFileChoice(primaryStage, "Select", DirectoryChoice.FileChooserType.OPEN, new File(defaultPath));
                    addFileChoiceOnLine("Source dir", grid, currentLine++, boxFileResultChoice/*, runButton*/);
                }
            }

            HBox filterBox = new HBox(); 

            grid.addRow(0, runButton);

            box.getChildren().addAll(grid, filterBox/*, runButton, btnOpenNewWindow*/, duplicationPane);
        }

        getChildren().add(box);
    }

    private void addFileChoiceOnLine(String label, GridPane grid, int line, BoxFileChoice boxFileChoice, Node... nodes) {

        List<Node> nodeList = Arrays.asList(
                new Label(label),
                boxFileChoice.getTextField(),
                boxFileChoice.getFileChoice());
        for (Node node : nodes) {
            nodeList.add(node);
        }
        
        grid.addRow(line, nodeList.toArray(new Node[0]));
    }

    private Button createButton(final Stage primaryStage, String buttonLabel) {

        Button btn = new Button();
        btn.setText(buttonLabel);
        btn.setMinWidth(100);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            private List<MethodLocalisation> methodList;

            @Override
            public void handle(ActionEvent event) {
                System.out.println(boxFileResultChoice.getAbsolutePath());
                OnceHandler handler = new OnceHandler();
                try {
                    StringBuffer buffer = new StringBuffer();
                    handler.compute(buffer, boxFileResultChoice.getAbsolutePath());
                    System.out.println(buffer.toString());

                    Code code = handler.getCode();
                    tokenList = code.getTokenList();
                    methodList = code.getMethodList();
                    RedundancyFinder manager = new RedundancyFinder(code);
                    showDuplication(manager);
                    

                } catch (IOException e) {
                    System.err.println(e);
                }

            }

            private void showDuplication(RedundancyFinder manager) {
                Class<ComparateurWithSubstitutionAndType> comparator = ComparateurWithSubstitutionAndType.class;
                int minimalSize = ConfStyle.BUTTON_MIN_WIDTH;
                Configuration configuration = new Configuration(comparator).withMinimalTokenNumber(minimalSize);
                List<FunctionalRedundancy> redundancies = manager.findRedundancies(configuration);

                duplicationPane.display(tokenList, methodList, redundancies);
            }

            private Thread launchAnalysis(final Task<Void> task) {
                Thread th = new Thread(task);
                th.setDaemon(true);
                return th;
            }

        });
        return btn;
    }

}
