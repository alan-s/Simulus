package com.simulus.test.model;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.test.TestCaseBaseCode;

/**
 *{@code SpawningVihecles} tests on checking there are vehicles spawned on the map
 *
 *
 */
public class SpawningVihecles extends TestCaseBaseCode  {

	private boolean test1Pass;
	private	boolean test2Pass;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();

		writeToFile("test/com/simulus/result/SpawningVihecles.txt", true);
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

				if (carIsSpawnedOnMap()){
					test1Pass = true;
					writeToLog(" Cars detected on the map: " + carIsSpawnedOnMap() );
				}
				else{
					writeToLog(" Cars detected on the Map: " + truckIsSpawnedOnMap() );
				}
				writeToLog("Test1 completed!");
			}

		});

		Thread.sleep(4000);


		isTestPassed(test1Pass, 1);

	}


	@Test 
	public void test2() throws InterruptedException {


		executor.execute(new Runnable() {

			@Override
			public void run() {

				writeToLog("Initialising Test2...");

				if (truckIsSpawnedOnMap()){
					test2Pass = true;
					writeToLog(" Trucks detected on Map: " + truckIsSpawnedOnMap() );

				}
				else{
					writeToLog(" Trucks detected on map: " + truckIsSpawnedOnMap() );
					test2Pass = false;

				}

				writeToLog("Test2 completed!");

			}
		});


		Thread.sleep(2000);

		isTestPassed(test2Pass, 2);

	}

	

}




