package scheduler.sim.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(
                getClass().getResource("/main.fxml")
        );

        Scene scene = new Scene(root);

        
        scene.getStylesheets().add(
            getClass().getResource("/styles.css").toExternalForm()
        );

        stage.setTitle("CPU Scheduler Simulator");
        stage.setScene(scene);
        // stage.setWidth(1200);
        // stage.setHeight(800);
        // stage.setWidth(1200);
stage.setHeight(800);
stage.setMinWidth(900);
stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}