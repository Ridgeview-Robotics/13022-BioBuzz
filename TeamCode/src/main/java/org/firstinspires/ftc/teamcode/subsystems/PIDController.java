package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    private double kP;
    private double kI;
    private double kD;
    private double integralSum;
    private double lastError;
    private ElapsedTime timer;

    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        timer = new ElapsedTime();
    }

    public double calculate(double target, double current) {
        double error = target - current;
        double dt = timer.seconds();
        if (dt <= 0) {
            return 0;
        }
        timer.reset();
        integralSum += error * dt;
        double derivative = (error - lastError) / dt;
        lastError = error;
        return kP * error + kI * integralSum + kD * derivative;
    }

    public void reset() {
        integralSum = 0;
        lastError = 0;
        timer.reset();
    }
}