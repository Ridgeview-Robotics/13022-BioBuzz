package org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.TeleOp;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.subsystems.SwerveDrivetrain;

@TeleOp(name = "swerve debug", group = "Debug")
public class SwerveDebugOpMode extends OpMode {
    private SwerveDrivetrain swerve;
    private GamepadEx gp;

    @Override
    public void init() {
        swerve = new SwerveDrivetrain(hardwareMap);
        gp = new GamepadEx(gamepad1);
        telemetry.addData("status", "ready  A=manual  else=drive");
    }

    @Override
    public void loop() {
        gp.readButtons();

        if (gp.isDown(GamepadKeys.Button.A)) {
            // manually aim front module with left stick, spin left/right with triggers
            double angle = Math.toDegrees(Math.atan2(gamepad1.left_stick_x, -gamepad1.left_stick_y));
            double speed = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
            swerve.getFront().set(angle, speed, false);
            swerve.getLeft().set(0,  gamepad1.left_trigger,  false);
            swerve.getRight().set(0, gamepad1.right_trigger, false);
        } else {
            swerve.drive(gamepad1.left_stick_x, -gamepad1.left_stick_y,
                         gamepad1.right_stick_x, false);
        }

        telemetry.addData("heading", "%.1f°", swerve.getHeading());
        telemetry.addData("", "");
        telemetry.addData("front", "tgt=%.1f°  cur=%.1f°  spd=%.2f  pos=%d  vel=%.0f",
            swerve.getFront().getTargetAngleDeg(), swerve.getFront().getCurrentAngleDeg(),
            swerve.getFront().getTargetSpeed(), swerve.getFront().getDrivePosition(),
            swerve.getFront().getDriveVelocity());
        telemetry.addData("left",  "tgt=%.1f°  cur=%.1f°  spd=%.2f  pos=%d  vel=%.0f",
            swerve.getLeft().getTargetAngleDeg(),  swerve.getLeft().getCurrentAngleDeg(),
            swerve.getLeft().getTargetSpeed(),  swerve.getLeft().getDrivePosition(),
            swerve.getLeft().getDriveVelocity());
        telemetry.addData("right", "tgt=%.1f°  cur=%.1f°  spd=%.2f  pos=%d  vel=%.0f",
            swerve.getRight().getTargetAngleDeg(), swerve.getRight().getCurrentAngleDeg(),
            swerve.getRight().getTargetSpeed(), swerve.getRight().getDrivePosition(),
            swerve.getRight().getDriveVelocity());
        telemetry.update();
    }
}
