package org.firstinspires.ftc.teamcode.TeleOp;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Config.SwerveConfig;
import org.firstinspires.ftc.teamcode.Config.TeleOpConfig;
import org.firstinspires.ftc.teamcode.helpers.SlewRateLimiter;
import org.firstinspires.ftc.teamcode.subsystems.SwerveDrivetrain;

@TeleOp(name = "main tele", group = "Main")
public class TeleOpMain extends CommandOpMode {
    private SwerveDrivetrain swerve;
    private GamepadEx gp;
    private PIDFController headingPID;

    private final SlewRateLimiter xLim = new SlewRateLimiter();
    private final SlewRateLimiter yLim = new SlewRateLimiter();
    private final SlewRateLimiter rxLim= new SlewRateLimiter();

    private enum DriveMode { ROBOT, FIELD }
    private DriveMode mode = DriveMode.FIELD;

    private double tgtH = 0;
    private boolean hLock = false;
    private boolean lastStickNeutral = true;
    private boolean inDefense = false;

    @Override
    public void initialize() {
        swerve = new SwerveDrivetrain(hardwareMap);
        gp = new GamepadEx(gamepad1);

        headingPID = new PIDFController(
            SwerveConfig.HEADING_KP,
            SwerveConfig.HEADING_KI,
            SwerveConfig.HEADING_KD,
            0
        );
        headingPID.setTolerance(SwerveConfig.HEADING_TOLERANCE_DEG);
        
        register(swerve);

        swerve.resetYaw();
        tgtH = swerve.getHeading();
        telemetry.addData("status", "ready");
        telemetry.update();
    }

    @Override
    public void run() {
        super.run(); // runs cmdschduler + subsystem periodic()
        gp.readButtons();

        //fcd/robo-centric mode toggle
        if (gp.wasJustPressed(GamepadKeys.Button.BACK)) {
            mode = (mode == DriveMode.ROBOT) ? DriveMode.FIELD : DriveMode.ROBOT;
            gamepad1.rumble(150);
        }

        //yaw reset (x)
        if (gp.wasJustPressed(GamepadKeys.Button.X)) {
            swerve.resetYaw();
            tgtH = 0;
            gamepad1.rumble(200);
        }

        //defense toggle (b)
        if (gp.wasJustPressed(GamepadKeys.Button.B)) {
            inDefense = !inDefense;
            if (inDefense) { swerve.defense(); hLock = false; }
            gamepad1.rumble(100);
        }
        if (inDefense) { updateTelemetry(); return; }

        // --- raw inputs ---
        double lx   = gp.getLeftX();
        double ly   = gp.getLeftY();
        double rRaw = gp.getRightX();

        //magnitude-preserving curve on translation
        double mag = Math.hypot(lx, ly);
        double x, y;
        if (mag > 0) {
            if (mag > 1.0) { lx /= mag; ly /= mag; mag = 1.0; }
            double scale = TeleOpConfig.DRIVE_CURVE.apply(mag,
                TeleOpConfig.BEZIER_P1, TeleOpConfig.BEZIER_P2, TeleOpConfig.EXP_A) / mag;
            x = lx * scale;
            y = ly * scale;
        } else {
            x = 0; y = 0;
        }
        double rx = TeleOpConfig.DRIVE_CURVE.apply(rRaw,
            TeleOpConfig.BEZIER_P1, TeleOpConfig.BEZIER_P2, TeleOpConfig.EXP_A);

        //precision (bumpers)
        if (gp.isDown(GamepadKeys.Button.LEFT_BUMPER) || gp.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            x  *= TeleOpConfig.PRECISION_TRANSLATE_SCALE;
            y  *= TeleOpConfig.PRECISION_TRANSLATE_SCALE;
            rx *= TeleOpConfig.AIM_TURN_SCALE;
        }

        //dpad snap
        if      (gp.wasJustPressed(GamepadKeys.Button.DPAD_UP))    snapTo(SwerveConfig.SNAP_FORWARD_DEG);
        else if (gp.wasJustPressed(GamepadKeys.Button.DPAD_DOWN))  snapTo(SwerveConfig.SNAP_BACK_DEG);
        else if (gp.wasJustPressed(GamepadKeys.Button.DPAD_LEFT))  snapTo(SwerveConfig.SNAP_LEFT_DEG);
        else if (gp.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) snapTo(SwerveConfig.SNAP_RIGHT_DEG);

        // passive align to nearest cardinal
        double curH = swerve.getHeading();
        boolean stickMoving = Math.abs(rRaw) > TeleOpConfig.STICK_DB;
        if (SwerveConfig.USE_PASSIVE_ALIGN && !stickMoving && !hLock) {
            double[] cardinals = {0, 90, 180, -90};
            for (double c : cardinals) {
                if (Math.abs(wrap(c - curH)) < SwerveConfig.PASSIVE_ALIGN_DEG) {
                    snapTo(c);
                    gamepad1.rumble(100);
                    break;
                }
            }
        }

        // heading hold
        if (SwerveConfig.USE_HEADING_HOLD) {
            if (stickMoving) {
                hLock = false;
                lastStickNeutral = false;
            } else if (!lastStickNeutral) {
                tgtH = curH;
                hLock = true;
                lastStickNeutral = true;
            }
        } else if (stickMoving) {
            hLock = false;
        }

        if (hLock) {
            headingPID.setPIDF(SwerveConfig.HEADING_KP, SwerveConfig.HEADING_KI,
                               SwerveConfig.HEADING_KD, 0);
            //manual wrap for continuous input
            rx = headingPID.calculate(curH, curH + wrap(tgtH - curH));
        }

        // slew
        if (TeleOpConfig.USE_SLEW) {
            x  = xLim.calculate(x, TeleOpConfig.X_ACCEL, TeleOpConfig.X_DECEL);
            y  = yLim.calculate(y, TeleOpConfig.Y_ACCEL, TeleOpConfig.Y_DECEL);
            rx = rxLim.calculate(rx, TeleOpConfig.RX_ACCEL, TeleOpConfig.RX_DECEL);
        }

        swerve.drive(x, y, rx, mode == DriveMode.FIELD);
        updateTelemetry();
    }

    @Override
    public void reset() {
        super.reset();
        swerve.stop();
    }

    private void snapTo(double heading) {
        tgtH = heading; hLock = true;
        headingPID.reset();
        gamepad1.rumble(100);
    }

    private void updateTelemetry() {
        telemetry.addData("mode", mode);
        telemetry.addData("heading", "%.1f", swerve.getHeading());
        telemetry.addData("h lock", hLock ? "ON  tgt=" + tgtH : "OFF");
        telemetry.addData("defense", inDefense);
        telemetry.addData("front","%.1f° @ %.2f", swerve.getFront().getTargetAngleDeg(), swerve.getFront().getTargetSpeed());
        telemetry.addData("left","%.1f° @ %.2f", swerve.getLeft().getTargetAngleDeg(),  swerve.getLeft().getTargetSpeed());
        telemetry.addData("right","%.1f° @ %.2f", swerve.getRight().getTargetAngleDeg(), swerve.getRight().getTargetSpeed());
        telemetry.update();
    }

    private static double wrap(double deg) {
        while (deg >  180) deg -= 360;
        while (deg < -180) deg += 360;
        return deg;
    }
}
