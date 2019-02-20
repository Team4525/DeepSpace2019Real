package frc.robot.commands.autoCommands;

import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motion.TrajectoryPoint;

import frc.robot.auto.Command;
import frc.robot.Robot;

public class PathFollower implements Command {

    private BufferedTrajectoryPointStream left_bufferedStream = new BufferedTrajectoryPointStream();
    private BufferedTrajectoryPointStream right_bufferedStream = new BufferedTrajectoryPointStream();
    private double leftPath[][];
    private double rightPath[][];
    private int leftPoints;
    private int rightPoints;

    private boolean finished = false;
	private boolean stop = false;
	private boolean start = false;
    

    public PathFollower(double leftPath[][], double rightPath[][], int leftPoints, int rightPoints) {
        this.leftPath = leftPath;
        this.rightPath = rightPath;
        this.leftPoints = leftPoints;
        this.rightPoints = rightPoints;
    }

    @Override
    public void init() {
        initLeftBuffer(leftPath, leftPoints); // path , number of points
        initRightBuffer(rightPath, rightPoints);
    }

    public void execute () {
        Robot.drive.FollowPath(left_bufferedStream, right_bufferedStream);
        System.out.println("Im running");
    }

    @Override
    public void end() {
        Robot.drive.stop();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void stop() {
        stop = true;
    }

    @Override
    public boolean interupted() {
        return stop;
    }

    @Override
    public boolean started() {
        return start;
    }

    

    private void initLeftBuffer(double[][] profile, int totalCnt) {

        boolean forward = true; // set to false to drive in opposite direction of profile (not really needed
        TrajectoryPoint point = new TrajectoryPoint(); // temp for for loop, since unused params are initialized automatically, you can alloc just one
        /* clear the buffer, in case it was used elsewhere */
        left_bufferedStream.Clear();
        
        /* Insert every point into buffer, no limit on size */
        for (int i = 0; i < totalCnt; ++i) {

            double direction = forward ? +1 : -1;
            double positionRot = profile[i][0];
            double velocityRPM = profile[i][1];
            int durationMilliseconds = 10;

            /* for each point, fill our structure and pass it to API */
            point.timeDur = durationMilliseconds;
            point.position = direction * positionRot * 4096; // Convert Revolutions to
                                                                                          // Units
            point.velocity = direction * velocityRPM * 4096 / 600.0; // Convert RPM to
                                                                                                  // Units/100ms
            point.auxiliaryPos = 0;
            point.auxiliaryVel = 0;
            point.profileSlotSelect0 = 0; /* which set of gains would you like to use [0,3]? */
            point.profileSlotSelect1 = 0; /* auxiliary PID [0,1], leave zero */
            point.zeroPos = (i == 0); /* set this to true on the first point */
            point.isLastPoint = ((i + 1) == totalCnt); /* set this to true on the last point */
            point.arbFeedFwd = 0; /* you can add a constant offset to add to PID[0] output here */

            left_bufferedStream.Write(point);
        }
    }

    private void initRightBuffer(double[][] profile, int totalCnt) {

        boolean forward = true; // set to false to drive in opposite direction of profile (not really needed since you can use negative numbers in profile).
        TrajectoryPoint point = new TrajectoryPoint(); // temp for for loop, since unused params are initialized automatically, you can alloc just one
        /* clear the buffer, in case it was used elsewhere */
        right_bufferedStream.Clear();

        /* Insert every point into buffer, no limit on size */
        for (int i = 0; i < totalCnt; ++i) {

            double direction = forward ? +1 : -1;
            double positionRot = -profile[i][0];
            double velocityRPM = -profile[i][1];
            int durationMilliseconds = 10;

            /* for each point, fill our structure and pass it to API */
            point.timeDur = durationMilliseconds;
            point.position = direction * positionRot * 4096; // Convert Revolutions to
                                                                                          // Units
            point.velocity = direction * velocityRPM * 4096 / 600.0; // Convert RPM to
                                                                                                  // Units/100ms
            point.auxiliaryPos = 0;
            point.auxiliaryVel = 0;
            point.profileSlotSelect0 = 0; /* which set of gains would you like to use [0,3]? */
            point.profileSlotSelect1 = 0; /* auxiliary PID [0,1], leave zero */
            point.zeroPos = (i == 0); /* set this to true on the first point */
            point.isLastPoint = ((i + 1) == totalCnt); /* set this to true on the last point */
            point.arbFeedFwd = 0; /* you can add a constant offset to add to PID[0] output here */

            right_bufferedStream.Write(point);
        }
    }

    

    
}