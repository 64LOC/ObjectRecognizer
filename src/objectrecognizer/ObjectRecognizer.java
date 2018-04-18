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
import org.opencv.imgproc.Imgproc;

public class ObjectRecognizer extends Application {

    private ObjectRecognizerController controller;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startFX(primaryStage);

        Mat image = Imgcodecs.imread("eagle.png");
        System.out.println(image);

        Imgproc.resize(image, image, new Size(224, 224));
        //Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
        System.out.println(image);
        //             blobFromImage(Mat image, double scalefactor, Size size, Scalar mean, boolean swapRB, boolean crop)
        Mat blob = Dnn.blobFromImage(image, 1.0f, new Size(224, 224), new Scalar(104, 117, 123), false);

        int cols = blob.cols();
        int rows = blob.rows();
        System.out.println(blob);

        Net net = Dnn.readNetFromCaffe("bvlc_googlenet.prototxt", "bvlc_googlenet.caffemodel");
        //Net net = Dnn.readNetFromCaffe("MobileNet_deploy.prototxt", "MobileNet_deploy.caffemodel");

        //https://github.com/opencv/opencv/issues/10802
        net.setInput(blob);
        Mat prob = net.forward();

        System.out.println(prob.total() + ", " + prob.rows());

        for (int irows = 0; irows < prob.rows(); irows++)
            for (int icols = 0; icols < prob.cols(); icols++) {
                double darray[] = prob.get(irows, icols);
                StringBuilder dString = new StringBuilder();
                for (int i = 0; i < darray.length; i++)
                    dString.append(darray[i] + ", ");
                System.out.println("col: " + icols + ", " + dString);
            }

        for (int i = 0; i < prob.rows(); i++) {
            double confidence = prob.get(i, 2)[0];
            System.out.print(confidence);
            int classId = (int) prob.get(i, 1)[0];
            System.out.println(" : " + classId);
        }

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
