package org.firstinspires.ftc.teamcode.assistiveshooting;
import org.firstinspires.ftc.teamcode.assistiveshooting.DrivingDataLogger;
import android.os.Environment;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DrivingDataLogger {

    private BufferedWriter writer;
    private final String sessionId;

    private final long startNs = System.nanoTime();
    private boolean hasLast = false;
    private double lastT, lastX, lastY, lastVx, lastVy;
    private int rowsSinceFlush = 0;

    private boolean shotPending = false;
    // A fired shot's row is held back until you label it hit/miss so that it doesn't log it incorrectly
    private String heldPrefix = null, heldSuffix = null;

    private String team = "red";
    private String gameState = "start_teleop";
    private String teeterSide = "unknown";

    // Edge detection for gamepad 2 buttons, so each press only counts once
    private boolean lastUp, lastDown, lastLeft, lastRight, lastLb, lastRb;

    public DrivingDataLogger(String driverName) {
        sessionId = driverName + "_" + System.currentTimeMillis();
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "FIRST/drivelogs");
            dir.mkdirs();
            writer = new BufferedWriter(new FileWriter(new File(dir, "drive_" + sessionId + ".csv")));
            writer.write("session_id,timestamp,x_position,y_position,heading,vx,vy,ax,ay,"
                    + "leftX,leftY,rightX,rightY,turret_deg,flywheel_speed,flywheel_angle,"
                    + "hit,shot,team,game_state,teeter_side\n");
        } catch (IOException e) {
            writer = null;
        }
    }
/*!!!IMPORTANT!!!: IF WILL (our real driver) IS ACTUALLY THE ONE DRIVING ADD THIS LINE IN THE CODE SOMEWHERE
This make it so others can drive but not affect my AI. adding an option to make it upon startup run as
being Will. The option is now in the OpMode: press A in init_loop (on gamepad 1 before start after init)
 to say Will is driving. See usage at the bottom.
 */

    //Context setters: call these whenever the values change
    public void setTeam(String t) { team = t.toLowerCase(); }              // "red" or "blue"
    public void setTeeterSide(String s) { teeterSide = s.toLowerCase(); }  // "left" or "right" = which side is UP
    public void setGameState(String s) { gameState = s; }                  // start_teleop, mid_teleop, late_teleop, endgame

    public String getTeam() { return team; }
    public String getTeeterSide() { return teeterSide; }

    /** Optional automatic game state. Pass seconds since teleop started. */
    public void setGameStateFromTime(double teleopSeconds) {
        if (teleopSeconds < 30) gameState = "start_teleop";
        else if (teleopSeconds < 60) gameState = "mid_teleop";
        else if (teleopSeconds < 90) gameState = "late_teleop";
        else gameState = "endgame";
    }

    /**  Gamepad 2 = the person logging. Gamepad 1 stays with the driver.
     *  D-pad up = left side of teeter-totter is UP, D-pad down = right side is UP,
     *  D-pad left = last shot made ✓, D-pad right = last shot missed,
     *  left bumper = team red, right bumper = team blue. */
    public void handleButtons(Gamepad g2) {
        if (g2.dpad_up && !lastUp) setTeeterSide("left");
        if (g2.dpad_down && !lastDown) setTeeterSide("right");
        if (g2.dpad_left && !lastLeft) markResult(true);
        if (g2.dpad_right && !lastRight) markResult(false);
        if (g2.left_bumper && !lastLb) setTeam("red");
        if (g2.right_bumper && !lastRb) setTeam("blue");
        lastUp = g2.dpad_up;
        lastDown = g2.dpad_down;
        lastLeft = g2.dpad_left;
        lastRight = g2.dpad_right;
        lastLb = g2.left_bumper;
        lastRb = g2.right_bumper;
    }

    //ALSO IMPORTANT: Please call this markshot thing whenever we fire a shot
    //NOTE I may add some additional stuff for when we are prepping to make a shot but not running flywheels yet
    public void markShot() {
        shotPending = true;
    }

    /** Call when you know the result (handleButtons does this for you with the d-pad). */
    public void markResult(boolean hit) {
        if (heldPrefix != null) {
            writeRow(heldPrefix, hit ? "1" : "0", heldSuffix);
            heldPrefix = null;
            heldSuffix = null;
        }
    }

    public void update(Gamepad gamepad1, double x, double y, double headingRad,
                       double turretDeg, double flywheelSpeed, double flywheelAngleDeg) {
        double t = (System.nanoTime() - startNs) / 1e9;

        if (!hasLast) {
            lastT = t; lastX = x; lastY = y;
            hasLast = true;
            return;
        }

        double dt = t - lastT;
        if (dt <= 0) return;

        double vx = (x - lastX) / dt;
        double vy = (y - lastY) / dt;
        double ax = (vx - lastVx) / dt;
        double ay = (vy - lastVy) / dt;

        String prefix = sessionId + "," + t + "," + x + "," + y + ","
                + Math.toDegrees(headingRad) + "," + vx + "," + vy + "," + ax + "," + ay + ","
                + gamepad1.left_stick_x + "," + gamepad1.left_stick_y + ","
                + gamepad1.right_stick_x + "," + gamepad1.right_stick_y + ","
                + turretDeg + "," + flywheelSpeed + "," + flywheelAngleDeg;
        String suffix = (shotPending ? "1" : "0") + "," + team + "," + gameState + "," + teeterSide;

        if (shotPending) {
            // A previous shot never got labeled then: save it with a blank hit
            if (heldPrefix != null) writeRow(heldPrefix, "", heldSuffix);
            heldPrefix = prefix;
            heldSuffix = suffix;
            shotPending = false;
        } else {
            writeRow(prefix, "", suffix);
        }

        lastT = t; lastX = x; lastY = y; lastVx = vx; lastVy = vy;
    }

    private void writeRow(String prefix, String hit, String suffix) {
        if (writer == null) return;
        try {
            writer.write(prefix + "," + hit + "," + suffix + "\n");
            //This gets around some errors it may throw for the amount of data that can be written, not a huge deal
            // But please don't remove!!  >:(
            if (++rowsSinceFlush >= 50) {
                writer.flush();
                rowsSinceFlush = 0;
            }
        } catch (IOException ignored) { }
    }

    public void close() {
        try {
            if (heldPrefix != null) writeRow(heldPrefix, "", heldSuffix); // (added) unlabeled last shot
            if (writer != null) { writer.flush(); writer.close(); }
        } catch (IOException ignored) { }
    }
}
//!!!HOW TO USE!!!

/* Ok so plain and simple talk down here rq. This code is technically a subsystem but the folder isn't there
 * which is ok. This has a bunch of functions that just need to be implemented into the code whenever we would
 * press a button where it will log it. Additionaly, id like it if you could add a thing that exports a state
 * (ie: endgame, begin teleop, ect) this would more accurately let the AI group data that will flow better together
 * AND im gonna try and make code that can detect what side of the teeter-totter is up or down. (for testing can be manual)
 * !!!IMPORTANT!!! Please also export the team side we are "training as so the bot doesn't shoot wrong" (ill add a spot for that)
 *  !!!ALSO!!! we need to make sure to export this driving data via
 * a website but ill probably be the one who is doing all the stuff with Will so this shouldn't be
 * a huge problem..... this is definitely forshadowing  ;-;  */

/*So here is all the stuff that needs to be done in the op-mode (!NOT AUTO!):
 in init:
logger = new DrivingDataLoggerSubsystem(localizer, "Willheham"); <-!Only if Will was the one driving!

in the loop:
logger.update(gamepad1, turretAngleDeg, flywheelSpeed);
if  (you just fired): logger.markShot();

 when the OpMode stops:
logger.close();*/

/* (added) UPDATED USAGE, replaces the block above (class name, update() arguments, and buttons changed):

 fields:
DrivingDataLogger logger = null;
boolean willIsDriving = false;
ElapsedTime teleopTimer = new ElapsedTime();

 in init_loop: press A on gamepad1 to say "Will is driving"
if (gamepad1.a) willIsDriving = true;
telemetry.addData("Logging as Will", willIsDriving);

 in start():
if (willIsDriving) {
    logger = new DrivingDataLogger("Willheham");
    logger.setTeam("red");   // or "blue"
}
teleopTimer.reset();

 in the loop:
if (logger != null) {
    logger.setGameStateFromTime(teleopTimer.seconds());
    logger.handleButtons(gamepad2);
    logger.update(gamepad1, x, y, headingRad, turretAngleDeg, flywheelSpeed, flywheelAngleDeg);
    if (justFired) logger.markShot();
    telemetry.addData("Team", logger.getTeam());
    telemetry.addData("Teeter side up", logger.getTeeterSide());
}

 when the OpMode stops:
if (logger != null) logger.close();

 GAMEPAD 2 (the person logging):
 D-pad up = left side UP | D-pad down = right side UP
 D-pad left = shot made  | D-pad right = shot missed
 Left bumper = red       | Right bumper = blue
*/