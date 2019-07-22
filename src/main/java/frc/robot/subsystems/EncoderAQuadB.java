/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

/**
 * 
 */

public class EncoderAQuadB{
  public Encoder encoder;

  public EncoderAQuadB(int pinA, int pinB, boolean reverse,double distancePerPulse) {

    encoder = new Encoder(pinA, pinB);
    encoder.setReverseDirection(reverse);
    encoder.setDistancePerPulse(distancePerPulse);
 
  }

  public int getCounts(){
    return encoder.getRaw();
  }
  public double getDistance() {
    return encoder.getDistance();
  }

  public double getRate() {
    return encoder.getRate();
  }

  public boolean getStopped() {
    return encoder.getStopped();
  }




 

}
