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
/*
− AprilTag ID’s 30, 31, 32, 33 on the red CELL on the side of the FIELD opposite of the audience.
− AprilTag ID’s 34, 35, 36, 37 on the red CELL on the audience side.
− AprilTag ID’s 38, 39, 40, 41 on the blue CELL on the audience side.
− AprilTag ID’s 42, 43, 44, 45 on the blue CELL on the side of the FIELD opposite of the audience.
 */
public class TeeterSide {

    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;

    private AprilTagSingleDetection activeTarget = null;
    private ElapsedTime runtime = new ElapsedTime();

    public TeeterSide(HardwareMap hardwareMap, String cameraName) {
        // 1. Set up the processor
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();

        // 2. Set up and initialize the Vision Portal
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, cameraName))
                .addProcessor(aprilTagProcessor)
                .build();
    }

    public void update() {
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        activeTarget = null; // Reset every frame so we don't use old data

        for (AprilTagDetection detection : detections) {
            // Check if the detection is an individual tag before processing
            if (detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                // Safely read fields from the subclass
                if (singleDet.metadata != null && singleDet.id >= 30 && singleDet.id <= 33) {
                    activeTarget = singleDet;
                    break; // Target found! Stop looking at other tags in this frame
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
