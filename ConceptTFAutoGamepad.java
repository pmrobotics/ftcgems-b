package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@Autonomous

public class ConceptTFAutoGamepad extends LinearOpMode {

    public TFAuto tfAuto = null;
    private static String VUFORIA_KEY = "AUwJrJj/////AAABmZBm9Hhb0UTmuhcQBJ37xmYBxSuFuOzgZ4Qgzncyk3+Yh8VrceDAfcqDFDTFJStnHYV8BHR3Eu+xRKW27K0guJJmjVV0fvNyVgYcI/DzEC3xhCEH6DWiYl8gqQvG5v6MA5XY6PHSVNH4BqsSDqb+e6wp1qJ1ThEeiN3SwDzRET3Pfho64ucr1vSqGQ2ptJEnN0l0Vg3m9vjZk3oC7wbY27bshCDCra7UUysKUm7wL663wlpnD+IBpc57pJCffVXbmcrVOE2YHD3/lTSZzKyIKVfxN6Rs7YXjhg+K2qu9z0O9FiEtLg/mVGFVYfaN+UXKMP/6yymNo7TxRZuigXJBLlyCv7LLDJTSgwxFpZ/M4SnM";

    @Override
    public void runOpMode() {
        tfAuto = new TFAuto();
        tfAuto.init(hardwareMap, "Webcam 1", VUFORIA_KEY);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        int scansize = 0;
        int autoChoice = 0;
        String lastSignal = "";
        
        // Wait for the game to start (driver presses PLAY)
        while (!opModeIsActive() && !isStopRequested()) {
            if (tfAuto.scan() != null) {
                scansize = tfAuto.scanRecognitions.size();
                lastSignal = tfAuto.scanResult.getLabel();
                if (autoChoice < 10) {
                    if (lastSignal.equals("1 Bolt"))  autoChoice = 1;
                    if (lastSignal.equals("2 Bulb"))  autoChoice = 2;
                    if (lastSignal.equals("3 Panel")) autoChoice = 3;
                }
            }
            if (gamepad1.dpad_left)  autoChoice = 11;
            if (gamepad1.dpad_up)    autoChoice = 12;
            if (gamepad1.dpad_right) autoChoice = 13;
            if (gamepad1.dpad_down)  autoChoice = 0;
            telemetry.addData("autoChoice", autoChoice);
            telemetry.addData("scansize", scansize);
            telemetry.addData("lastSignal", lastSignal);
            telemetry.update();
        }
        
        // Driver has pressed play, so turn off TensorFlow
        // and start autonomous tasks
        tfAuto.stop();
        if (autoChoice > 10) autoChoice = autoChoice - 10;

        if (autoChoice == 1) {
            // run code for zone 1 here
            while (opModeIsActive()) {
                telemetry.addData("Auto", "Running autonomous zone 1");
                telemetry.update();
            }
        }

        if (autoChoice == 2) {
            // run code for zone 2 here
            while (opModeIsActive()) {
                telemetry.addData("Auto", "Running autonomous zone 2");
                telemetry.update();
            }
        }
        
        if (autoChoice == 3) {
            // run code for zone 1 here
            while (opModeIsActive()) {
                telemetry.addData("Auto", "Running autonomous zone 3");
                telemetry.update();
            }
        }
        
    }
}
