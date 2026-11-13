package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
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
    private AprilTagDetection activeTarget = null;
    private Telemetry telemetry;

    private AllianceColor targetAlliance;
    private AllianceColor detectedAlliance = AllianceColor.UNKNOWN;
    private FieldSide detectedSide = FieldSide.UNKNOWN;
    private String lastDetectedLabel = "None";

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
                    lastDetectedLabel = "Single ID: " + id;
                    break;
                } else if (targetAlliance == AllianceColor.RED && id >= 34 && id <= 37) {
                    activeTarget = singleDet;
                    detectedAlliance = AllianceColor.RED;
                    detectedSide = FieldSide.AUDIENCE;
                    lastDetectedLabel = "Single ID: " + id;
                    break;
                } else if (targetAlliance == AllianceColor.BLUE && id >= 38 && id <= 41) {
                    activeTarget = singleDet;
                    detectedAlliance = AllianceColor.BLUE;
                    detectedSide = FieldSide.AUDIENCE;
                    lastDetectedLabel = "Single ID: " + id;
                    break;
                } else if (targetAlliance == AllianceColor.BLUE && id >= 42 && id <= 45) {
                    activeTarget = singleDet;
                    detectedAlliance = AllianceColor.BLUE;
                    detectedSide = FieldSide.OPPOSITE_AUDIENCE;
                    lastDetectedLabel = "Single ID: " + id;
                    break;
                }
            } else if (detection instanceof AprilTagClusterDetection) {
                AprilTagClusterDetection cluster = (AprilTagClusterDetection) detection;
                String name = (cluster.metadata != null && cluster.metadata.name != null)
                        ? cluster.metadata.name.toUpperCase()
                        : "";

                if (targetAlliance == AllianceColor.RED && name.contains("RED")) {
                    activeTarget = cluster;
                    detectedAlliance = AllianceColor.RED;
                    detectedSide = (name.contains("OPP") || name.contains("FAR") || name.contains("BACK"))
                            ? FieldSide.OPPOSITE_AUDIENCE
                            : FieldSide.AUDIENCE;
                    lastDetectedLabel = "Cluster: " + cluster.metadata.name;
                    break;
                } else if (targetAlliance == AllianceColor.BLUE && name.contains("BLUE")) {
                    activeTarget = cluster;
                    detectedAlliance = AllianceColor.BLUE;
                    detectedSide = (name.contains("OPP") || name.contains("FAR") || name.contains("BACK"))
                            ? FieldSide.OPPOSITE_AUDIENCE
                            : FieldSide.AUDIENCE;
                    lastDetectedLabel = "Cluster: " + cluster.metadata.name;
                    break;
                }
            }
        }

        if (telemetry != null) {
            telemetry.addData("Alliance", targetAlliance);
            telemetry.addData("Current Side", detectedSide);
            telemetry.addData("Actively Seeing Tag", hasTarget());
            telemetry.addData("Last Match", lastDetectedLabel);
            telemetry.addData("Raw Detections Count", detections.size());

            if (!hasTarget() && !detections.isEmpty()) {
                AprilTagDetection first = detections.get(0);
                if (first instanceof AprilTagClusterDetection) {
                    AprilTagClusterDetection firstCluster = (AprilTagClusterDetection) first;
                    String unmatchedName = (firstCluster.metadata != null) ? firstCluster.metadata.name : "null metadata";
                    telemetry.addData("Unmatched Cluster Name", unmatchedName);
                } else if (first instanceof AprilTagSingleDetection) {
                    telemetry.addData("Unmatched Single ID", ((AprilTagSingleDetection) first).id);
                }
            }
            telemetry.update();
        }
    }

    public boolean hasTarget() {
        return activeTarget != null;
    }

    public int getTargetId() {
        if (activeTarget instanceof AprilTagSingleDetection) {
            return ((AprilTagSingleDetection) activeTarget).id;
        }
        return -1;
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
