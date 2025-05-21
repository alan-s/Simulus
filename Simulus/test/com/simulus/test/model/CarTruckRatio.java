package com.simulus.test.model;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;


/**
 * {@code CarTruckRatio} tests Car Truck Ratio
 * 
 * 
 */
public class CarTruckRatio extends TestCaseBaseCode{

	private boolean test1Pass = false;

	private double car;
	private double truck;
	private double vihecleCount;

	private double ratio ;
	private double myRatio;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {


		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();

		writeToFile("test/com/simulus/result/CarTruckRatio.txt", true);
		writeLog.flush();
		writeLog.WriteToLog("FX App thread started \n");

		executor.execute(new Runnable() {

			@Override
			public void run() {
				clickButton("Simulator");

			}
		});
		Thread.sleep(2000);
		loadDefaultMap();	
	}
	@Before
	public void setUp() throws Exception {
		Thread.sleep(5000);

	}

	@After
	public void tearDown() throws Exception {
		Thread.sleep(3000);
	}


	@Test 
	public void test0() {

		executor.execute(new Runnable() {

			@Override
			public void run() {

				scene = new SceneDock();

				clickButton("Start"); 

			}
		});

	}


	@Test
	public void test1() throws InterruptedException {
		executor.execute(new Runnable() {

			@Override
			public void run() {

				writeToLog("Initialising Test1...");

				ratio = SimulationController.getInstance().getCarTruckRatio();
				
				writeToLog("Map Car/Truck Ratio: " + ratio);
				
				truck = SimulationController.getInstance().getTruckCount();

				vihecleCount = SimulationController.getInstance().getMap().getVehicleCount();

				car = vihecleCount - truck;

				myRatio = car/vihecleCount;

				writeToLog("Trucks on map: " + truck);
				writeToLog("Cars on map: " + car);
				writeToLog("Current Car/Truck Ratio " + myRatio);
		


				if(myRatio< ratio){
					test1Pass = true;
				}


				writeToLog("Test 1 completed!");

			}
		});

		Thread.sleep(3000);
		isTestPassed(test1Pass, 1);
	}



}
