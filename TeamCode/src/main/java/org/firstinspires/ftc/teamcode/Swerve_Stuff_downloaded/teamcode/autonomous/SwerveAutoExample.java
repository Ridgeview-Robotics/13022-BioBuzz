package org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.autonomous;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.RunCommand;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.subsystems.SwerveDrivetrain;

// command-based auto template, TODO: REPLACE WITH RR
@Autonomous(name = "swerve auto example", group = "Autonomous")
public class SwerveAutoExample extends CommandOpMode {
    private SwerveDrivetrain swerve;

    @Override
    public void initialize() {
        swerve = new SwerveDrivetrain(hardwareMap);
        register(swerve);

        schedule(
            new RunCommand(() -> swerve.drive(0, 0.5, 0, false), swerve)
                .withTimeout(2000)
                .andThen(new RunCommand(() -> swerve.drive(0, 0, 0.5, false), swerve)
                .withTimeout(1000))
                .andThen(new InstantCommand(swerve::stop, swerve))
        );
    }
}
