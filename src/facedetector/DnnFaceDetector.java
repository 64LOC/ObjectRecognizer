package facedetector;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

public class DnnFaceDetector {

    private static final double DEFAULT_CONFIDENCE_TRESHOLD = 0.6;

    private final Net net;
    private double confidenceTreshold;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    DnnFaceDetector(double confidenceTreshold) {
        net = Dnn.readNetFromCaffe("deploy.prototxt", "res10_300x300_ssd_iter_140000.caffemodel");
        this.confidenceTreshold = confidenceTreshold;
    }

    DnnFaceDetector() {
        this(DEFAULT_CONFIDENCE_TRESHOLD);
    }

    void detectFace(Mat frame) {

        int cols = frame.cols();
        int rows = frame.rows();

//        Imgproc.resize(frame, frame, new Size(300, 300));
        Mat blob = Dnn.blobFromImage(frame, 1f, new Size(300, 300), new Scalar(104, 177, 123), false, false);
        net.setInput(blob);
        Mat faces = net.forward();
        faces = faces.reshape(1, (int) faces.total() / 7);

        float detectionData[] = new float[7];

        for (int row = 0; row < faces.rows(); row++) {
            double confidence = faces.get(row, 2)[0];
            if (confidence > confidenceTreshold) {
                faces.get(row, 0, detectionData);
                int startX = (int) (detectionData[3] * cols);
                int startY = (int) (detectionData[4] * rows);
                int endX = (int) (detectionData[5] * cols);
                int endY = (int) (detectionData[6] * rows);

                Imgproc.rectangle(frame, new Point(startX, startY), new Point(endX, endY), new Scalar(0, 255, 0));
                Imgproc.putText(frame, new Double(confidence).toString(), new Point(startX, startY), 0, 0.6, new Scalar(255, 100, 0));
            }
        }
    }
}
