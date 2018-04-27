package poc;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ObjectRecognizerPoC {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat image = Imgcodecs.imread("example_02.jpg", Imgcodecs.IMREAD_COLOR);
        Imgproc.resize(image, image, new Size(300, 300));

        Net net = Dnn.readNetFromCaffe("MobileNetSSD_deploy.prototxt.txt", "MobileNetSSD_deploy.caffemodel");
        Mat blob = Dnn.blobFromImage(image, 0.007843f, new Size(300, 300), new Scalar(127.5), false, false);

        net.setInput(blob);
        Mat objects = net.forward();

        objects = objects.reshape(1, (int) objects.total() / 7);

        float data[] = new float[7];

        for (int row = 0; row < objects.rows(); row++) {
            double confidence = objects.get(row, 2)[0];
            objects.get(row, 0, data);
            for (int j = 0; j < data.length; j++)
                System.out.println(j + ", " + data[j]);
        }

    }
}
