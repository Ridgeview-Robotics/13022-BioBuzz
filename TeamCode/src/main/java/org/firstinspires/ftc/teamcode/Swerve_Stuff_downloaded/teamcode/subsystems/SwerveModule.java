package org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.AnalogInput;

import org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.Config.Constants;

// single differential swerve module
public class SwerveModule {
    private final DcMotorEx driveMotor;
    private final CRServo steerServo;
    private final AnalogInput angleEncoder;
    private final double offsetDeg;
    private final boolean steerReversed;

    private double targetAngleDeg = 0;
    private double targetSpeed = 0;

    public SwerveModule(DcMotorEx drive, CRServo steer, AnalogInput encoder,
                        double offsetDeg, boolean driveRev, boolean steerRev) {
        this.driveMotor = drive;
        this.steerServo = steer;
        this.angleEncoder = encoder;
        this.offsetDeg = offsetDeg;
        this.steerReversed = steerRev;

        driveMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        driveMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        driveMotor.setDirection(driveRev ? DcMotorEx.Direction.REVERSE : DcMotorEx.Direction.FORWARD);
    }

    // set module state (angle in degrees, speed [-1, 1])
    public void set(double angleDeg, double speed, boolean optimize) {
        angleDeg = normalizeAngle(angleDeg);

        if (optimize) {
            double delta = normalizeAngle(angleDeg - targetAngleDeg);
            if (Math.abs(delta) > 90) {
                angleDeg = normalizeAngle(angleDeg + 180);
                speed = -speed;
            }
        }

        targetAngleDeg = angleDeg;
        targetSpeed = speed;

        double power = steerPower(angleDeg);
        steerServo.setPower(steerReversed ? -power : power);
        driveMotor.setPower(speed);
    }

    // p-controller on angle error -> servo power
    private double steerPower(double targetDeg) {
        double error = normalizeAngle(targetDeg - getCurrentAngleDeg());
        double power = Constants.STEERING_KP * error;
        return Math.max(-Constants.STEERING_MAX_POWER, Math.min(Constants.STEERING_MAX_POWER, power));
    }

    // analog encoder voltage -> degrees, offset-corrected
    public double getCurrentAngleDeg() {
        if (angleEncoder != null) {
            double raw = (angleEncoder.getVoltage() / 3.3) * 360.0;
            return normalizeAngle(raw - offsetDeg);
        }
        return targetAngleDeg;
    }

    public double getDriveVelocity()  { return driveMotor.getVelocity(); }
    public int    getDrivePosition()  { return driveMotor.getCurrentPosition(); }
    public double getTargetAngleDeg() { return targetAngleDeg; }
    public double getTargetSpeed()    { return targetSpeed; }

    private static double normalizeAngle(double deg) {
        while (deg >  180) deg -= 360;
        while (deg < -180) deg += 360;
        return deg;
    }
}
