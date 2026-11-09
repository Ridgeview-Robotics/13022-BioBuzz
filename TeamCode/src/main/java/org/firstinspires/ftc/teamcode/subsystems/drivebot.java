package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Math.pow;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.Drivetrain_mecanum;

public class drivebot {
    Drivetrain_mecanum drivetrain;

    public drivebot(HardwareMap hardwareMap) {
        drivetrain = new Drivetrain_mecanum(hardwareMap);
    }

    public void driving_schematic(double x, double y, double r){

        double max;

        double leftFrontPower = pow(x + y + r,3);
        double rightFrontPower = pow(x - y - r,3);
        double leftBackPower = pow(x - y + r,3);
        double rightBackPower = pow(x + y - r,3);

        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if(max > 1.0){
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        drivetrain.set_motor_power(leftFrontPower, leftBackPower, rightBackPower, rightFrontPower);
    }

}


