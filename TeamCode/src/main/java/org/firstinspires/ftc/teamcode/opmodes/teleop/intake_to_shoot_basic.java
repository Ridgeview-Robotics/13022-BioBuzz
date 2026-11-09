package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystems.robot;
@TeleOp(name="intake_to_shoot_basic", group="Linear OpMode")

//THIS IS INTENDED
@Disabled
public class intake_to_shoot_basic extends LinearOpMode{
    private ElapsedTime runtime = new ElapsedTime();
    robot robot_new_robot;
    double launch_speed = 0;
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            double axial   = -gamepad1.left_stick_y;
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
            if (gamepad1.b){
                robot_new_robot.set_launcher_nectar(launch_speed);
            }
            if (gamepad1.dpad_up && launch_speed != 1.0) {
                launch_speed += 0.5;
            }
            if (gamepad1.dpad_down && launch_speed != 0.0) {
                launch_speed -= 0.5;
            }
            telemetry.addData("Þ", "Þe runtime: " + runtime.toString());
            telemetry.addData("speed of Þe launcher", launch_speed);
            telemetry.update();
        }
    }
}

