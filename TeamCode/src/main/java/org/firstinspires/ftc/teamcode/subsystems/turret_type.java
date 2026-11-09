package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class turret_type {
    DcMotorEx turret_pollen;
    DcMotorEx turret_nectar;
    public turret_type(HardwareMap hardwareMap) {
        //todo: speaketh its true name, and it shall be created! (and not throw an error, but that's just a byproduct).
        turret_pollen = hardwareMap.get(DcMotorEx.class, "turret_pollen");
        turret_nectar = hardwareMap.get(DcMotorEx.class, "turret_nectar");
    }
    public void set_turret_pollen(double power) {
        this.turret_pollen.setPower(power);
    }

    public void set_turret_nectar(double power) {
        this.turret_nectar.setPower(power);
    }
}
