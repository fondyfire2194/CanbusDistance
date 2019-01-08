package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SD {

	public SD() {

	}

	public static void putN(String name, double value) {

		SmartDashboard.putNumber(name, value);
	}

	public static void putN0(String name, double value) {

		SmartDashboard.putNumber(name, Math.round(value));
	}

	public static void putN1(String name, double value) {

		SmartDashboard.putNumber(name, Math.round(value * 10.) / 10.);
	}

	public static void putN2(String name, double value) {

		SmartDashboard.putNumber(name, Math.round(value * 100.) / 100.);
	}

	public static void putN3(String name, double value) {

		SmartDashboard.putNumber(name, Math.round(value * 1000.) / 1000.);
	}

	public static void putN4(String name, double value) {

		SmartDashboard.putNumber(name, Math.round(value * 10000.) / 10000.);
	}

}
