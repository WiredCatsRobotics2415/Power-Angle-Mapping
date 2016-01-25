package org.usfirst.frc.team2415.robot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot {
	
	private final int[] LEFT_TALS = {0,1};
	private final int[] RIGHT_TALS = {2,3};
	private final int[] LEFT_ENC = {0,1};
	private final int[] RIGHT_ENC = {2,3};
	
	private final double POWER_INCREMEMT = 0.01;
	private BufferedWriter file;
	
	Transmission left, right;
	
    public Robot(){
    	left = new Transmission(LEFT_TALS, LEFT_ENC);
    	right = new Transmission(RIGHT_TALS, RIGHT_ENC);
	
    }
    
    public void writeToFile(double currentValue, double desiredValue) {
		try {
			file.write(currentValue + ",");
			file.write(desiredValue + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public void flushAndClose() {
		try {
    		System.out.println("I am about to flush the toilet");
			file.flush();
			file.close();
			System.out.println("The dookie is down the drain");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public void robotInit() {
    	try {
    		file = new BufferedWriter(new FileWriter(new File("/V/hermes.csv")));
    	} catch (IOException e) {
    		try {
    			file = new BufferedWriter(new FileWriter(new File("/U/hermes.csv")));
    		} catch (IOException f) {
    			f.printStackTrace();
    		}
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
    }

    public void autonomous() {
    	
    	
    	int numIntervals = (int)(2/POWER_INCREMEMT)+1;
    	double power;
    	for(int i=0; i<numIntervals; i++){
    		power = (i*POWER_INCREMEMT)-1;
    		left.setMotors(power);
    		right.setMotors(power);
    		
    		Timer.start();
    		while(Timer.getTime() <= 0.5);
    		
			for(int j = 0; j < 10; j++){
    			Timer.start();
    			while(Timer.getTime() < .1);
    			writeToFile(power, (right.getVel()));
			}
    	}
    	flushAndClose();
		left.setMotors(0);
		right.setMotors(0);
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
