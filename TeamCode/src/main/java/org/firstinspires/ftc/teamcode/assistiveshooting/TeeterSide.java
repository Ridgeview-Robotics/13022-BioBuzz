package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

public class TeeterSide {

    public enum AllianceColor {
        RED,
        BLUE,
        UNKNOWN
    }

    public enum FieldSide {
        AUDIENCE,
        OPPOSITE_AUDIENCE,
        UNKNOWN
    }

    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private AprilTagSingleDetection activeTarget = null;
    private Telemetry telemetry;

    // Track the currently detected state automatically
    private AllianceColor detectedAlliance = AllianceColor.UNKNOWN;
    private FieldSide detectedSide = FieldSide.UNKNOWN;

    public TeeterSide(HardwareMap hardwareMap, String cameraName, Telemetry telemetry) {
        this.telemetry = telemetry;

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, cameraName))
                .addProcessor(aprilTagProcessor)
                .build();
    }

    // Updated update method: No parameters needed! It auto-detects based on visible tags.
    public void update() {
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        activeTarget = null;
        detectedAlliance = AllianceColor.UNKNOWN;
        detectedSide = FieldSide.UNKNOWN;

        for (AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                if (singleDet.metadata != null) {
                    int id = singleDet.id;

                    // Automatically determine alliance and side from tag ID ranges
                    if (id >= 30 && id <= 33) {
                        activeTarget = singleDet;
                        detectedAlliance = AllianceColor.RED;
                        detectedSide = FieldSide.OPPOSITE_AUDIENCE;
                        break;
                    } else if (id >= 34 && id <= 37) {
                        activeTarget = singleDet;
                        detectedAlliance = AllianceColor.RED;
                        detectedSide = FieldSide.AUDIENCE;
                        break;
                    } else if (id >= 38 && id <= 41) {
                        activeTarget = singleDet;
                        detectedAlliance = AllianceColor.BLUE;
                        detectedSide = FieldSide.AUDIENCE;
                        break;
                    } else if (id >= 42 && id <= 45) {
                        activeTarget = singleDet;
                        detectedAlliance = AllianceColor.BLUE;
                        detectedSide = FieldSide.OPPOSITE_AUDIENCE;
                        break;
                    }
                }
            }
        }

        // Send telemetry to the Driver Station
        if (telemetry != null) {
            telemetry.addData("Has Target", hasTarget());
            if (hasTarget()) {
                telemetry.addData("Target ID", getTargetId());
                telemetry.addData("Detected Alliance", detectedAlliance);
                telemetry.addData("Detected Side", detectedSide);
            } else {
                telemetry.addData("Status", "Searching for tags...");
            }
            telemetry.update();
        }
    }

    public boolean hasTarget() {
        return activeTarget != null;
    }

    public int getTargetId() {
        return hasTarget() ? activeTarget.id : -1;
    }

    public AllianceColor getDetectedAlliance() {
        return detectedAlliance;
    }

    public FieldSide getDetectedSide() {
        return detectedSide;
    }

    public void close() {
        if (visionPortal.getCameraState() != VisionPortal.CameraState.CAMERA_DEVICE_CLOSED) {
            visionPortal.close();
        }
    }
}