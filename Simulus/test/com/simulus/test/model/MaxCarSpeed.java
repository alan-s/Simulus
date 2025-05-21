package com.simulus.test.model;
import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;

/**
 *{@code MaxCarSpeed} tests the Controller settings on Max car speed
 *
 */

public class MaxCarSpeed extends TestCaseBaseCode {

	private boolean test1Pass = false;
	private boolean test2Pass = false;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {


		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();

		writeToFile("test/com/simulus/result/MaxCarSpeed.txt", true);
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
		Thread.sleep(4000); 

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

				getAllVihecleSpeed();

				test1Pass = true;

				writeToLog("Test 1 completed!");

			}
		});

		Thread.sleep(5000);
		isTestPassed(test1Pass, 1);
	}

	@Test
	public void test2() throws InterruptedException{
		executor.execute(new Runnable() {

			@Override
			public void run() {

				writeToLog("Initialising Test2...");

				for (int i = 0; i< getVehicleSpeed().size(); i++){
					writeToLog("Vihecle " + i + " speed is: " + getVehicleSpeed().get(i));

					if (getVehicleSpeed().get(i) > 
					SimulationController.getInstance().getMaxCarSpeed()){
						test2Pass = false;
					}
					else{
						test2Pass = true;
					}
				}
				writeToLog("Test2 completed!");


			}
		});

		Thread.sleep(5000);
		isTestPassed(test2Pass, 2);
	}


}
