package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.util.ElapsedTime;

public class SlewRateLimiter {
    private double lastValue = 0;
    private final ElapsedTime timer = new ElapsedTime();
    private boolean firstRun = true;

    public double calculate(double target, double accel, double decel) {
        double dt = timer.seconds();
        timer.reset();

        if (firstRun || dt > 0.2) {
            firstRun = false;
            return lastValue = target;
        }

        double rate = (Math.abs(target) > Math.abs(lastValue)) ? accel : decel;
        double maxStep = rate * dt;
        double error = target - lastValue;

        if (Math.abs(error) > maxStep) lastValue += Math.signum(error) * maxStep;
        else lastValue = target;

        return lastValue;
    }

    public void reset() { firstRun = true; }
}
