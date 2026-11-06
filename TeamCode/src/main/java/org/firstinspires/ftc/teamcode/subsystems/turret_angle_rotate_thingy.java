package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class turret_angle_rotate_thingy {
        CRServo angler;
        public turret_angle_rotate_thingy(HardwareMap hardwareMap) {
            //todo: you know, right?
            angler = hardwareMap.get(CRServo.class, "angler");
        }
        public void rotate(double rotation) {
            this.angler.setPower(0.01*rotation);
        }
}
