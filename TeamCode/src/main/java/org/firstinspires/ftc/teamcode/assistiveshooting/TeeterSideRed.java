package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "RED - TeeterSide", group = "Assistive Shooting")
public class TeeterSideRed extends TeeterSideOpMode {

    @Override
    protected TeeterSide.AllianceColor getAllianceColor() {
        return TeeterSide.AllianceColor.RED;
    }
}