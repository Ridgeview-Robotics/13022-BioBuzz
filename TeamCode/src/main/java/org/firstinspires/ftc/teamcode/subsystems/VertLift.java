package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class VertLift {
    public enum LiftPosition {
        DOWN(-25, 25),               //min,max
        FLOWER(475, 525),            //min,max
        BLOCKING(775, 825),          //min,max
        UNKNOWN(0, 0);               //placeholder, never commanded

        private final int min;
        private final int max;

        LiftPosition(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() { return min; }
        public int getMax() { return max; }
        public int getTarget() { return (min + max) / 2; }
    }

    // ---- data ----
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private PIDController pid;
    private int targetTicks;
    private double kG = 0;
    private double maxPower = 0.4;
    private LiftPosition currentLiftPosition = LiftPosition.UNKNOWN;
    private LiftPosition wantedLiftPosition = LiftPosition.DOWN;

    // ---- constructor ----
    public VertLift(HardwareMap hardwareMap) {
        leftMotor = hardwareMap.get(DcMotor.class, "leftLift");
        rightMotor = hardwareMap.get(DcMotor.class, "rightLift");

        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pid = new PIDController(0.005, 0, 0);   // kP, kI, kD starting guesses
    }

    // ---- setter ----
    public void setWantedPosition(LiftPosition p) {
        if (p == LiftPosition.UNKNOWN) return;
        wantedLiftPosition = p;
        targetTicks = p.getTarget();
        pid.reset();
    }

    // ---- getters ----
    public LiftPosition getCurrentPosition() { return currentLiftPosition; }
    public int getLeftPos() { return leftMotor.getCurrentPosition(); }

    // ---- called once per loop from the OpMode ----
    public void update() {
        double power = pid.calculate(targetTicks,getLeftPos()) + kG;
        power = Math.max(-maxPower, Math.min(maxPower, power));

        leftMotor.setPower(power);
        rightMotor.setPower(power);

        int pos = getLeftPos();

        currentLiftPosition = LiftPosition.UNKNOWN;
        for (LiftPosition p : LiftPosition.values()) {
            if (p == LiftPosition.UNKNOWN) {
                continue;
            }
            if (pos >= p.getMin() && pos <= p.getMax()){
                currentLiftPosition = p;
            }

        }
        // SLOT 5: the for loop over LiftPosition.values()
        //         inside it: skip UNKNOWN, then the range test (the && line)
        //         inside the range test: set currentLiftPosition
    }
}