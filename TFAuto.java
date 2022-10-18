package org.firstinspires.ftc.teamcode;

import java.util.List;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

public class TFAuto {
    public enum Select { MAXCONF, FIRST, LAST, MAXAREA, MAXWIDTH, MAXHEIGHT, LEFTMOST, RIGHTMOST };
    public List<Recognition> scanRecognitions = null;
    public Recognition scanResult = null;
    public static String[] LABELS = {
            "1 Bolt",
            "2 Bulb",
            "3 Panel"
    };
    public static final String FRONT = "-FRONT-";
    public static final String BACK = "-BACK-";
    
    // source for TensorFlow Model
    private static final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
    // private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";

    // Obtain a Vuforia License Key from https://developer.vuforia.com/license-manager.
    // Paste it here, or use it as the second argument to init(...) below.
    private static final String VUFORIA_KEY =
            " -- YOUR NEW VUFORIA KEY GOES HERE  --- ";
    private VuforiaLocalizer vuforia;  // Vuforia localization engine
    private TFObjectDetector tfod;     // TensorFlow Object Detection engine

    public void init(HardwareMap hwMap) { init(hwMap, "Webcam 1"); }
    public void init(HardwareMap hwMap, String wcname) { init(hwMap, wcname, VUFORIA_KEY); }
    public void init(HardwareMap hwMap, String wcname, String vkey) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = vkey;
        if (wcname.equals(BACK)) 
            parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        else if (wcname.equals(FRONT)) 
            parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        else 
            parameters.cameraName = hwMap.get(WebcamName.class, wcname);
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        int tfodMonitorViewId = hwMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hwMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
        
        if (tfod != null) {
            tfod.activate();
            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can increase the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.0, 16.0/9.0);
        }
    }
    
    public void stop() {
        tfod.shutdown();
    }

    public Recognition scan() { return scan(Select.MAXCONF); }
    public Recognition scan(Select select) {
        scanResult = null;
        double maxval = -999999;
        if (tfod == null) return scanResult;
        scanRecognitions = tfod.getUpdatedRecognitions();
        if (scanRecognitions != null) {
            rloop: for (Recognition r : scanRecognitions) {
                double v = maxval;
                switch (select) {
                    case FIRST: scanResult = r; break rloop;
                    case LAST:  v = Math.max(1, maxval + 1); break;
                    case MAXCONF: v = r.getConfidence(); break;
                    case MAXAREA: v = r.getWidth() * r.getHeight(); break;
                    case MAXWIDTH: v = r.getWidth(); break;
                    case MAXHEIGHT: v = r.getHeight(); break;
                    case LEFTMOST: v = -r.getLeft(); break;
                    case RIGHTMOST: v = r.getRight(); break;
                }
                if (v > maxval) { scanResult = r; maxval = v; }
            }
        }
        return scanResult;
    }
}
