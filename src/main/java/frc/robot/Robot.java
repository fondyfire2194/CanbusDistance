/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.CanbusDistanceSensor;
import frc.robot.subsystems.CANSendReceive;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  public static OI m_oi;

  private double a;
  private int b;

  public static int distanceSensorLoad = 0;
  public static double loadSensorSerial;
  public static double loadSensorPart;
  public static double loadSensorFirmware;
  public static byte[] hwdataLoad = new byte[8];

  public static int distanceSensorRocket = 0;
  public static double rocketSensorSerial;
  public static double rocketSensorPart;
  public static double rocketSensorFirmware;
  public static byte[] hwdataRocket  = new byte[8];

  
  
  
  /*
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_oi = new OI();
 

    hwdataLoad = CanbusDistanceSensor.readHeartbeat(distanceSensorLoad);
    double[] temp = CanbusDistanceSensor.getSensorInfo(hwdataLoad);
    loadSensorSerial = temp[0];
    loadSensorPart = temp[1];
    loadSensorFirmware = temp[2];
    SmartDashboard.putNumber("LoadSerial", loadSensorSerial);
    SmartDashboard.putNumber("LoadPart", loadSensorPart);
    SmartDashboard.putNumber("LoadFirmware", loadSensorFirmware);
    double temp1[] = CanbusDistanceSensor.readCalibrationState(distanceSensorLoad);
    SD.putN("X", temp1[0]);
    SD.putN("Y", temp1[1]);
    SD.putN("Offset", temp1[2]);
 }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    updateStatus();

  }

  /**
   * This function is called once each time the robot enters Disabled mode. You
   * can use it to reset any subsystem information you want to clear when the
   * robot is disabled.
   */
  @Override
  public void disabledInit() {
    
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
    
    
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString code to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons to
   * the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {

    /**
     * String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
     * switch(autoSelected) { case "My Auto": autonomousCommand = new
     * MyAutoCommand(); break; case "Default Auto": default: autonomousCommand = new
     * ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    SmartDashboard.putNumber("TTT", Timer.getFPGATimestamp() - a);
    a = Timer.getFPGATimestamp();
    b++;
    if (b >= 10) {
      double dist = CanbusDistanceSensor.getDistanceMM(distanceSensorLoad);
      SD.putN0("DistMM", dist);
      SD.putN2("DistFt", dist / 304.8);
      double temp[] = CanbusDistanceSensor.readQuality(distanceSensorLoad);
      SD.putN0("AmbLight", temp[0]);
      SD.putN0("StdDev", temp[1]);

      b = 0;

    }

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void updateStatus() {


  }
}
