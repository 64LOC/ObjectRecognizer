package facedetector;

import java.util.Date;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

class PermanentFrameGrabber implements Runnable {

    private final VideoCapture capture = new VideoCapture();
    private Mat lastGrabbedFrame;
    private boolean continueRunning = true;
    private long lastTimestamp = 0;
    private double fps = 0;

    public PermanentFrameGrabber() {
        capture.open(0);
        if (!capture.isOpened()) {
            System.err.println("Impossible to open the camera connection...");
            continueRunning = false;
        }
    }

    @Override
    public void run() {
        Mat grabbedFrame = new Mat();

        while (continueRunning)
            if (capture.read(grabbedFrame))
                setLastGrabbedFrame(grabbedFrame); //calculateFPS();

        capture.release();
    }

    public void stop() {
        continueRunning = false;
    }

    private synchronized void setLastGrabbedFrame(Mat lastGrabbedFrame) {
        this.lastGrabbedFrame = lastGrabbedFrame;
    }

    public synchronized Mat getLastGrabbedFrame() {
        Mat result = new Mat();
        result = lastGrabbedFrame.clone();
        return result;
    }

    private void calculateFPS() {
        if (lastTimestamp == 0)
            lastTimestamp = new Date().getTime();
        else {
            long now = new Date().getTime();
            fps = 1000 / (now - lastTimestamp);
            lastTimestamp = now;
            System.out.println("FPS: " + fps);
        }
    }

    public double getFps() {
        return fps;
    }
}
