package org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.Config.Constants;
import org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.Config.SwerveConfig;

public class SwerveDrivetrain extends SubsystemBase {
    private final SwerveModule front, left, right;
    private final IMU imu;

    public SwerveDrivetrain(HardwareMap hw) {
        front = new SwerveModule(
            hw.get(DcMotorEx.class,   Constants.FRONT_DRIVE),
            hw.get(CRServo.class,     Constants.FRONT_STEER),
            hw.get(AnalogInput.class, Constants.FRONT_ENCODER),
            Constants.FRONT_OFF_DEG, Constants.FRONT_DRIVE_REV, Constants.FRONT_STEER_REV
        );
        left = new SwerveModule(
            hw.get(DcMotorEx.class,   Constants.LEFT_DRIVE),
            hw.get(CRServo.class,     Constants.LEFT_STEER),
            hw.get(AnalogInput.class, Constants.LEFT_ENCODER),
            Constants.LEFT_OFF_DEG, Constants.LEFT_DRIVE_REV, Constants.LEFT_STEER_REV
        );
        right = new SwerveModule(
            hw.get(DcMotorEx.class,   Constants.RIGHT_DRIVE),
            hw.get(CRServo.class,     Constants.RIGHT_STEER),
            hw.get(AnalogInput.class, Constants.RIGHT_ENCODER),
            Constants.RIGHT_OFF_DEG, Constants.RIGHT_DRIVE_REV, Constants.RIGHT_STEER_REV
        );

        imu = hw.get(IMU.class, Constants.IMU);
        imu.initialize(new IMU.Parameters(
            new RevHubOrientationOnRobot(Constants.LOGO, Constants.USB)
        ));
    }

    // called every loop by CommandScheduler
    @Override
    public void periodic() { /* telemetry updates happen in opmode */ }

    // x right+, y forward+, rx ccw+
    public void drive(double x, double y, double rx, boolean fc) {
        if (fc) {
            double h = Math.toRadians(getHeading());
            double cos = Math.cos(h), sin = Math.sin(h);
            double nx = x * cos + y * sin;
            double ny = -x * sin + y * cos;
            x = nx; y = ny;
        }

        double r    = Constants.ROBOT_RADIUS_INCHES;
        double fRad = 0;
        double lRad = Math.toRadians(Constants.MODULE_LEFT_ANGLE);
        double rRad = Math.toRadians(Constants.MODULE_RIGHT_ANGLE);

        double fx  = x + rx * r * Math.sin(fRad), fy = y + rx * r * Math.cos(fRad);
        double lx  = x + rx * r * Math.sin(lRad), ly = y + rx * r * Math.cos(lRad);
        double rx2 = x + rx * r * Math.sin(rRad), ry = y + rx * r * Math.cos(rRad);

        double fs = Math.hypot(fx, fy);
        double ls = Math.hypot(lx, ly);
        double rs = Math.hypot(rx2, ry);

        double max = Math.max(Math.max(fs, ls), rs);
        if (max > 1.0) { fs /= max; ls /= max; rs /= max; }

        fs *= SwerveConfig.MAX_SPEED_MULTIPLIER;
        ls *= SwerveConfig.MAX_SPEED_MULTIPLIER;
        rs *= SwerveConfig.MAX_SPEED_MULTIPLIER;

        front.set(Math.toDegrees(Math.atan2(fx,  fy)),  fs, SwerveConfig.OPTIMIZE_MODULE_ANGLES);
        left.set( Math.toDegrees(Math.atan2(lx,  ly)),  ls, SwerveConfig.OPTIMIZE_MODULE_ANGLES);
        right.set(Math.toDegrees(Math.atan2(rx2, ry)),  rs, SwerveConfig.OPTIMIZE_MODULE_ANGLES);
    }

    public void stop() {
        front.set(0, 0, false);
        left.set(0,  0, false);
        right.set(0, 0, false);
    }

    public void defense() {
        front.set(90,  0, false);
        left.set(210,  0, false);
        right.set(330, 0, false);
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void resetYaw() { imu.resetYaw(); }

    public SwerveModule getFront() { return front; }
    public SwerveModule getLeft()  { return left; }
    public SwerveModule getRight() { return right; }
}
