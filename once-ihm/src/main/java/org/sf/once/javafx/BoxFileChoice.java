package org.sf.once.javafx;
import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


/**
 * Groupe d'objet permettant la sélection d'un fichier et sa visualisation.
 *
 */
class BoxFileChoice extends HBox {

    private File selectedFile;

    private int BUTTON_MIN_WIDTH = 75;

    private TextField textField = new TextField();
    private DirectoryChoice directoryChoice;

    public BoxFileChoice(final Stage primaryStage, final String title, final DirectoryChoice.FileChooserType dialogType, File defaultValue) {
        textField.setEditable(false);
        textField.setMinWidth(300);
        
        directoryChoice = new DirectoryChoice(primaryStage, title, dialogType, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChoice fileChoice = (DirectoryChoice) event.getSource();
                if (fileChoice.getFile() != null) {
                    setFile(fileChoice.getFile());
                }
            }
        }, defaultValue);
        directoryChoice.setMinWidth(BUTTON_MIN_WIDTH);
        
        setMargin(textField, new Insets(0, 0, 0, 10));
    }
    public String getAbsolutePath() {
        return directoryChoice.getFile().getAbsolutePath();
    }
    
    public TextField getTextField() {
        return textField;
    }

    public DirectoryChoice getFileChoice() {
        return directoryChoice;
    }
    
    public void setFile(File file) {
        selectedFile = file;
        getFileChoice().setFile(selectedFile);
        if (selectedFile != null) {
            getTextField().setText(selectedFile.getAbsolutePath());
        } else {
            getTextField().setText("");
        }
    }
    
    public void setFile(String fileName) {
        setFile(new File(fileName));
    }
    
    
    public boolean isFileSelected() {
        return selectedFile != null;
    }
    
//    public Button getOpenButton() {
//        return openButton;
//    }
//    
}