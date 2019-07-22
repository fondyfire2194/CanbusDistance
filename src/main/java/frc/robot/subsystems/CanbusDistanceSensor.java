/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.subsystems.CANSendReceive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * 
 */

public class CanbusDistanceSensor {
  // Message IDs

  private static final int HEARTBEAT_MESSAGE = 0x18F0FF00;
  private static final int CALIBRATION_STATE_MESSAGE = 0x0CF91200;
  private static final int MEASURED_DISTANCE_MESSAGE = 0x0CF91000;
  private static final int MEASUREMENT_QUALITY_MESSAGE = 0x0CF91100;
  private static final int RANGING_CONFIGURATION_MESSAGE = 0x0CF91300;
  public final int DEVICE_CONFIGURATION_MESSAGE = 0x0CAAFFF9;
  private static final int kSendMessagePeriod = 0;
  private int myId;
  public CANSendReceive canSendReceive;

  public byte[] hwdata = new byte[6];

  public static enum DU {
    Mm, M, In, Ft
  };

  public CanbusDistanceSensor(int deviceID) {
    myId = deviceID;
    canSendReceive = new CANSendReceive();
  }

  // Messages from device
  public long readHeartbeat() {
    long read = canSendReceive.readMessage(HEARTBEAT_MESSAGE, myId);
    int serialNumber1 = 256 * (256 * canSendReceive.result[3] + canSendReceive.result[2]) + canSendReceive.result[1];
    int partNumber1 = 256 * canSendReceive.result[5] + canSendReceive.result[4];

    hwdata[1] = canSendReceive.result[1];
    hwdata[2] = canSendReceive.result[2];
    hwdata[3] = canSendReceive.result[3];
    hwdata[4] = canSendReceive.result[4];
    hwdata[5] = canSendReceive.result[5];
    SmartDashboard.putNumber("SerialNumber1", serialNumber1);
    SmartDashboard.putNumber("PartNumber1", partNumber1);

    return read;
  }

  public double getDistance(DU du) {

    long read = canSendReceive.readMessage(MEASURED_DISTANCE_MESSAGE, myId);
    int rangingStatus = Byte.toUnsignedInt(canSendReceive.result[2]);

    if (rangingStatus != 0)
      read = (long)-rangingStatus;
    if (read != -1 && rangingStatus == 0) {
      double mm = (double) 256 * canSendReceive.result[1] + canSendReceive.result[0];
      switch (du) {
      case Mm:
        return mm;
      case M:
        return mm / 1000;
      case In:
        return mm / 25.4;
      case Ft:
        return mm / 304.8;
      default:
        return -33.;
      }
    }
  }

  boolean isBitSet(byte test, int index) {
    int mask = 1 << index;
    return (test & mask) != 0;
  }

  // public double readQuality() {
  // CANData canData = new CANData();
  // double quality = 999;
  // if (!canbusDistanceSensor.readPacketNew(MEASUREMENT_QUALITY_MESSAGE,
  // canData)) {
  // return quality;
  // } else {
  // return quality;
  // }
  // }

  // public double readQualityStdDev() {
  // CANData canData = new CANData();
  // double stDev = 999;
  // if (!canbusDistanceSensor.readPacketNew(MEASUREMENT_QUALITY_MESSAGE,
  // canData)) {
  // return stDev;
  // } else {
  // return stDev;
  // }
  // }

  // public double readCalibrationState() {
  // CANData canData = new CANData();
  // double calState = 999;
  // if (!canbusDistanceSensor.readPacketNew(CALIBRATION_STATE_MESSAGE, canData))
  // {
  // return calState;
  // } else {
  // return calState;
  // }
  // }

  // // Messages to device
  // private void configureRange(int mode) {
  // byte[] data = null;
  // switch (mode) {
  // case 0:// short
  // data[0] = 0x0;
  // break;
  // case 1:// medium
  // data[0] = 0x01;
  // break;
  // case 2:// long
  // data[0] = 0x02;
  // break;
  // default:
  // data[0] = 0x0;
  // break;
  // }
  // canSend.sendMessage(RANGING_CONFIGURATION_MESSAGE, data, 1,
  // kSendMessagePeriod);
  // }

  public void identifyDevice(int apiId) {

    hwdata[0] = 0x0D;

    CANSend.sendMessage(DEVICE_CONFIGURATION_MESSAGE | apiId, hwdata, 6, kSendMessagePeriod);
  }

  private void configureDevice(int newID) {
    if (newID > 0 && newID < 33) {
      hwdata[0] = 0x0C;
      hwdata[7] = (byte) newID;
      CANSend.sendMessage(DEVICE_CONFIGURATION_MESSAGE, hwdata, 7, kSendMessagePeriod);
    }
  }

}
