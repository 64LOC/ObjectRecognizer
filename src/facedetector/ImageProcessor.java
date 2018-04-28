package facedetector;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;

public class ImageProcessor implements Runnable {

    private boolean continueRunning = true;
    private final PermanentFrameGrabber permanentFrameGrabber;
    private final ImageView currentFrame;
    private final DnnFaceDetector dnnFaceDetector;

    ImageProcessor(PermanentFrameGrabber permanentFrameGrabber, ImageView currentFrame) {
        this.permanentFrameGrabber = permanentFrameGrabber;
        this.currentFrame = currentFrame;
        dnnFaceDetector = new DnnFaceDetector();
    }

    @Override
    public void run() {
        Mat frame = new Mat();

        while (continueRunning) {
            frame = permanentFrameGrabber.getLastGrabbedFrame();
            if (!frame.empty()) {
                dnnFaceDetector.detectFace(frame);
                Image imageToShow = Utils.mat2Image(frame);
                Utils.onFXThread(currentFrame.imageProperty(), imageToShow);
            }

            try {
                sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        continueRunning = false;
    }
}
