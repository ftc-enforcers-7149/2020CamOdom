package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.LynxUsbDevice;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxI2cConfigureChannelCommand;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.internal.android.dx.util.Warning;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.util.BulkRead;
import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.RevBulkData;

@Autonomous(name="bulkread")
public class BulkReadTest extends SuperAuto {

    ExpansionHubEx expX;
    DistanceSensor lDist, rDist;

    int stepCounter = 0;
//TODO: CHANGE I2C ADDRESS
    public void init() {

        super.init();

        lDist = hardwareMap.get(DistanceSensor.class, "lRange");
        rDist = hardwareMap.get(DistanceSensor.class, "rRange");

        expX = hardwareMap.get(ExpansionHubEx.class, "Control Hub");

        LynxI2cConfigureChannelCommand.SpeedCode speedCode = LynxI2cConfigureChannelCommand.SpeedCode.FAST_400K;
        LynxI2cConfigureChannelCommand command = new LynxI2cConfigureChannelCommand(controlHub, 1, speedCode);
        LynxI2cConfigureChannelCommand command2 = new LynxI2cConfigureChannelCommand(controlHub, 2, speedCode);
        try
        {
            command.send();
            command2.send();
        }
        catch (LynxNackException | InterruptedException e)
        {
            throw new Warning("I2C speeds configured incorrectly.");

        }
        finally {
            telemetry.addLine("Done!");
        }

    }


    public void loop() {

        controlHub.setConstant(532342);

        double[] retVal = bRead.getMotors();
        double[] velVal = bRead.getMVelocity();
        String readVal = retVal[0] + " " + retVal[1];
        String velocityVal = velVal[0] + " " + velVal[1];

        switch (stepCounter) {
            case 0:
                Trajectory traj = drive.trajectoryBuilder(new Pose2d(0, 0))
                        .forward(10)
                        .build();
                drive.followTrajectory(traj);
        }
        stepCounter++;

        telemetry.addData("Position return: ", readVal);
        telemetry.addData("Velocity return: ", velocityVal);
        telemetry.addLine(expX.getConnectionInfo());
        telemetry.addData("String return: ", expX.getBulkInputData().getMotorCurrentPosition(bRead.portX1));
        telemetry.addData("X port: ", bRead.portX1);
        telemetry.addData("Y port: ", bRead.portY);
        telemetry.addData("rDist: ", rDist.getDistance(DistanceUnit.CM));
        telemetry.addData("lDist: ", lDist.getDistance(DistanceUnit.CM));

    }
}
