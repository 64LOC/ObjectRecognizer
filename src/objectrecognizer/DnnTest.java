package objectrecognizer;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.*;
import org.opencv.dnn.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;

public class DnnTest {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    static String[] PASCIFARnames = new String[]{"airoplane", "bicycle", "bird", "boat", "bottle", "bus", "car", "cat", "chair", "cow", "diningtable", "dog", "horse", "motorbike", "person",
        "pottedplant", "sheep", "sofa", "train", "tvmonitor"};

    public static void main(String[] args) {

        PASCIFARnames = readWords();

        //Net net = Dnn.readNetFromDarknet("C:\\opencv3.4.1\\samples\\tiny-yolo-voc.cfg", "C:\\opencv3.4.1\\samples\\tiny-yolo-voc.weights");
        Net net = Dnn.readNetFromCaffe("bvlc_googlenet.prototxt", "bvlc_googlenet.caffemodel");

        if (net.empty())
            System.out.println("未載入特徵類別檔!");

        //String image_file = "C:\\opencv3.4.1\\samples\\004545.jpg";
        String image_file = "eagle.png";
        Mat im = Imgcodecs.imread(image_file, Imgcodecs.IMREAD_COLOR);
        if (im.empty())
            System.out.println("Reading Image error");

        Mat frame = new Mat();

        Size sz1 = new Size(im.cols(), im.rows());
        Imgproc.resize(im, frame, sz1);

        Mat resized = new Mat();
        //Size sz = new Size(416,416);
        Size blockSize = new Size(224, 224);//146person
        Imgproc.resize(im, resized, blockSize);

        float scale = 1.0F;
        Mat inputBlob = Dnn.blobFromImage(im, scale, blockSize, new Scalar(104, 117, 123), false, false);
        System.out.println(inputBlob);
        net.setInput(inputBlob);
        Mat detectionMat = net.forward();
        System.out.println(detectionMat);
        if (detectionMat.empty())
            System.out.println("No result");

        for (int i = 0; i < detectionMat.rows(); i++) {
            int probability_index = 5;
            int size = (int) (detectionMat.cols() * detectionMat.channels());

            float[] data = new float[size];
            detectionMat.get(i, 0, data);
            float confidence = -1;
            int objectClass = -1;
            for (int j = 0; j < detectionMat.cols(); j++)
                if (j >= probability_index && confidence < data[j]) {
                    confidence = data[j];
                    objectClass = j - probability_index;
                }

            if (confidence > 0.5) {
                System.out.println("Result Object: " + i);
                for (int j = 0; j < detectionMat.cols(); j++)
                    System.out.print(" " + j + ":" + data[j]);
                System.out.println("");
                float x = data[0];
                float y = data[1];
                float width = data[2];
                float height = data[3];
                float xLeftBottom = (x - width / 2) * frame.cols();
                float yLeftBottom = (y - height / 2) * frame.rows();
                float xRightTop = (x + width / 2) * frame.cols();
                float yRightTop = (y + height / 2) * frame.rows();

                System.out.println("Class: " + PASCIFARnames[objectClass]);
                System.out.println("Confidence: " + confidence);
                System.out.println("ROI: " + xLeftBottom + " " + yLeftBottom + " " + xRightTop + " " + yRightTop + "\n");
                //繪出框
                Imgproc.rectangle(frame, new Point(xLeftBottom, yLeftBottom),
                        new Point(xRightTop, yRightTop), new Scalar(0, 255, 0), 3);
                //繪出類別
                Imgproc.putText(frame, PASCIFARnames[objectClass], new Point(xLeftBottom + 10, yLeftBottom + 3), 3, 0.6, new Scalar(0, 0, 05));
            }
        }

        //Imgcodecs.imwrite("C:\\opencv3.4.1\\samples\\004545out.jpg", frame);
    }

    static String[] readWords() {
        LinkedList<String> resultList = new LinkedList<>();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader("synset_words.txt");
            LineNumberReader lineReader = new LineNumberReader(fileReader);

            String line;
            do {
                line = lineReader.readLine();
                if (line != null)
                    resultList.add(line.substring(line.indexOf(" ") + 1));
            } while (line != null);

        } catch (IOException ex) {
            Logger.getLogger(DnnTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] result = resultList.toArray(new String[resultList.size()]);

        return result;
    }
}
