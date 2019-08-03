/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

/**
 * Add your docs here.
 */
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import edu.wpi.first.hal.can.CANExceptionFactory;
import edu.wpi.first.hal.can.CANJNI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CANSendReceive {

    public static ByteBuffer targetID = ByteBuffer.allocateDirect(4);
    private static ByteBuffer timeStamp = ByteBuffer.allocateDirect(4);
    public static byte[] result;
    public static int status;

    /** helper routine to get last received message for a given ID */
    public static long readMessage(int fullId, int deviceID) {

        // try {
        // CANExceptionFactory.checkStatus(status, fullId | deviceID);

        // } catch (Exception e) {
        // return -2;
        // }
        try {
            targetID.clear();
            targetID.order(ByteOrder.LITTLE_ENDIAN);
            targetID.asIntBuffer().put(0, fullId | deviceID);

            timeStamp.clear();
            timeStamp.order(ByteOrder.LITTLE_ENDIAN);
            timeStamp.asIntBuffer().put(0, 0x00000000);

            result = CANJNI.FRCNetCommCANSessionMuxReceiveMessage(targetID.asIntBuffer(), 0x1fffffff, timeStamp);

            long retval = timeStamp.getInt();
            retval &= 0xFFFFFFFF; /* undo sign-extension */
            return retval;

        } catch (Exception e) {

            System.out.println(e);
            return -1;
        }
    }

    public static void sendMessage(int messageID, byte[] data, int dataSize, int period) {

        CANJNI.FRCNetCommCANSessionMuxSendMessage(messageID, data, period);
        CANExceptionFactory.checkStatus(status, messageID);

        return;

    }

}
