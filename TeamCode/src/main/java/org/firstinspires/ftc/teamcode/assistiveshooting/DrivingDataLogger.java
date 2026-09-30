package org.firstinspires.ftc.teamcode.assistiveshooting;
import android.os.Environment;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DrivingDataLogger {

    private BufferedWriter writer;

    private boolean hasLast = false;
    private double lastX, lastY, lastVx, lastVy;
    private long lastNs = 0;
    private int rowsSinceFlush = 0;
    private boolean shotPending = false;

    public DrivingDataLogger(String driverName) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "FIRST/drivelogs");
            dir.mkdirs();
            File file = new File(dir, "drive_" + driverName + "_" + System.currentTimeMillis() + ".csv");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("time_ms,x,y,heading_rad,vx,vy,ax,ay,leftX,leftY,rightX,rightY,turret_deg,flywheel_speed,shot\n");
        } catch (IOException e) {
            writer = null;
        }
    }
/*IMPORTANT: IF WILL(our real driver) IS ACTUALLY THE ONE DRIVING ADD THIS LINE IN THE CODE SOMEWHERE
This make it so others can drive but not affect my AI. adding an option to make it upon startup run as
being Will is probably the best way to do this*/
    // logger = new DrivingDataLogger("Willheham");


    //ALSO IMPORTANT: Please call this markshot thing whenever we fire a shot
    //NOTE I may add some additional stuff for when we are prepping to make a shot but not running flywheels yet
    public void markShot() {
        shotPending = true;
    }

    public void update(Gamepad gamepad1, double x, double y, double headingRad,
                       double turretDeg, double flywheelSpeed) {
        long nowNs = System.nanoTime();

        if (!hasLast) {
            lastX = x; lastY = y; lastNs = nowNs;
            hasLast = true;
            return;
        }

        double dt = (nowNs - lastNs) / 1e9;
        if (dt <= 0) return;

        double vx = (x - lastX) / dt;
        double vy = (y - lastY) / dt;
        double ax = (vx - lastVx) / dt;
        double ay = (vy - lastVy) / dt;

        if (writer != null) {
            try {
                writer.write(System.currentTimeMillis() + ","
                        + x + "," + y + "," + headingRad + ","
                        + vx + "," + vy + "," + ax + "," + ay + ","
                        + gamepad1.left_stick_x + "," + gamepad1.left_stick_y + ","
                        + gamepad1.right_stick_x + "," + gamepad1.right_stick_y + ","
                        + turretDeg + "," + flywheelSpeed + ","
                        + (shotPending ? 1 : 0) + "\n");
                shotPending = false;
                //This gets around some errors it may throw for the amount of data that can be written, not a huge deal
                if (++rowsSinceFlush >= 50) {
                    writer.flush();
                    rowsSinceFlush = 0;
                }
            } catch (IOException ignored) { }
        }

        lastX = x; lastY = y; lastVx = vx; lastVy = vy; lastNs = nowNs;
    }

    public void close() {
        try {
            if (writer != null) writer.close();
        } catch (IOException ignored) { }
    }
}
/* Ok so plain and simple talk down here rq. This code is technically a subsystem but the folder isn't there
* which is ok. This has a bunch of functions that just need to be implemented into the code whenever we would
* press a button where it will log it. Additionally id lake it if you could add a thing that exports a stae
* (ie: endgame, begin teleop, ect) this would more accurately let the AI group data that will flow better together
* AND im gonna try and make code that can detect what side of the teeter-totter is up or down. (for testing can be manual)
* !!!IMPORTANT!!! Please also export the team side we are "training as so the bot doesn't shoot wrong" (ill add a spot for that)
*  !!!ALSO!!! we need to make sure to export this driving data via
* a website but ill probably be the one who is doing all the stuff with Will so this shouldn't be
* a huge problem..... this is definitely forshadowing */

/*So here is all the stuff that needs to be done:
 in init:
logger = new DrivingDataLoggerSubsystem(localizer, "Willheham"); <-Only if Will was the one driving

in the loop:
logger.update(gamepad1, turretAngleDeg, flywheelSpeed);
if  (you just fired): logger.markShot();

 when the OpMode stops:
logger.close();*/