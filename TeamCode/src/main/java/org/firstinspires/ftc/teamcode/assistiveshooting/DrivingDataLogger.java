package org.firstinspires.ftc.teamcode.assistiveshooting;
import android.os.Environment;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DrivingDataLoggerSubsystem {

    private final Localizer localizer;
    private BufferedWriter writer;

    private Pose2d lastPose = null;
    private double lastVx = 0, lastVy = 0;
    private long lastNs = 0;
    private int rowsSinceFlush = 0;
    private boolean shotPending = false;

    public DrivingDataLoggerSubsystem(Localizer localizer, String driverName) {
        this.localizer = localizer;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "FIRST/drivelogs");
            dir.mkdirs();
            File file = new File(dir, "drive_" + driverName + "_" + System.currentTimeMillis() + ".csv");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("time_ms,x,y,heading_rad,vx,vy,ax,ay,leftX,leftY,rightX,rightY,turret_deg,flywheel_speed,shot\n");
        } catch (IOException e) {
            writer = null; // logging fails quietly instead of crashing the OpMode
        }
    }

    // Call this the moment a ball is fired.
    public void markShot() {
        shotPending = true;
    }

    public void update(Gamepad gamepad1, double turretDeg, double flywheelSpeed) {
        localizer.update();
        Pose2d pose = localizer.getPoseEstimate();
        long nowNs = System.nanoTime();

        // First loop: nothing to compare against yet
        if (lastPose == null) {
            lastPose = pose;
            lastNs = nowNs;
            return;
        }

        double dt = (nowNs - lastNs) / 1e9;
        if (dt <= 0) return;

        double vx = (pose.getX() - lastPose.getX()) / dt;
        double vy = (pose.getY() - lastPose.getY()) / dt;
        double ax = (vx - lastVx) / dt;
        double ay = (vy - lastVy) / dt;

        if (writer != null) {
            try {
                writer.write(System.currentTimeMillis() + ","
                        + pose.getX() + "," + pose.getY() + "," + pose.getHeading() + ","
                        + vx + "," + vy + "," + ax + "," + ay + ","
                        + gamepad1.left_stick_x + "," + gamepad1.left_stick_y + ","
                        + gamepad1.right_stick_x + "," + gamepad1.right_stick_y + ","
                        + turretDeg + "," + flywheelSpeed + ","
                        + (shotPending ? 1 : 0) + "\n");
                shotPending = false;
                if (++rowsSinceFlush >= 50) {
                    writer.flush();
                    rowsSinceFlush = 0;
                }
            } catch (IOException ignored) { }
        }

        lastPose = pose;
        lastVx = vx;
        lastVy = vy;
        lastNs = nowNs;
    }

    // Call this is for when the OpMode ends, or else the last few rows may not be saved. D:
    public void close() {
        try {
            if (writer != null) writer.close();
        } catch (IOException ignored) { }
    }
}
/* Ok so plain and simple talk down here rq. This code is technically a subsystem but the folder isn't there
* which is ok. This has a bunch of functions that just need to be implemented into the code whenever we would
* press a button where it will log it. !!!ALSO!!! we need to make sure to export this driving data via
* a website but ill probably be the one who is doing all the stuff with Will so this shouldn't be
* a huge problem..... this is definitely forshadowing */