package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class TeeterSideOpMode extends OpMode {

    private TeeterSide teeterSide;

    // This is the method TeeterSideBlue is looking for!
    protected abstract TeeterSide.AllianceColor getAllianceColor();

    @Override
    public void init() {
        TeeterSide.AllianceColor alliance = getAllianceColor();
        teeterSide = new TeeterSide(hardwareMap, "Webcam 1", telemetry, alliance);

        telemetry.addData("Status", "Initialized for " + alliance);
        telemetry.update();
    }

    @Override
    public void loop() {
        teeterSide.update();

        if (teeterSide.hasTarget()) {
            TeeterSide.FieldSide currentSide = teeterSide.getDetectedSide();

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