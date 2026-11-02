package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    DcMotorEx intake;
    public Intake(HardwareMap hardwareMap) {
        //tOdO: SET THIS TO REAL NAME
        intake = hardwareMap.get(DcMotorEx.class, "intake");
    }
    public void set_intake(double power) {
        this.intake.setPower(power);
    }
}