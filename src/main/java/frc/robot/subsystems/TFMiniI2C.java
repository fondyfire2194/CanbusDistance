package frc.robot.subsystems;

import java.util.TimerTask;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;
import java.nio.ByteBuffer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;

public class TFMiniI2C {
	private I2C i2c;
	protected final static int CMD = 0x80; // Most significant bit = 1
	private java.util.Timer updater;
	private int testCtr;
	private int retry_counter;
	int distanceCM;// actual distance measurements of LiDAR
	int signalStrength;// signal strength of LiDAR'
	int originalSignalQualityDegree;
	boolean check;
	private final int TFMINI_ADDR = 0x10;
	private final int TFMINI_CONFIG_REGISTER = 0x14;
	private final int TFMINI_DISTANCE_REGISTER = 0x8f;
	private byte[] buff = new byte[9];
	private byte sumBytes = 0;
	public ByteBuffer targetID = ByteBuffer.allocateDirect(8);

	public TFMiniI2C(Port port) {
		while (i2c == null && retry_counter++ < 10) {
			try {
				i2c = new I2C(port, TFMINI_ADDR);
			} catch (Exception e) {
				System.out.println("FAILED!!");
				SmartDashboard.putBoolean("I2CPort OK", false);
				e.printStackTrace();

				SmartDashboard.putNumber("Retry ", retry_counter);
			}
		}
		SmartDashboard.putBoolean("SUCCESSI2C", i2c != null);
		if (i2c == null) {
			DriverStation.reportError("Cannot open serial port to TFMini.", false);

			return;
		}
	}

	public boolean writeToTFMini() {
		byte[] sendstuff = { 0x01, 0x02, 0x07 };
		return i2c.writeBulk(sendstuff, 3);

	}
	public boolean readTFMini() {
		
		return i2c.readOnly(buff,7);

	}
}