package com.simulus.test.model;
import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;

/**
 *{@code SetNewSpawnDelay} tests the Controller settings on Tick Rate and Spawn Delay
 *
 */
public class SetNewSpawnDelay extends TestCaseBaseCode {

	private boolean test1Pass = false;
	private boolean test2Pass = false;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();

		writeToFile("test/com/simulus/result/SetNewSpawnDelay.txt", true);
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


	@After
	public void tearDown() throws Exception {
		Thread.sleep(5000);
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

				double spawnRate = SimulationController.getInstance().getSpawnRate();

				writeToLog("The current Spawn rate is: "+ spawnRate );

				if(spawnRate == 5.0){
					test1Pass = true;
				}
				else{
					test1Pass = false;
					writeToLog("The default Spawn rate is not equal to 5. Test Fail!");
				}
				writeToLog("Test 1 completed!");

			}
		});

		Thread.sleep(3000);
		isTestPassed(test1Pass, 1);
	}


	/**
	 * Run comparison of spawn rate against the Controller
	 * @throws InterruptedException
	 */
	@Test
	public void test2() throws InterruptedException{

		executor.execute(new Runnable() {

			@Override
			public void run() {
				writeToLog("Initialising Test2...");

				SimulationController.getInstance().setSpawnRate(1);

				double spawnRate = SimulationController.getInstance().getSpawnRate();

				if (spawnRate !=1){
					writeToLog("Spawn rate has not been changed correctly to 1. Test Fail!");
				}
				else{
					test2Pass = true;
				}
				writeToLog("Test 2 completed!");
			}
		});
		Thread.sleep(4000);
		isTestPassed(test2Pass, 2);
	}


}
