package org.firstinspires.ftc.teamcode.assistiveshooting;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "BLUE - TeeterSide", group = "Assistive Shooting")
public class TeeterSideBlue extends TeeterSideOpMode {

    @Override
    protected TeeterSide.AllianceColor getAllianceColor() {
        return TeeterSide.AllianceColor.BLUE;
    }
}