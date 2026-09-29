package org.firstinspires.ftc.teamcode.assistiveshooting;
package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

public class DrivingDataLoggerSubsystem {

    private final FtcDashboard dashboard;
    private final Localizer localizer;

    // For velocity + acceleration calculations
    private Pose2d lastPose = new Pose2d();
    private Pose2d lastVelocity = new Pose2d();
    private long lastTime = System.currentTimeMillis();

    public DrivingDataLoggerSubsystem(HardwareMap hardwareMap, Localizer localizer) {
        dashboard = FtcDashboard.getInstance();
        this.localizer = localizer;
    }

    public void update(Gamepad gamepad1) {

        TelemetryPacket packet = new TelemetryPacket();

        long now = System.currentTimeMillis();
        double dt = (now - lastTime) / 1000.0;

        // Update odometry
        localizer.update();
        Pose2d pose = localizer.getPoseEstimate();

        // Compute velocity (dx/dt, dy/dt)
        double vx = (pose.getX() - lastPose.getX()) / dt;
        double vy = (pose.getY() - lastPose.getY()) / dt;

        // Compute acceleration (dv/dt)
        double ax = (vx - lastVelocity.getX()) / dt;
        double ay = (vy - lastVelocity.getY()) / dt;

        // Log pose
        packet.put("x", pose.getX());
        packet.put("y", pose.getY());
        packet.put("heading", pose.getHeading());

        // Log velocity
        packet.put("vx", vx);
        packet.put("vy", vy);

        // Log acceleration
        packet.put("ax", ax);
        packet.put("ay", ay);

        // Log driver inputs
        packet.put("leftX", gamepad1.left_stick_x);
        packet.put("leftY", gamepad1.left_stick_y);
        packet.put("rightX", gamepad1.right_stick_x);
        packet.put("rightY", gamepad1.right_stick_y);

        // Timestamp
        packet.put("timestamp", now);

        // Send packet over Wi-Fi
        dashboard.sendTelemetryPacket(packet);

        // Save for next loop
        lastPose = pose;
        lastVelocity = new Pose2d(vx, vy, 0);
        lastTime = now;
    }
}