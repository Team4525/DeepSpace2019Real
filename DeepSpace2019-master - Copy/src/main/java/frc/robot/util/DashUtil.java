package frc.robot.util;

import java.util.*;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DashUtil {

	private final String KEY_LOG = "Robot_Log";
	private String[] logMsgs = new String[10];
	private static DashUtil instance = new DashUtil();

	/***
	 * Utility Pattern!
	 */
	private DashUtil() {
		for (int i = 0; i < logMsgs.length; i++) {
			logMsgs[i] = "";
		}
	}

	public static DashUtil getInstance() {
		return instance;
	}

	public static void put(String lable, String value) {
		SmartDashboard.putString(lable, value);
	}

	public synchronized void log(String msg) {
		shiftLog();
		logMsgs[0] = msg;
		for (int i = 0; i < logMsgs.length; i++) {
			SmartDashboard.putString(KEY_LOG + i, logMsgs[i]);
		}
	}

	public synchronized void log(Hashtable ht) {
		Enumeration enu = ht.keys();
		while (enu.hasMoreElements()) {
			Object key = enu.nextElement();
			Object value = ht.get(key);
			SmartDashboard.putString(key + "", "" + value);
		}
	}

	private void shiftLog() {
		for (int i = logMsgs.length - 1; i > 0; i--) {
			logMsgs[i] = logMsgs[i - 1];
		}
	}

	public static void error(String msg) {
		DriverStation.reportError(msg, false);
	}

	public void clear() {
		for (int i = 0; i < logMsgs.length; i++) {
			logMsgs[i] = "";
		}
	}

}