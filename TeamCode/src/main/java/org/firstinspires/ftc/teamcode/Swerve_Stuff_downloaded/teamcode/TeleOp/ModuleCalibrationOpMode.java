package org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.Swerve_Stuff_downloaded.teamcode.Config.Constants;

@TeleOp(name="module calibration", group="Calibration")
public class ModuleCalibrationOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        telemetry.addData("status", "calibration mode");
        telemetry.addData("", "align modules forward, then press start");
        telemetry.update();
        waitForStart();

        DcMotorEx frontDrive = hardwareMap.get(DcMotorEx.class, Constants.FRONT_DRIVE);
        DcMotorEx leftDrive  = hardwareMap.get(DcMotorEx.class, Constants.LEFT_DRIVE);
        DcMotorEx rightDrive = hardwareMap.get(DcMotorEx.class, Constants.RIGHT_DRIVE);
        CRServo frontSteer   = hardwareMap.get(CRServo.class, Constants.FRONT_STEER);
        CRServo leftSteer    = hardwareMap.get(CRServo.class, Constants.LEFT_STEER);
        CRServo rightSteer   = hardwareMap.get(CRServo.class, Constants.RIGHT_STEER);

        int selectedModule = 0;
        double[] servoPowers = {0, 0, 0};

        while (opModeIsActive()) {
            if (gamepad1.dpad_up   && selectedModule > 0) { selectedModule--; sleep(200); }
            if (gamepad1.dpad_down && selectedModule < 2) { selectedModule++; sleep(200); }

            double adj = 0;
            if (gamepad1.dpad_left)    adj = -0.01;
            if (gamepad1.dpad_right)   adj =  0.01;
            if (gamepad1.left_bumper)  adj = -0.002;
            if (gamepad1.right_bumper) adj =  0.002;

            servoPowers[selectedModule] = Math.max(-1, Math.min(1, servoPowers[selectedModule] + adj));

            frontSteer.setPower(servoPowers[0]);
            leftSteer.setPower(servoPowers[1]);
            rightSteer.setPower(servoPowers[2]);

            double drivePow = gamepad1.a ? 0.3 : 0;
            frontDrive.setPower(selectedModule == 0 ? drivePow : 0);
            leftDrive.setPower(selectedModule  == 1 ? drivePow : 0);
            rightDrive.setPower(selectedModule == 2 ? drivePow : 0);

            String[] names = {"FRONT", "LEFT", "RIGHT"};
            telemetry.addData("module", names[selectedModule]);
            telemetry.addData("front pow", "%.4f", servoPowers[0]);
            telemetry.addData("left pow",  "%.4f", servoPowers[1]);
            telemetry.addData("right pow", "%.4f", servoPowers[2]);
            telemetry.addData("", "dpad up/dn=select  L/R=coarse  bumpers=fine  A=drive");
            telemetry.update();
        }
    }
}
