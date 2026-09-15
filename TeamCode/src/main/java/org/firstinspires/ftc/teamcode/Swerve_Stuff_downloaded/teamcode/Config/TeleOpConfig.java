package org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.Config;

import com.acmerobotics.dashboard.config.Config;
import org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.helpers.Curve;

@Config
public class TeleOpConfig {
    public static double AIM_TURN_SCALE = 0.2;
    public static double PRECISION_TRANSLATE_SCALE = 0.3;
    public static double STICK_DB = 0.05;
    public static boolean USE_SLEW = true;

    public static double Y_ACCEL  = 3.0;
    public static double Y_DECEL  = 3.0;
    public static double X_ACCEL  = 3.0;
    public static double X_DECEL  = 3.0;
    public static double RX_ACCEL = 3.0;
    public static double RX_DECEL = 3.0;

    public static Curve  DRIVE_CURVE = Curve.LINEAR;
    public static double BEZIER_P1   = 0.5;
    public static double BEZIER_P2   = 0.5;
    public static double EXP_A       = 2.0;
}
