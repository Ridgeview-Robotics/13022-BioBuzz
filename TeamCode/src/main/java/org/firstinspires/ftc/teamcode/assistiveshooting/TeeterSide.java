package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
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

    private AllianceColor targetAlliance;
    private AllianceColor detectedAlliance = AllianceColor.UNKNOWN;
    private FieldSide detectedSide = FieldSide.UNKNOWN;

    public TeeterSide(HardwareMap hardwareMap, String cameraName, Telemetry telemetry, AllianceColor alliance) {
        this.telemetry = telemetry;
        this.targetAlliance = alliance;

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, cameraName))
                .addProcessor(aprilTagProcessor)
                .build();
    }

    public void update() {
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        activeTarget = null;

        for (AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;
                int id = singleDet.id;

                if (targetAlliance == AllianceColor.RED && id >= 30 && id <= 33) {
                    activeTarget = singleDet;
                    detectedAlliance = AllianceColor.RED;
                    detectedSide = FieldSide.OPPOSITE_AUDIENCE;
                    break;
                } else if (targetAlliance == AllianceColor.RED && id >= 34 && id <= 37) {
                    activeTarget = singleDet;
                    detectedAlliance = AllianceColor.RED;
                    detectedSide = FieldSide.AUDIENCE;
                    break;
                } else if (targetAlliance == AllianceColor.BLUE && id >= 38 && id <= 41) {
                    activeTarget = singleDet;
                    detectedAlliance = AllianceColor.BLUE;
                    detectedSide = FieldSide.AUDIENCE;
                    break;
                } else if (targetAlliance == AllianceColor.BLUE && id >= 42 && id <= 45) {
                    activeTarget = singleDet;
                    detectedAlliance = AllianceColor.BLUE;
                    detectedSide = FieldSide.OPPOSITE_AUDIENCE;
                    break;
                }
            }
        }

        if (telemetry != null) {
            telemetry.addData("Alliance", targetAlliance);
            telemetry.addData("Current Side", detectedSide);
            telemetry.addData("Actively Seeing Tag", hasTarget());

            if (hasTarget()) {
                telemetry.addData("Tag ID", getTargetId());
            } else if (!detections.isEmpty() && detections.get(0) instanceof AprilTagSingleDetection) {
                telemetry.addData("Ignored Other Tag ID", ((AprilTagSingleDetection) detections.get(0)).id);
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
        if (visionPortal != null && visionPortal.getCameraState() != VisionPortal.CameraState.CAMERA_DEVICE_CLOSED) {
            visionPortal.close();
        }
    }
}