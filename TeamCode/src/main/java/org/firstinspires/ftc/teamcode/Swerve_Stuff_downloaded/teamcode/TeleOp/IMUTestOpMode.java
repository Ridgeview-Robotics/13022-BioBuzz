package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Config.Constants;

@TeleOp(name="imu test", group="Calibration")
public class IMUTestOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        IMU imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters params = new IMU.Parameters(
            new RevHubOrientationOnRobot(Constants.LOGO, Constants.USB)
        );
        imu.initialize(params);

        telemetry.addData("status", "ready");
        telemetry.addData("", "press A to reset heading");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                imu.resetYaw();
            }

            double yaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double pitch = imu.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES);
            double roll = imu.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES);

            telemetry.addData("yaw", "%.2f°", yaw);
            telemetry.addData("pitch", "%.2f°", pitch);
            telemetry.addData("roll", "%.2f°", roll);
            telemetry.addData("", "");
            telemetry.addData("test", "rotate robot CCW");
            telemetry.addData("expected", "yaw should increase (positive)");
            telemetry.update();
        }
    }
}

