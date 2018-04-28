package facedetector;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.*;

public class FaceDetector extends Application {

    private FaceDetectorController controller;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startFX(primaryStage);

    }

    private void startFX(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FaceDetector.fxml"));
        BorderPane rootElement = (BorderPane) loader.load();
        Scene scene = new Scene(rootElement, 800, 500);
        scene.getStylesheets().add(getClass().getResource("facedetector.css").toExternalForm());
        primaryStage.setTitle("FaceDetector");
        primaryStage.setScene(scene);
        primaryStage.show();

        controller = loader.getController();

        primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                controller.setClosed();
            }
        }));
    }
}
