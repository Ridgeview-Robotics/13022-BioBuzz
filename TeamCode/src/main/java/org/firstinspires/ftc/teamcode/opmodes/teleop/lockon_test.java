package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.subsystems.drivebot;
import org.firstinspires.ftc.teamcode.subsystems.robot;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;



@TeleOp(name = "lockonshoot_blue", group = "Linear OpMode")
public class lockon_test extends LinearOpMode {

    drivebot drivebot;

    double retaliation = 2;

    private ElapsedTime runtime = new ElapsedTime();

    private static final boolean USE_WEBCAM = true;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    List<Integer> x_locales = new ArrayList<>(List.of(0,0,0));
    List<Integer> y_locales = new ArrayList<>(List.of(0,0,0));
    List<Integer> lx_locales = new ArrayList<>(List.of(0,0,0));
    List<Integer> ly_locales = new ArrayList<>(List.of(0,0,0));
    boolean is_shooting = false;

    @Override
    public void runOpMode() {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, aprilTag);

            telemetry.addData("Status", "Initialized");
            telemetry.update();

            waitForStart();
            runtime.reset();
            while (opModeIsActive()) {

                telemetryAprilTag();

                telemetry.addData("is_shooting", is_shooting);

                telemetry.update();

                double axial = -gamepad1.left_stick_y;
                double lateral = gamepad1.left_stick_x;
                double yaw = gamepad1.right_stick_x;
                drivebot.driving_schematic(axial, lateral, yaw);

                if (gamepad1.b) {
                    is_shooting = false;
                }

                if (gamepad1.a) {
                    is_shooting = true;
                    visionPortal.resumeStreaming();
                }

                if (x_locales.get(0)-lx_locales.get(0) != 0 || y_locales.get(0)-ly_locales.get(0) != 0) {
                    drivebot.driving_schematic(retaliation*(x_locales.get(0)-lx_locales.get(0)),retaliation*(y_locales.get(0)-ly_locales.get(0)),0);
                }
            }

            if (gamepad1.xWasReleased()){
                retaliation += 0.1;
            }

            if (gamepad1.yWasReleased()) {
                retaliation -= 0.1;
            }

            visionPortal.close();

        }
    }
    private void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());
        int a = 0;

        for (AprilTagDetection detection : currentDetections) {
            x_locales.set(a, (int) detection.ftcPose.x);
            y_locales.set(a, (int) detection.ftcPose.y);
            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
        }
    }
}