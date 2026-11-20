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
    double retaliation = 20;

    boolean nectar_mode = false;

    private static final boolean USE_WEBCAM = true;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    float x_locales = 0.0F;
    float y_locales = 0.0F;
    float lx_locales = 0.0F;
    float ly_locales = 0.0F;
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
            telemetry.update();
            waitForStart();

            while (opModeIsActive()) {

                lx_locales = x_locales;
                ly_locales = y_locales;

                List<AprilTagDetection> currentDetections = aprilTag.getDetections();
                telemetry.addData("# AprilTags Detected", currentDetections.size());

                for (AprilTagDetection detection : currentDetections) {
                    x_locales =(float) detection.ftcPose.x;
                    y_locales =(float) detection.ftcPose.y;
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                }

                float b = lx_locales - x_locales;
                float c = ly_locales - y_locales;

                telemetry.addData("lx:", lx_locales);
                telemetry.addData("ly:", ly_locales);
                telemetry.addData("y:", y_locales);
                telemetry.addData("x:", x_locales);
                telemetry.addData("move_by x:", b);
                telemetry.addData("move_by y:", c);
                telemetry.addData("speed of Þe launcher", launch_speed);
                telemetry.addData("is_shooting", is_shooting);
                telemetry.addData("shooting nectar",nectar_mode);
                telemetry.addData("is_shooting/retaliating", is_shooting);
                telemetry.addData("retalitation value:",retaliation);
                telemetry.update();

                double axial = -gamepad1.left_stick_y;
                double lateral = gamepad1.left_stick_x;
                double yaw = gamepad1.right_stick_x;
                robot_new_robot.driving_schematic(axial, lateral, yaw);

                if (gamepad1.left_trigger_pressed) {
                    robot_new_robot.set_intake(1);
                } else {
                    robot_new_robot.set_intake(0);
                }

                if (gamepad1.xWasReleased()) {
                    retaliation += 1;
                }

                if (gamepad1.yWasReleased()) {
                    retaliation -= 1;
                }

                if (gamepad1.b) {
                    is_shooting = false;
                    visionPortal.stopStreaming();
                    robot_new_robot.set_launcher_nectar(0);
                    robot_new_robot.set_launcher_pollen(0);
                }

                if (gamepad1.a) {
                    is_shooting = true;
                    visionPortal.resumeStreaming();
                    if (nectar_mode) {
                        robot_new_robot.set_launcher_nectar(launch_speed);
                    } else {
                        robot_new_robot.set_launcher_pollen(launch_speed);
                    }

                }

                if (gamepad1.right_bumper) {nectar_mode = !nectar_mode;}

                if (gamepad1.dpad_up && launch_speed != 1.0) {launch_speed += 0.05;}
                if (gamepad1.dpad_down && launch_speed != 0.0) {launch_speed -= 0.05;}

                if (is_shooting) {
                    if (ly_locales == 0 && lx_locales == 0){
                        telemetry.addLine("✦✦✦edge✦case✦✦✦");
                        ly_locales = y_locales;
                        lx_locales = x_locales;
                    }
                    robot_new_robot.driving_schematic(retaliation * (x_locales - lx_locales), retaliation * (y_locales - ly_locales), 0);
                }

                if (gamepad1.dpad_left){robot_new_robot.lockon(1);}
                if (gamepad1.dpad_right){robot_new_robot.lockon(-1);}

            }

            visionPortal.close();

        }
    }
}







