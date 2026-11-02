package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class turret_type {
    DcMotorEx turret;
    public turret_type(HardwareMap hardwareMap) {
        //todo: speaketh its true name, and it shall be created! (and not throw an error, but that's just a byproduct).
        turret = hardwareMap.get(DcMotorEx.class, "turret");
    }
    public void set_turret(double power) {
        this.turret.setPower(power);
    }
}
