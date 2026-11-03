package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class turret_angle_rotate_thingy {
        DcMotorEx angler;
        public turret_angle_rotate_thingy(HardwareMap hardwareMap) {
            //todo: you know, right?
            angler = hardwareMap.get(DcMotorEx.class, "angler");
        }
        public void rotate(double rotation) {
            this.angler.setPower(0.01*rotation);
        }
}
