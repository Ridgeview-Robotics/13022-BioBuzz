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
import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "lockonshoot_blue", group = "Linear OpMode")
public class lockonshoot_blue extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    robot robot_new_robot;
    double launch_speed = 0;

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
        telemetry.addData("Status", "Initialized");
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        aprilTag.setDecimation(3);
        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, aprilTag);
            // Wait for the DS start button to be touched.
            telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
            telemetry.update();
            waitForStart();

            while (opModeIsActive()) {

                telemetryAprilTag();

                telemetry.addData("speed of Þe launcher", launch_speed);
                telemetry.addData("is_shooting", is_shooting);

                telemetry.update();

                double axial = -gamepad1.left_stick_y;
                double lateral = gamepad1.left_stick_x;
                double yaw = gamepad1.right_stick_x;
                robot_new_robot.driving_schematic(axial, lateral, yaw);

                if (gamepad1.left_trigger_pressed) {
                    robot_new_robot.set_intake(1);
                } else if (gamepad1.right_trigger_pressed) {
                    robot_new_robot.set_intake(-1);
                } else {
                    robot_new_robot.set_intake(0);
                }

                if (gamepad1.b) {
                    is_shooting = false;
                    visionPortal.stopStreaming();
                    robot_new_robot.set_launcher(0);
                }

                if (gamepad1.a) {
                    is_shooting = true;
                    visionPortal.resumeStreaming();
                    robot_new_robot.set_launcher(launch_speed);
                }

                if (gamepad1.dpad_up && launch_speed != 1.0) {
                    launch_speed += 0.5;
                }

                if (gamepad1.dpad_down && launch_speed != 0.0) {
                    launch_speed -= 0.5;
                }

                if (x_locales.get(0)-lx_locales.get(0) != 0 || y_locales.get(0)-ly_locales.get(0) != 0) {
                    robot_new_robot.driving_schematic(2*(x_locales.get(0)-lx_locales.get(0)),2*(y_locales.get(0)-ly_locales.get(0)),0);
                }
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







