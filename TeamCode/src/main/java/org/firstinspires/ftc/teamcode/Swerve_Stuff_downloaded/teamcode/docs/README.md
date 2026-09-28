# swerve-ftclib

3-module differential swerve drivetrain for FTC, built on FTCLib.

## hardware

- 3× Gobilda 5203 drive motors (DcMotorEx, RUN_USING_ENCODER)
- 3× Axon Max steering servos (CRServo, continuous rotation)
- 3× Analog absolute encoders (AnalogInput, 0–3.3V)
- Rev Hub IMU

## quick start

1. Set hardware names, offsets, and directions in `Config/Constants.java`
2. Set IMU orientation (`LOGO`, `USB`) in `Constants.java`
3. Run **imu test** — verify yaw increases CCW
4. Run **module calibration** — set `*_OFF_DEG` offsets
5. Run **swerve debug** — verify kinematics
6. Drive with **main tele**

## controls

| input | action |
|---|---|
| left stick | translate |
| right stick | rotate |
| bumpers | precision mode |
| PS button | toggle field/robot centric |
| X | reset yaw |
| B | toggle defense mode |
| D-pad | snap to cardinal heading |

## tuning (FTC Dashboard `192.168.43.1:8080/dash`)

- **SwerveConfig** — heading PID, snap angles, passive align, speed
- **TeleOpConfig** — drive curve, slew rates, precision scales

## structure

```
Config/
  Constants.java       # hardware names, offsets, directions, dimensions
  SwerveConfig.java    # heading PID, features (dashboard-tunable)
  TeleOpConfig.java    # input curves, slew rates (dashboard-tunable)
subsystems/
  SwerveModule.java    # single module: steer P-controller + drive
  SwerveDrivetrain.java # 3-module kinematics, FTCLib SubsystemBase
TeleOp/
  TeleOpMain.java      # CommandOpMode: GamepadEx, PIDFController, heading hold
  SwerveDebugOpMode.java
  ModuleCalibrationOpMode.java
  IMUTestOpMode.java
autonomous/
  SwerveAutoExample.java
helpers/
  Curve.java
  SlewRateLimiter.java
docs/
  IMPLEMENTATION.md    # kinematics, setup, tuning details
```

## docs

See `docs/IMPLEMENTATION.md` for kinematics math, calibration procedure, and tuning guide.
