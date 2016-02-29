package org.sf.once.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    private static final String CSS_STYLESHEET = "css/once.css";

    public static void main(String[] args) {
        launch(args);
    }

    public Parameters getApplicationParameters() {
        return super.getParameters();
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Detect code patterns");
        Scene scene = new Scene(new OncePane(primaryStage), 1200, 600);
        scene.getStylesheets().add(CSS_STYLESHEET);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
