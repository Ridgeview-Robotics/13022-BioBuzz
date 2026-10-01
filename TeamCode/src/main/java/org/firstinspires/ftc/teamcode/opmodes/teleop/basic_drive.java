package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.Drivetrain_mecanum;

@TeleOp(name="basic_drive", group="Linear OpMode")

public class basic_drive extends LinearOpMode{

    // Declare OpMode members for each of the 4 motors
    private ElapsedTime runtime = new ElapsedTime();

    Drivetrain_mecanum drivetrain;

    @Override
    public void runOpMode() {

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;
            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            // Send calculated power to wheels
            drivetrain.set_motor_power(frontLeftPower,backLeftPower,backRightPower,frontRightPower);

            // Show the elapsed game time and wheel power.
            telemetry.addData("☆☆☆Running :D☆☆☆", "Þe runtime: " + runtime.toString());
            telemetry.addData("þe front power", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("þe back power", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.update();
        }
    }
}

