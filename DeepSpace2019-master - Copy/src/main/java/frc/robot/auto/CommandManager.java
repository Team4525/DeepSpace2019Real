package frc.robot.auto;

import java.util.ArrayList;

import frc.robot.util.DashUtil;


public class CommandManager {

	private boolean active = false;

	private static CommandManager instance = new CommandManager();

	public static CommandManager getInstance() {
		return instance;
	}

	private ArrayList<Command> commands = new ArrayList<Command>();
	private ArrayList<Boolean> parallels = new ArrayList<Boolean>(); // a list
																		// of
																		// booleans
																		// stating
																		// whether
																		// its a
																		// parallel
																		// command
																		// or
																		// sequential

	// Que's a command which runs and then starts the next set of things when
	// its
	// done
	public Command queSequential(Command cmd) {
		commands.add(cmd);
		parallels.add(false);
		return cmd;
	}

	// Que's a parallel command which runs periodically with what ever
	// sequential/parallel commands are active
	public Command queParallel(Command cmd) {
		commands.add(cmd);
		parallels.add(true);
		return cmd;
	}

	public synchronized void start() {
		if (!active) {
			active = true;
			new Thread(new Runnable() {

				public void run() {
					Command sequential = null;
					ArrayList<Command> running_parallels = new ArrayList<Command>();
					// Command loop
					while (active) {
						if (sequential == null) {
							if (!commands.isEmpty()) {
								if (parallels.get(0)) {
									running_parallels.add(commands.get(0));
									commands.remove(0);
									parallels.remove(0);
								} else {
									sequential = commands.get(0);
									commands.remove(0);
									parallels.remove(0);
								}
							}

						} else { // Runs the actual commands
							if (!sequential.started()) {
								sequential.init();
								DashUtil.getInstance().log("Starting sequential " + sequential.toString());
							} else if (sequential.isFinished() || sequential.interupted()) {
								DashUtil.getInstance().log("Stopping sequential " + sequential.toString());
								sequential.end();
								sequential = null;

							} else {
								sequential.execute();
							}
						}
						for (Command cmd : running_parallels) {
							if (!cmd.started()) {
								cmd.init();
								DashUtil.getInstance().log("Starting parallel " + cmd.toString());
							} else if (cmd.isFinished() || cmd.interupted()) {
								DashUtil.getInstance().log("Stopping Parallel, " + cmd.toString());
								cmd.end();
								running_parallels.remove(cmd);
							} else {
								cmd.execute();
							}
						}
					}
					DashUtil.getInstance().log("Autonomous commands stopped.");
					for (Command cmd : running_parallels) {
						cmd.stop();
						cmd.end();
					}
					running_parallels.clear();
					sequential.stop();
					sequential.end();
					sequential = null;
				}

			}).start();
		}
	}

	// Stop the command loop
	public void stop() {
		active = false;

	}

	private CommandManager() {
		DashUtil.getInstance().log("Autonomous Command manager ready.");
	}

}