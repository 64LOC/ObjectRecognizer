package objectrecognizer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 */
public class ObjectRecognizerController implements Initializable {

    @FXML
    private ImageView currentFrame;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    void show(Image image) {
        Utils.onFXThread(currentFrame.imageProperty(), image);
    }

    void setClosed() {

    }

}
