package org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.Config;

import com.acmerobotics.dashboard.config.Config;

// tunable swerve params (pid, heading hold, etc.)
@Config
public class SwerveConfig {

    // heading hold pid
    public static double HEADING_KP = 0.02;
    public static double HEADING_KI = 0.0;
    public static double HEADING_KD = 0.001;
    public static double HEADING_TOLERANCE_DEG = 2.0;
    public static double HEADING_HOLD_ROTATION_THRESHOLD = 0.05;
    public static double D_ALPHA = 0.8; // d-term low-pass filter weight

    // heading snap targets (dpad, deg)
    public static double SNAP_FORWARD_DEG = 0.0;
    public static double SNAP_RIGHT_DEG   = -90.0;
    public static double SNAP_BACK_DEG    = 180.0;
    public static double SNAP_LEFT_DEG    = 90.0;

    // features
    public static boolean START_FIELD_CENTRIC  = true;
    public static boolean OPTIMIZE_MODULE_ANGLES = true;
    public static boolean USE_HEADING_HOLD     = true;
    public static boolean USE_PASSIVE_ALIGN    = false;
    public static double  PASSIVE_ALIGN_DEG    = 10.0;

    // speed
    public static double MAX_SPEED_MULTIPLIER  = 1.0;
}
