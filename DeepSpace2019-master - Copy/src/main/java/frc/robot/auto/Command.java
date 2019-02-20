package frc.robot.auto;

public interface Command {

	public void init();

	public void execute();

	public boolean isFinished();

	public void end();

	//
	public void stop();

	//
	public boolean interupted();

	public boolean started();
}