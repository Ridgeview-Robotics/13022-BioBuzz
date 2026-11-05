package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystems.robot;

import java.util.List;

@TeleOp(name = "lockonshoot_blue", group = "Linear OpMode")
public class lockonshoot_blue extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    robot robot_new_robot;
    double launch_speed = 0;

    private static final boolean USE_WEBCAM = true;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");

        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        aprilTag.setDecimation(3);

        // Create the vision portal the easy way.
        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, aprilTag);
            // Wait for the DS start button to be touched.
            telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
            telemetry.addData(">", "Touch START to start OpMode");
            telemetry.update();
            waitForStart();

            while (opModeIsActive()) {

                telemetryAprilTag();

                telemetry.addData("speed of Þe launcher", launch_speed);

                telemetry.update();

                double axial = -gamepad1.left_stick_y;
                double lateral = gamepad1.left_stick_x;
                double yaw = gamepad1.right_stick_x;
                robot_new_robot.driving_schematic(axial, lateral, yaw);

                if (gamepad1.a) {
                    robot_new_robot.set_intake(1);
                } else if (gamepad1.right_trigger_pressed) {
                    robot_new_robot.set_intake(-1);
                } else {
                    robot_new_robot.set_intake(0);
                }

                if (gamepad1.b) {
                    robot_new_robot.set_launcher(launch_speed);
                }

                if (gamepad1.dpad_up && launch_speed != 1.0) {
                    launch_speed += 0.5;
                }

                if (gamepad1.dpad_down && launch_speed != 0.0) {
                    launch_speed -= 0.5;
                }

                // Save CPU resources; can resume streaming when needed.
                if (gamepad1.dpad_left) {
                    visionPortal.stopStreaming();
                } else if (gamepad1.dpad_right) {
                    visionPortal.resumeStreaming();
                }

            }

            // Save more CPU resources when camera is no longer needed.
            visionPortal.close();

        }
    }
    private void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        for (AprilTagDetection detection : currentDetections) {
            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
        }
    }
}







