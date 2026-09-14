package org.firstinspires.ftc.teamcode.Config;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

@Config
public class Constants {
    // drive motors
    public static String FRONT_DRIVE = "frontDrive";
    public static String LEFT_DRIVE = "leftDrive";
    public static String RIGHT_DRIVE = "rightDrive";

    // steering servos
    public static String FRONT_STEER = "frontSteer";
    public static String LEFT_STEER = "leftSteer";
    public static String RIGHT_STEER = "rightSteer";

    // angle encoders
    public static String FRONT_ENCODER = "frontEncoder";
    public static String LEFT_ENCODER = "leftEncoder";
    public static String RIGHT_ENCODER = "rightEncoder";

    public static String IMU = "imu";

    // motor directions
    public static boolean FRONT_DRIVE_REV = false;
    public static boolean LEFT_DRIVE_REV = false;
    public static boolean RIGHT_DRIVE_REV = false;

    // servo directions
    public static boolean FRONT_STEER_REV = false;
    public static boolean LEFT_STEER_REV = false;
    public static boolean RIGHT_STEER_REV = false;

    // servo offsets (degrees)
    public static double FRONT_OFF_DEG = 0.0;
    public static double LEFT_OFF_DEG = 0.0;
    public static double RIGHT_OFF_DEG = 0.0;

    // imu orientation
    public static RevHubOrientationOnRobot.LogoFacingDirection LOGO =
            RevHubOrientationOnRobot.LogoFacingDirection.UP;
    public static RevHubOrientationOnRobot.UsbFacingDirection USB =
            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD;

    // dimensions
    public static double ROBOT_RADIUS_INCHES = 8.0;
    public static double WHEEL_DIAMETER_INCHES = 4.0;

    // module geometry (deg)
    public static double MODULE_LEFT_ANGLE = 120.0;
    public static double MODULE_RIGHT_ANGLE = 240.0;

    // steering
    public static double STEERING_KP = 0.01;
    public static double STEERING_MAX_POWER = 0.8;

    // module steer jitter suppression
    public static double STEER_JITTER_DEG = 0.5;
}
