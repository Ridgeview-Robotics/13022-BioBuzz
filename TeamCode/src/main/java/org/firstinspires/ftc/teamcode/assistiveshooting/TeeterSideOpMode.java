package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "TeeterSide Auto-Detect", group = "Assistive Shooting")
public class TeeterSideOpMode extends OpMode {

    private TeeterSide teeterSide;

    @Override
    public void init() {
        teeterSide = new TeeterSide(hardwareMap, "Webcam 1", telemetry);
        telemetry.addData("Status", "Initialized. Point camera at AprilTags.");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Automatically scans tags and updates telemetry internally
        teeterSide.update();

        // You can also use the detected values in your robot logic like this:
        if (teeterSide.hasTarget()) {
            TeeterSide.AllianceColor currentAlliance = teeterSide.getDetectedAlliance();
            TeeterSide.FieldSide currentSide = teeterSide.getDetectedSide();

            // Example condition:
            if (currentSide == TeeterSide.FieldSide.AUDIENCE) {
                // Do something specific for Audience side
            }
        }
    }

    @Override
    public void stop() {
        if (teeterSide != null) {
            teeterSide.close();
        }
    }
}