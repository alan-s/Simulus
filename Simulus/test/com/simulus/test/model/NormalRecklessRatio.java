package com.simulus.test.model;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;


/**
 *{@code NormalRecklessRatio} tests on Normal Reckless Cars ratio
 * 
 * 
 */
public class NormalRecklessRatio extends TestCaseBaseCode{

	private boolean test1Pass = false;

	private double recklessCount;
	private double normalCarCount;
	private double vihecleCount;

	private double ratio ;
	private double myRatio;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {


		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();

		writeToFile("test/com/simulus/result/NormalRecklessRatio.txt", true);
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
		Thread.sleep(3000);

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

				SimulationController.getInstance().setRecklessNormalRatio(0.45);
				SimulationController.getInstance().setSpawnRate(1);
				
				ratio = SimulationController.getInstance().getRecklessNormalRatio();
				writeToLog("Map reckless/Normal Ratio: " + ratio);
	
				recklessCount = SimulationController.getInstance().getRecklessCount();

				vihecleCount = SimulationController.getInstance().getMap().getVehicleCount();

				normalCarCount = vihecleCount - recklessCount;

				myRatio = recklessCount/vihecleCount;

				writeToLog("Reckless cars on map: " + recklessCount);
				writeToLog("Normal Cars on map: " + normalCarCount);
				writeToLog("Current reckless/normal Ratio " + myRatio);

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
