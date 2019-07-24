/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.subsystems.CANSendReceive;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * 
 */

public class CanbusDistanceSensor extends SendableBase implements Sendable {
  // Message IDs

  private static final int HEARTBEAT_MESSAGE = 0x18F0FF00;
  private static final int CALIBRATION_STATE_MESSAGE = 0x0CF91200;
  private static final int MEASURED_DISTANCE_MESSAGE = 0x0CF91000;
  private static final int MEASUREMENT_QUALITY_MESSAGE = 0x0CF91100;
  private static final int RANGING_CONFIGURATION_MESSAGE = 0x0CF91300;
  private static final int DEVICE_CONFIGURATION_MESSAGE = 0x0CAAFFF9;
  private static final int kSendMessagePeriod = 0;
  // LiveWindow color update MS maximum interval (milliseconds)
  protected final static int LIVE_WINDOW_UPDATE_INTERVAL = 50;

  private static double lastDistance = 0;
  // public static byte[] hwdata = new byte[8];
  private double serialNumber;
  private double partNumber;
  private double firmWare;

  public CanbusDistanceSensor() {

    // LiveWindow.add(this);

  }

  // Messages from device
  public static byte[] readHeartbeat(int id) {
    byte[] hwdata = null;
    ;
    long read = CANSendReceive.readMessage(HEARTBEAT_MESSAGE, id);

    if (read != -1)
      hwdata = CANSendReceive.result;

    return hwdata;
  }

  public static double[] getSensorInfo(byte[] hwdata) {
    double[] temp = new double[3];
    temp[0] = extractValue(hwdata, 3, 1);
    temp[1] = extractValue(hwdata, 5, 4);
    temp[2] = extractValue(hwdata, 7, 6);
    return temp;
  }

  public static double getDistanceMM(int id) {
    long read = CANSendReceive.readMessage(MEASURED_DISTANCE_MESSAGE, id);

    if (read == -1)
      return lastDistance;
    else {
      int rangingStatus = Byte.toUnsignedInt(CANSendReceive.result[2]);
      SmartDashboard.putNumber("RS", rangingStatus);
      if (rangingStatus != 0) {
        return (double) -rangingStatus;
      } else {
        lastDistance = extractValue(CANSendReceive.result, 1, 0);

        return lastDistance;
      }
    }
  }

  public static double[] readQuality(int id) {
    double temp[] = { 0, 0 };
    long read = CANSendReceive.readMessage(MEASUREMENT_QUALITY_MESSAGE, id);
    if (read != -1) {
      temp[0] = extractValue(CANSendReceive.result, 3, 0) / 65536;
      temp[1] = extractValue(CANSendReceive.result, 7, 4) / 65536;
    }
    return temp;
  }

  public static double[] readCalibrationState(int id) {
    double temp[] = { 0, 0, 0, 0, 0, 0, 0 };
    long read = CANSendReceive.readMessage(CALIBRATION_STATE_MESSAGE, id);

    if (read != -1) {
      temp[2] = extractValue(CANSendReceive.result, 2, 1);
      temp[0] = CANSendReceive.result[0] & 0b00001111;
      temp[1] = CANSendReceive.result[0] >> 4;
    }

    return temp;

  }

  // // Messages to device
  public static void configureRange(int id, int mode) {
    byte[] data = new byte[1];
    data[0] = (byte) mode;
    CANSendReceive.sendMessage(RANGING_CONFIGURATION_MESSAGE | id, data, 1, kSendMessagePeriod);

  }

  public static void identifyDevice(int id) {
    byte[] hwdata = new byte[8];

    hwdata = readHeartbeat(id);

    hwdata[0] = 0x0D;
    CANSendReceive.sendMessage(DEVICE_CONFIGURATION_MESSAGE, hwdata, 6, kSendMessagePeriod);
  }

  public static void configureDevice(int oldID, int newID) {
    byte[] hwdata = new byte[8];
    double temp[] = new double[3];
    if (newID >= 0 && newID < 33) {
      hwdata = readHeartbeat(oldID);
      temp = getSensorInfo(hwdata);
      SmartDashboard.putNumber("Cdat", temp[0]);
      hwdata[0] = 0x0C;
      hwdata[6] = (byte) newID;
      CANSendReceive.sendMessage(DEVICE_CONFIGURATION_MESSAGE, hwdata, 7, kSendMessagePeriod);
    }
  }

  static double extractValue(byte[] src, int high, int low) {
    double temp = src[high] * 256;
    int i = 0;
    for (i = high - 1; i > low; i--) {
      temp = 256 * (temp + (double) src[i]);
    }
    return temp + src[i];
  }

  // https://www.chiefdelphi.com/t/creating-custom-smartdashboard-types-like-pidcommand/162737/8
  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("ColorProx");
    // builder.addDoubleProperty("Range Offset", () -> readCalibrationState()[2],
    // null);
    // builder.addDoubleProperty("X Position", () -> readCalibrationState()[0],
    // null);
    // builder.addDoubleProperty("Y Position", () -> readCalibrationState()[1],
    // null);

    builder.addDoubleProperty("Serial Number", () -> serialNumber, null);
    builder.addDoubleProperty("Part Number", () -> (double) partNumber, null);
    builder.addDoubleProperty("Firmware", () -> (double) firmWare, null);

    // builder.addDoubleProperty("Distance MM", () -> getDistanceMM(), null);
    // builder.addDoubleProperty("Distance Inch", () -> getDistanceMM() / 25.4,
    // null);
    // builder.addDoubleProperty("Ambient Light", () -> readQuality()[0], null);
    // builder.addDoubleProperty("Std Dev", () -> readQuality()[1], null);

  }

}
