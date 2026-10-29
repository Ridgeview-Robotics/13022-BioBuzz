package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class turret_type {
    DcMotorEx turret;
    public turret_type(HardwareMap hardwareMap) {
        turret = hardwareMap.get(DcMotorEx.class, "turret");
    }
    public void set_turret(double power) {
        this.turret.setPower(power);
    }
}
