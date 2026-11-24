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
    private LiftPosition currentLiftPosition = LiftPosition.UNKNOWN;
    private LiftPosition wantedLiftPosition = LiftPosition.DOWN;

    // BUILDER
    public VertLift(HardwareMap hardwareMap) {
        leftMotor = hardwareMap.get(DcMotor.class, "leftLift");
        rightMotor = hardwareMap.get(DcMotor.class, "rightLift");

        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setTargetPosition(0);
        leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftMotor.setPower(0.5);

        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setTargetPosition(0);
        rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightMotor.setPower(0.5);
    }

    // ---- setter: remembers the wanted position AND sends the new target ----
    public void setWantedPosition(LiftPosition p) {
        if (p == LiftPosition.UNKNOWN) return;      // UNKNOWN is never a valid goal
        wantedLiftPosition = p;
        leftMotor.setTargetPosition(p.getTarget());
        rightMotor.setTargetPosition(p.getTarget());
    }

    // ---- getters ----
    public LiftPosition getCurrentPosition() { return currentLiftPosition; }
    public int getLeftPos() { return leftMotor.getCurrentPosition(); }
    public int getRightPos() { return rightMotor.getCurrentPosition(); }

    // ---- called once per loop from the OpMode ----
    public void update() {
        // PART 1: is the lift inside the wanted position's range?
        //   - read left and right with getLeftPos() / getRightPos()
        //   - range is wantedLiftPosition.getMin() to .getMax()
        //   - boolean inRange = (left in range) && (right in range);

        // PART 2: if NOT inRange, send the target again
        //   - same two setTargetPosition lines as the setter, using
        //     wantedLiftPosition.getTarget()
        //   - this is the "got pushed out of place" correction

        // PART 3: work out where the lift actually is
        //   - set currentLiftPosition = UNKNOWN first
        //   - loop over LiftPosition.values(), skip UNKNOWN
        //   - if both encoders are in that value's range,
        //     set currentLiftPosition to it
    }
}