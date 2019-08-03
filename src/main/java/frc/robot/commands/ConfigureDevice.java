/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.subsystems.CanbusDistanceSensor;

/**
 * Add your docs here.
 */
public class ConfigureDevice extends InstantCommand {
  /**
   * Add your docs here.
   */
  private int myNewDeviceNumber;
  private int myOldDeviceNumber;

  public ConfigureDevice(int oldNumber, int newDeviceNumber) {
    super();
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    myOldDeviceNumber = oldNumber;
    myNewDeviceNumber = newDeviceNumber;
  }

  // Called once when the command executes
  @Override
  protected void initialize() {
    CanbusDistanceSensor.configureDevice(myOldDeviceNumber, myNewDeviceNumber);
  }

}
