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



@TeleOp(name = "lockon_test", group = "Linear OpMode")
public class lockon_test extends LinearOpMode {

    double retaliation = 20;

    private ElapsedTime runtime = new ElapsedTime();

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

        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, aprilTag);

            telemetry.addData("Status", "Initialized");
            telemetry.update();
        }

        drivebot drivebot = new drivebot(hardwareMap);
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
            telemetry.addData("moveby x:", b);
            telemetry.addData("moveby y:", c);
            


                telemetry.addData("is_shooting/retaliating", is_shooting);
                telemetry.addData("retalitation value:",retaliation);


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

                if (is_shooting) {
                    if (ly_locales == 0 && lx_locales == 0){
                        telemetry.addLine("✦✦✦edge✦case✦✦✦þ");
                        ly_locales = y_locales;
                        lx_locales = x_locales;
                    }
                    drivebot.driving_schematic(retaliation * (x_locales - lx_locales), retaliation * (y_locales - ly_locales), 0);


                if (gamepad1.xWasReleased()) {
                    retaliation += 0.1;
                }

                if (gamepad1.yWasReleased()) {
                    retaliation -= 0.1;
                }

            }
    }}}