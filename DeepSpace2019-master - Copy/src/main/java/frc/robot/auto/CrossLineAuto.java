package frc.robot.auto;

import frc.robot.auto.paths.CrossLine;
import frc.robot.commands.autoCommands.PathFollower;
import frc.robot.util.Controller;;

public class CrossLineAuto implements Controller {

    private CommandManager commands = CommandManager.getInstance();

    @Override
    public void start() {
        commands.queSequential(new PathFollower(CrossLine.LeftPath, CrossLine.RightPath, CrossLine.leftPoints, CrossLine.rightPoints));
    }

    @Override
    public void stop() {

    }
}