/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.nio.ByteBuffer;


import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RevColorV2 {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  protected final static int COMMAND_REGISTER_BIT = 0x80;
  protected final static int MULTI_BYTE_BIT = 0x20;

  protected final static int ENABLE_REGISTER = 0x00;
  protected final static int ATIME_REGISTER = 0x01;
  protected final static int PPULSE_REGISTER = 0x0E;

  protected final static int ID_REGISTER = 0x12;
  protected final static int CDATA_REGISTER = 0x14;
  protected final static int RDATA_REGISTER = 0x16;
  protected final static int GDATA_REGISTER = 0x18;
  protected final static int BDATA_REGISTER = 0x1A;
  protected final static int PDATA_REGISTER = 0x1C;

  private I2C sensor;

  public void RevColor2(I2C.Port port) {
    sensor = new I2C(port, 0x39); // port, I2c address
    SmartDashboard.putBoolean("PortOK",sensor!=null);

    sensor.write(COMMAND_REGISTER_BIT | 0x00, 0b00000011); // power on, color sensor on
  }

  protected int readWordRegister(int address) {
    ByteBuffer buf = ByteBuffer.allocate(2);
    sensor.read(COMMAND_REGISTER_BIT | MULTI_BYTE_BIT | address, 2, buf);
    // buf.order(ByteOrder.LITTLE_ENDIAN);
    return buf.getShort(0);
  }

  public int red() {
    return readWordRegister(RDATA_REGISTER);
  }

  public int green() {
    return readWordRegister(GDATA_REGISTER);
  }

  public int blue() {
    return readWordRegister(BDATA_REGISTER);
  }

  public int clear() {
    return readWordRegister(CDATA_REGISTER);
  }

  public int proximity() {
    return readWordRegister(PDATA_REGISTER);
  }

  public void updateStatus() {
    // SmartDashboard.putNumber("Red:- ", red());
    // SmartDashboard.putNumber("Green:- ", green());
    // SmartDashboard.putNumber("Blue:- ", blue());
    // SmartDashboard.putNumber("Clear:- ", clear());
    SmartDashboard.putNumber("Proximity:- ", proximity());

  }
}
