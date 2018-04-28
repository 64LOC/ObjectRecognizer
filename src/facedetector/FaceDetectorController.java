package facedetector;

import static java.lang.Thread.sleep;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 */
public class FaceDetectorController implements Initializable {

    private PermanentFrameGrabber permanentFrameGrabber;
    private ImageProcessor imageProcessor;

    @FXML
    private ImageView currentFrame;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        permanentFrameGrabber = new PermanentFrameGrabber();
        new Thread(permanentFrameGrabber).start();

        try {
            sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FaceDetectorController.class.getName()).log(Level.SEVERE, null, ex);
        }

        imageProcessor = new ImageProcessor(permanentFrameGrabber, currentFrame);
        new Thread(imageProcessor).start();
    }

    void setClosed() {
        imageProcessor.stop();
        permanentFrameGrabber.stop();
    }

}
