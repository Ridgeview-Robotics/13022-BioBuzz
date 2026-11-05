package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

public class TeeterSide {

    // Enums declared safely inside the main class body
    public enum AllianceColor {
        RED,
        BLUE
    }

    public enum FieldSide {
        AUDIENCE,
        OPPOSITE_AUDIENCE
    }

    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private AprilTagSingleDetection activeTarget = null;
    private ElapsedTime runtime = new ElapsedTime();

    public TeeterSide(HardwareMap hardwareMap, String cameraName) {
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

    /**
     * Updates and searches for tags matching your exact alliance and field layout placement.
     */
    public void update(AllianceColor alliance, FieldSide side) {
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        activeTarget = null;

        for (AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                if (singleDet.metadata != null) {
                    int id = singleDet.id;

                    // Match exact game rules criteria
                    if (alliance == AllianceColor.RED) {
                        if (side == FieldSide.OPPOSITE_AUDIENCE && id >= 30 && id <= 33) {
                            activeTarget = singleDet;
                            break;
                        } else if (side == FieldSide.AUDIENCE && id >= 34 && id <= 37) {
                            activeTarget = singleDet;
                            break;
                        }
                    } else if (alliance == AllianceColor.BLUE) {
                        if (side == FieldSide.AUDIENCE && id >= 38 && id <= 41) {
                            activeTarget = singleDet;
                            break;
                        } else if (side == FieldSide.OPPOSITE_AUDIENCE && id >= 42 && id <= 45) {
                            activeTarget = singleDet;
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean hasTarget() {
        return activeTarget != null;
    }

    public int getTargetId() {
        return hasTarget() ? activeTarget.id : -1;
    }

    public double getTargetX() {
        return hasTarget() ? activeTarget.ftcPose.x : 0.0;
    }

    public double getTargetY() {
        return hasTarget() ? activeTarget.ftcPose.y : 0.0;
    }

    public double getTargetBearing() {
        return hasTarget() ? activeTarget.ftcPose.bearing : 0.0;
    }

    public void close() {
        if (visionPortal.getCameraState() != VisionPortal.CameraState.CAMERA_DEVICE_CLOSED) {
            visionPortal.close();
        }
    }
}
