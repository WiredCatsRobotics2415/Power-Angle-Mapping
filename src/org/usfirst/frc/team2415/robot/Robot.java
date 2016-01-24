package org.usfirst.frc.team2415.robot;

import java.io.BufferedWriter;

import com.kauailabs.nav6.frc.IMU;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;

public class Robot extends SampleRobot {
	
	private final int[] LEFT_TALS = {0,1};
	private final int[] RIGHT_TALS = {2,3};
	private final int[] LEFT_ENC = {0,1};
	private final int[] RIGHT_ENC = {2,3};
	
	private final double POWER_INCREMEMT = 0.05;
	private BufferedWriter file;
	
	Transmission left, right;
	
    public Robot(){
    	left = new Transmission(LEFT_TALS, LEFT_ENC);
    	right = new Transmission(RIGHT_TALS, RIGHT_ENC);
    	
    	WriteToFlashDrive.createBufferedWriter("Hermes", file);
    }
    
    public void robotInit() {}

    public void autonomous() {
    	int numIntervals = (int)(2/POWER_INCREMEMT)+1;
    	double power;
    	for(int i=0; i<numIntervals; i++){
    		power = (i*POWER_INCREMEMT)-1;
    		left.setMotors(power);
    		right.setMotors(power);
    		
    		Timer.start();
    		while(Timer.getTime() <= 0.5);
    		
    		Timer.start();
    		while(Timer.getTime() <= 1){
    			WriteToFlashDrive.writeToFile(power, (left.getVel() + right.getVel())/2, file);
    		}
    	}
    	WriteToFlashDrive.flushAndClose(file);
    }

    public void operatorControl() {}
    public void test() {}
    
    private class Transmission{
		private CANTalon tal1, tal2;
		private Encoder encoder;
		public Transmission(int[] talPorts, int[] encPorts){
			tal1 = new CANTalon(talPorts[0]);
			tal2 = new CANTalon(talPorts[1]);
			
			encoder = new Encoder(encPorts[0], encPorts[1]);
		}
		public void setMotors(double speed){
			tal1.set(speed);
			tal2.set(speed);
		}
		
		public void stop(){
			tal1.set(0);
			tal2.set(0);
		}
		
		public double getVel(){
			return encoder.getRate();
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
