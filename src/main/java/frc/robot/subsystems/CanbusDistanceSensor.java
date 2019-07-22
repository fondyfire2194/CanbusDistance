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
  public final int DEVICE_CONFIGURATION_MESSAGE = 0x0CAAFFF9;
  private static final int kSendMessagePeriod = 0;
  // LiveWindow color update MS maximum interval (milliseconds)
  protected final static int LIVE_WINDOW_UPDATE_INTERVAL = 50;

  private int myId;
  public CANSendReceive canSendReceive;
  private double lastDistance = 0;
  public byte[] hwdata = new byte[7];
  private double serialNumber;
  private double partNumber;

  public CanbusDistanceSensor(int deviceID) {
    myId = deviceID;
    canSendReceive = new CANSendReceive();
    LiveWindow.add(this);
    setName("Distance", "Can Dist " + String.valueOf(myId));
    readHeartbeat();
  }

  // Messages from device
  public long readHeartbeat() {
    long read = canSendReceive.readMessage(HEARTBEAT_MESSAGE, myId);
    serialNumber = extractValue(canSendReceive.result, 3, 1);
    partNumber = extractValue(canSendReceive.result, 5, 4);

    for (int i = 1; i <= 5; i++) {
      hwdata[i] = canSendReceive.result[i];
    }
    SmartDashboard.putNumber("SNumber", serialNumber);
    SmartDashboard.putNumber("PNumber", partNumber);

    return read;
  }

  public double getDistanceMM() {
    long read = canSendReceive.readMessage(MEASURED_DISTANCE_MESSAGE, myId);
    if (read == -1)
      return lastDistance;
    else {
      int rangingStatus = Byte.toUnsignedInt(canSendReceive.result[2]);
      if (rangingStatus != 0) {
        return (double) -rangingStatus;
      } else {
        lastDistance = extractValue(canSendReceive.result, 1, 0);

        return lastDistance;
      }
    }
  }

  public double[] readQuality() {
    double temp[] = { 0, 0 };
    long read = canSendReceive.readMessage(MEASUREMENT_QUALITY_MESSAGE, myId);
    temp[0] = extractValue(canSendReceive.result, 3, 0) / 65536;
    temp[1] = extractValue(canSendReceive.result, 7, 4) / 65536;
    return temp;
  }

  // public double[] readCalibrationState() {
  // double temp[] = { 0, 0, 0 ,0,0,0,0};
  // long read = canSendReceive.readMessage(CALIBRATION_STATE_MESSAGE, myId);
  // SmartDashboard.putNumber("Bytes", canSendReceive.result.length);
  // SmartDashboard.putNumber("read", read);
  // if (read != -1) {
  // temp[2] = extractValue(canSendReceive.result, 2, 1);
  // temp[0] = canSendReceive.result[0] & 0b00001111;
  // temp[1] = canSendReceive.result[0] >> 4;
  // }

  // return temp;

  // }

  // // Messages to device
  public void configureRange(int mode) {
    byte[] data = new byte[7];
    switch (mode) {
    case 0:// short
      data[0] = 0x0;
      break;
    case 1:// medium
      data[0] = 0x01;
      break;
    case 2:// long
      data[0] = 0x02;
      break;
    default:
      data[0] = 0x0;
      break;
    }
    canSendReceive.sendMessage(RANGING_CONFIGURATION_MESSAGE | myId, data, 1, kSendMessagePeriod);
  }

  public void identifyDevice(int apiId) {
    hwdata[0] = 0x0D;
    canSendReceive.sendMessage(DEVICE_CONFIGURATION_MESSAGE | apiId, hwdata, 6, kSendMessagePeriod);
  }

  public void configureDevice(int newID) {
    if (newID > 0 && newID < 33) {
      hwdata[0] = 0x0C;
      hwdata[6] = (byte) newID;
      canSendReceive.sendMessage(DEVICE_CONFIGURATION_MESSAGE | myId, hwdata, 7, kSendMessagePeriod);
    }
  }

  double extractValue(byte[] src, int high, int low) {
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
    builder.addDoubleProperty("Distance MM", () -> getDistanceMM(), null);
    builder.addDoubleProperty("Distance Inch", () -> getDistanceMM() / 25.4, null);
    builder.addDoubleProperty("Ambient Light", () -> readQuality()[0], null);
    builder.addDoubleProperty("Std Dev", () -> readQuality()[1], null);

  }

}
