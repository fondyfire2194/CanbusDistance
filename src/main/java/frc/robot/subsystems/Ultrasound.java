/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.SD;
public class Ultrasound {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
/**
 * Maxbotics inch ultrasound analog resolution = 
 * [(5.0V/512) = 0.009766V per inch = 9.766mV per inch]
 * 
 *  Maxbotics metric ultrasound analog resolution = 
[(5.0V/1024) = 0.004883V per cm = 4.883mV per cm]
 */
  public static enum ultrasoundType {
    metric, inch
  };


  private double voltsPerInch;
  private double voltsPerMM;

  AnalogInput ultrasoundSensor;

  public Ultrasound(int analogPin, ultrasoundType type, double voltsPerUnit) {

    ultrasoundSensor = new AnalogInput(analogPin);

    if (type == ultrasoundType.metric) {
      voltsPerMM =  voltsPerUnit;
      voltsPerInch = voltsPerUnit * 25.4;
    } else {
      voltsPerInch = voltsPerUnit;
      voltsPerMM = voltsPerInch/25.4;
    }
  }


  public double getVolts() {
    return ultrasoundSensor.getAverageVoltage();
  }

  public double getDistanceMM() {
    return getVolts() / voltsPerMM;
  }

  public double getDistanceMeters() {
    return getDistanceMM() / 1000;
  }

  public double getDistanceInches() {
    return getVolts()/voltsPerInch;
  }
  
  public double getDistanceFeet() {
    return getDistanceInches() / 12;
  }

  public void updateStatus() {
    SD.putN0("Usnd MM ", getDistanceMM());
    SD.putN1("Usnd Meters ", getDistanceMeters());
    SD.putN0("Usnd Inches ", getDistanceInches());
    SD.putN1("Usnd Feet ", getDistanceFeet());
  }
}
