package frc.robot.subsystems;

import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.commands.DriveCommand;

public class Drive extends Subsystem {
    TalonSRX leftTalon = new TalonSRX(1);
    VictorSPX leftVictor = new VictorSPX(1);
    TalonSRX rightTalon = new TalonSRX(2);
    VictorSPX rightVictor = new VictorSPX(2);
    Ultrasonic sonic = new Ultrasonic(2, 1);

    public Drive() { 

        sonic.setAutomaticMode(true);
       
        leftVictor.follow(leftTalon);
        rightVictor.follow(rightTalon);
        /* Factory Default all hardware to prevent unexpected behaviour */
        leftTalon.configFactoryDefault();

        /* Config sensor used for Primary PID [Velocity] */
        leftTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);
        leftTalon.setSensorPhase(true);

        /* Config the peak and nominal outputs */
        leftTalon.configNominalOutputForward(0, 30);
        leftTalon.configNominalOutputReverse(0, 30);
        leftTalon.configPeakOutputForward(1, 30);
        leftTalon.configPeakOutputReverse(-1, 30);

        /* Config the Velocity closed loop gains in slot0 */
        leftTalon.config_kF(0, 0, 30);
        leftTalon.config_kP(0, 0, 30);
        leftTalon.config_kI(0, 0, 30);
        leftTalon.config_kD(0, 0, 30);

        rightTalon.configFactoryDefault();

        /* Config sensor used for Primary PID [Velocity] */
        rightTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

        /**
         * Phase sensor accordingly. Positive Sensor Reading should match Green
         * (blinking) Leds on Talon
         */
        rightTalon.setSensorPhase(true);

        /* Config the peak and nominal outputs */
        rightTalon.configNominalOutputForward(0, 30);
        rightTalon.configNominalOutputReverse(0, 30);
        rightTalon.configPeakOutputForward(1, 30);
        rightTalon.configPeakOutputReverse(-1, 30);

        /* Config the Velocity closed loop gains in slot0 */
        rightTalon.config_kF(0, 0, 30);
        rightTalon.config_kP(0, 0, 30);
        rightTalon.config_kI(0, 0, 30);
        rightTalon.config_kD(0, 0, 30);

        leftTalon.setNeutralMode(NeutralMode.Brake);
        rightTalon.setNeutralMode(NeutralMode.Brake);
    }
    
    public void drive(double targetVelocity, double targetOffset) {
        
            /* Velocity Closed Loop */

            /**
             * Convert 500 RPM to units / 100ms. 4096 Units/Rev * 500 RPM / 600 100ms/min in
             * either direction: velocity setpoint is in units/100ms
             */
            double targetVelocity_UnitsPer100ms = targetVelocity;
            double TargetTurnVelocity = targetOffset;

            double maxInput = Math.copySign(Math.max(Math.abs(targetVelocity_UnitsPer100ms), Math.abs(TargetTurnVelocity)),targetVelocity_UnitsPer100ms);

            if (targetVelocity_UnitsPer100ms >= 0.0) {
                if (TargetTurnVelocity >= 0.0) {
                    leftTalon.set(ControlMode.Velocity, maxInput);
                    rightTalon.set(ControlMode.Velocity, -(targetVelocity_UnitsPer100ms - TargetTurnVelocity));
                } else {
                    leftTalon.set(ControlMode.Velocity, targetVelocity_UnitsPer100ms + TargetTurnVelocity);
                    rightTalon.set(ControlMode.Velocity, -maxInput);
                }
            } else {
                if (TargetTurnVelocity >= 0.0) {
                    leftTalon.set(ControlMode.Velocity, targetVelocity_UnitsPer100ms + TargetTurnVelocity);
                    rightTalon.set(ControlMode.Velocity, -maxInput);
                } else {
                    leftTalon.set(ControlMode.Velocity, maxInput);
                    rightTalon.set(ControlMode.Velocity, -(targetVelocity_UnitsPer100ms - TargetTurnVelocity));
                }
            }
    }

    public void FollowPath(BufferedTrajectoryPointStream left_bufferedStream, BufferedTrajectoryPointStream right_bufferedStream) {
        leftTalon.startMotionProfile(left_bufferedStream, 10, ControlMode.MotionProfile);
        rightTalon.startMotionProfile(right_bufferedStream, 10, ControlMode.MotionProfile);
    }

    public boolean pathIsFinished() {
        if (leftTalon.isMotionProfileFinished() && rightTalon.isMotionProfileFinished()) {
            return true;
        } else {
            return false;
        }
    }

    public void setLeft(double output) {
        leftTalon.set(ControlMode.PercentOutput, output);
    }

    public void setRight(double output) {
        rightTalon.set(ControlMode.PercentOutput, output);
    }

    public void stop() {
        leftTalon.set(ControlMode.PercentOutput, 0);
        rightTalon.set(ControlMode.PercentOutput, 0);
    }

    public double getProximity() {
        return sonic.getRangeInches();
    }

    public void initDefaultCommand() {

        setDefaultCommand(new DriveCommand());
    }

}