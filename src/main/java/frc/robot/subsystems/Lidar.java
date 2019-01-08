/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.SD;

public class Lidar {
  /**
   * Requires 4.7 k resistor from mode input to ground
   * 
   * 
   */

  public static DigitalOutput lidarPowerEnable;
  
  public static Counter pwmWidth;

  private double lidarPulseWidth;

  public Lidar(int enPin, int modePin) {

    lidarPowerEnable = new DigitalOutput(enPin);// lidar power enable
    
    pwmWidth = new Counter(modePin); // need time of pwm high in microseconds
    
    pwmWidth.setSemiPeriodMode(true);// set counter for measuring pulse high

    lidarPowerEnable.set(true);// enable lidar

  }

  public double readLidarSensor() {

    lidarPulseWidth = pwmWidth.getPeriod();// get pwm high time

    if (lidarPulseWidth != 0) {
      lidarPulseWidth = lidarPulseWidth * 1000000;
      return lidarPulseWidth;
    } // 10 microseconds is 1 cm. Value returned is in seconds so this is mm

    else {
      lidarPowerEnable.set(false);
      Timer.delay(.005);
      lidarPowerEnable.set(true);
      return 0;
    }
  }

  public double getDistanceMM() {
    return readLidarSensor();
  }

  public double getDistanceMetes() {
    return getDistanceMM() / 1000;
  }

  public double getDistanceInches() {
    return getDistanceMM() / 25.4;
  }

  public double getDistanceFeet() {
    return getDistanceInches() / 12;
  }

  public void updateStatus() {
    SD.putN0("Lidar MM ", getDistanceMM());
    SD.putN2("Lidar Meters ", getDistanceMetes());
    SD.putN1("Lidar Inches ", getDistanceInches());
    SD.putN2("Lidar Feet ", getDistanceFeet());
    SmartDashboard.putBoolean("En", lidarPowerEnable.get());
  }
}
