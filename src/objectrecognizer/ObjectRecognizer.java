package objectrecognizer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;

public class ObjectRecognizer extends Application {

    private ObjectRecognizerController controller;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startFX(primaryStage);

        // https://github.com/search?utf8=%E2%9C%93&q=Dnn.blobFromImage+NOT+DnnTensorFlowTest.java+NOT+Dnn.java+language%3AJava&type=Code
        Mat image = Imgcodecs.imread("eagle.png", Imgcodecs.IMREAD_COLOR);
        System.out.println(image);

        Net net = Dnn.readNetFromCaffe("bvlc_googlenet.prototxt", "bvlc_googlenet.caffemodel");
        Mat blob = Dnn.blobFromImage(image, 1.0f, new Size(224, 224), new Scalar(104, 117, 123, 0), false, false);

        net.setInput(blob);
        Mat prob = net.forward();

        System.out.println("cols: " + prob.cols() + ", rows:" + prob.rows() + ", steps: " + prob.step1());
        System.out.println(prob.elemSize());
        long ll = prob.step1();

        System.out.println("dim: " + prob.dims());
        System.out.println("total: " + prob.total());
        System.out.println("channels: " + prob.channels());

        for (int i = 0; i < 3; i++)
            System.out.println(i + ": " + prob.step1(i));

        //    prob = prob.reshape(1, 142);
        System.out.println(prob);
        System.out.println("cols: " + prob.cols() + ", rows:" + prob.rows());
        prob.type();
        /*
        double darray[];
        for (int irows = 0; irows < prob.rows(); irows++) {
            darray = prob.get(irows, 2);
            System.out.println(darray + ", " + darray[0] + ", " + irows);
        }

        for (int icols = 0; icols < prob.cols(); icols++) {
            darray = prob.get(0, icols);
            System.out.println(darray + ", " + darray[0] + ", " + icols + ", " + darray.length);
        }
         */
        float farray[] = new float[7];
        for (int icols = 0; icols < prob.cols(); icols++) {
            prob.get(0, icols, farray);
            System.out.print(icols + ": ");
            for (int i = 0; i < farray.length; i++)
                System.out.print(farray[i] + ", ");
            System.out.println("");
        }

        double maxConfidence = -1;
        int classId = -1;

        System.out.println("max confidence: " + maxConfidence);
        System.out.println("classID: " + classId);

        Image imageToShow = Utils.mat2Image(image);

        controller.show(imageToShow);
        //https://docs.opencv.org/3.4.0/d0/d6c/tutorial_dnn_android.html.
    }

    private void startFX(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ObjectRecognizer.fxml"));
        BorderPane rootElement = (BorderPane) loader.load();
        Scene scene = new Scene(rootElement, 800, 500);
        scene.getStylesheets().add(getClass().getResource("objectrecognizer.css").toExternalForm());
        primaryStage.setTitle("ObjectRecognizer");
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
