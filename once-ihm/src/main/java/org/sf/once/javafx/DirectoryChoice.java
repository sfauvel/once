package org.sf.once.javafx;
import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

class DirectoryChoice extends Button {
    public enum FileChooserType {
        OPEN
    }
    private File file;
    private FileChooserType dialogType;

    DirectoryChoice(final Stage primaryStage, final String title, final FileChooserType dialogType, final EventHandler<ActionEvent> eventHandler, final File defaultValue) {
        this.dialogType = dialogType;
        setText(title);
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setFile(chooseFile(primaryStage, title, defaultValue));
                eventHandler.handle(event);
            }
        });
    }

    public File chooseFile(Window stage, String title, File defaultValue) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(defaultValue); 
        File file = openDialog(stage, fileChooser);
        return file;
    }

    protected File openDialog(Window stage, DirectoryChooser fileChooser) {
        if (dialogType == FileChooserType.OPEN) {
            return fileChooser.showDialog(stage);
        } else {
            return null;
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}