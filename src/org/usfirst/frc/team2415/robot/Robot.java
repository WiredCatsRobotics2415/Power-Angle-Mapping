package org.usfirst.frc.team2415.robot;

import java.io.BufferedWriter;

import com.kauailabs.nav6.frc.IMU;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;

public class Robot extends SampleRobot {
	
	private final double POWER_INCREMEMT = 0.05;
	private BufferedWriter file;
	private IMU imu;
	
	Transmission left, right;
	
    public Robot() {
    	left = new Transmission(0,1);
    	right = new Transmission(2,3);
    	
    	SerialPort imuPort = new SerialPort(57600, SerialPort.Port.kMXP);
    	imu = new IMU(imuPort, (byte)50);
    	imu.zeroYaw();
    	
    	WriteToFlashDrive.createBufferedWriter("Hermes", file);
    }
    
    public void robotInit() {
    }

    public void autonomous() {
    	int numIntervals = (int)(2/POWER_INCREMEMT)+1;
    	double power;
    	for(int i=0; i<numIntervals; i++){
    		power = (i*POWER_INCREMEMT)-1;
    		left.setMotors(power);
    		right.setMotors(power);
    		
    		Timer.start();
    		while(Timer.getTime() <= 0.25);
    		left.stop();
    		right.stop();
    		
    		Timer.start();
    		while(Timer.getTime() <= 0.75);
    		WriteToFlashDrive.writeToFile(power, imu.getYaw(), file);
    		
    		imu.zeroYaw();
    	}
    	WriteToFlashDrive.flushAndClose(file);
    }

    public void operatorControl() {}
    public void test() {}
    
    private class Transmission{
		private CANTalon tal1, tal2;
		public Transmission(int port1, int port2){
			tal1 = new CANTalon(port1);
			tal2 = new CANTalon(port2);
		}
		public void setMotors(double speed){
			tal1.set(speed);
			tal2.set(speed);
		}
		
		public void stop(){
			tal1.set(0);
			tal2.set(0);
		}
	}
    
    private static class Timer {
    	private static long startTime;
    	public static void start(){
    		startTime = System.currentTimeMillis();
    	}
    	
    	public static double getTime(){
    		return (System.currentTimeMillis()-startTime)/1000.0;
    	}
    }
}
