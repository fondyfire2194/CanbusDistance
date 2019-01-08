package frc.robot.subsystems;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.StopBits;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.SD;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * A subsystem for accessing the TFMini "LIDAR".
 * 
 * The TFMini reports distance in centimeters.
 * 
 * The TFMini frame format is as follows: Byte 0 - 0x59 frame header Byte 1 -
 * 0x59 frame header Byte 2 - Distance low byte Byte 3 - Distance high byte Byte
 * 4 - Strength low byte Byte 5 - Strength high byte Byte 6 - Reserved Byte 7 -
 * Original signal quality degree Byte 8 - Checksum = low byte of sum of
 * preceding 8 bytes
 */
public class TFMini {
  private SerialPort serialPort;

  private final int frameHeaderByte = 0x59;
  int badCheck;
  int badPacket;
  // Current values:
  int distanceCM;// actual distance measurements of LiDAR
  int signalStrength;// signal strength of LiDAR'
  int originalSignalQualityDegree;
  boolean check;// save check value
  int i;
  int[] uart = new int[9];// save data measured by LiDAR
  int HEADER = 0x59; // frame header of data package

  public TFMini() {
    int retry_counter = 0;
    SmartDashboard.putBoolean("SUCCESS", false);
    // Retry strategy to get this serial port open.
    // I have yet to see a single retry used assuming the camera is plugged in
    // but you never know.
    while (serialPort == null && retry_counter++ < 10) {
      try {
        System.out.print("Creating TFMini SerialPort...");
        serialPort = new SerialPort(115200, SerialPort.Port.kMXP, 8, Parity.kNone, StopBits.kOne);
        System.out.println("SUCCESS!!");
        SmartDashboard.putBoolean("Port OK", true);
      } catch (Exception e) {
        System.out.println("FAILED!!");
        SmartDashboard.putBoolean("Port OK", false);
        e.printStackTrace();

        System.out.println("Retry " + Integer.toString(retry_counter));
      }
    }
    if (serialPort == null) {
      DriverStation.reportError("Cannot open serial port to TFMini.", false);

      return;
    }
    // SmartDashboard.putBoolean("SUCCESS3", serialPort != null);
    serialPort.setWriteBufferSize(1);
    serialPort.setReadBufferSize(20);

    serialPort.flush();
    // Init TFMini message
    byte[] sendData = { 0x42, 0x57, 0x02, 0x00, 0x00, 0x00, 0x01, 0x06 };
    Timer.delay(.5);
    // int datasent = serialPort.write(sendData, 8);

    // SmartDashboard.putNumber("sent", datasent);

    // Start listening for packets
    packetListenerThread.setDaemon(true);
    packetListenerThread.start();
  }

  public void updateStatus() {
    SD.putN0("TFMini Distance Cm", distanceCM);
    SD.putN0("TFMini Distance In", distanceCM / 2.54);
    SmartDashboard.putNumber("TFMini Signal Strength", signalStrength);
    SmartDashboard.putNumber("TFMini Original Quality", originalSignalQualityDegree);
  }

  private void backgroundUpdate() {
    byte[] buff;
    byte sumBytes = 0;

    if (serialPort.getBytesReceived() >= 9)// check if serial port has data input
    {

      buff = serialPort.read(9);

      if (buff[0] == 0x59 && buff[1] == 0x59) {
        sumBytes = 0;
        for (int i = 0; i < 8; i++) {
          sumBytes += buff[i];
        }
        check = sumBytes == buff[8];
        SmartDashboard.putBoolean("ChkSumOK", check);

        if (check) {
          distanceCM = (buff[3] & 0xFF << 8 | buff[2] & 0xFF);
          signalStrength = (buff[5] & 0xFF << 8 | buff[4] & 0xFF);
          originalSignalQualityDegree = buff[7] & 0xFF;
        } else {
          badCheck++;
        }
      } else {
        badPacket++;
      }
    }

    SmartDashboard.putNumber("BadChck", badCheck);
    SmartDashboard.putNumber("BadPckt", badPacket);
  }

  /**
   * This thread runs a periodic task in the background to listen for serial
   * packets.
   */
  Thread packetListenerThread=new Thread(new Runnable(){public void run(){while(!Thread.interrupted()){backgroundUpdate();}}});

}