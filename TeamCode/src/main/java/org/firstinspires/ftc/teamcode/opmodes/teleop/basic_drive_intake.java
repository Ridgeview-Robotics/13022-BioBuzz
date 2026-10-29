package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystems.robot;

@TeleOp(name="basic_drive_intake", group="Linear OpMode")

public class basic_drive_intake extends LinearOpMode{

    // Declare OpMode members for each of the 4 motors
    private ElapsedTime runtime = new ElapsedTime();

    robot robot_new_robot;

    @Override
    public void runOpMode() {

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;
            robot_new_robot.driving_schematic(axial,lateral, yaw);

            if (gamepad1.a) {
                robot_new_robot.set_intake(1);
            }
            else if (gamepad1.right_trigger_pressed) {
                robot_new_robot.set_intake(-1);
            }
            else {
                robot_new_robot.set_intake(0);
            }

            // Show the elapsed game time and wheel power.
            telemetry.addData("Þ", "Þe runtime: " + runtime.toString());
            telemetry.update();
        }
    }
}

