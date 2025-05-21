package com.simulus.test.model;

import javafx.application.Platform;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;
import com.simulus.view.Tile;
import com.simulus.view.map.Lane;
import com.simulus.view.vehicles.Ambulance;
import com.simulus.view.vehicles.EmergencyCar;
import com.simulus.view.vehicles.Vehicle;


/**
 * {@code UIElements} Test on the UI elements -
 * Button: start and reset,
 * CheckBox: debug 
 */
public class UIElements extends TestCaseBaseCode {

	private boolean test1Pass;
	private boolean test2Pass;
	private boolean test3Pass;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();
		writeToFile("test/com/simulus/result/UIElements.txt", true);
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

		Thread.sleep(4000);

	}



	@Test 
	public void test1() throws InterruptedException {

		executor.execute(new Runnable() {

			@Override
			public void run() {
				scene = new SceneDock();
				writeToLog("Initialising Test1...");


				clickButton("Start"); 
				checkThreadStatus("animationThread");

				if(getIsAlive() == true){
					test1Pass = true;
					writeToLog("thread status is alive.");
				}

				writeToLog("Test 1 completed!");
			}
		});
		Thread.sleep(2000);
		isTestPassed(test1Pass, 1);

	}

	// 	This test scenario no longer exist as Stop button has been removed from the Main GUI
	//	
	//	@Test
	//	public void test2() throws InterruptedException{
	//		Platform.runLater(new Runnable() {
	//
	//			@Override
	//			public void run() {
	//
	//				writeToLog("Initialising Test2...");
	//
	//				runClickButtonThread("Stop"); 
	//				checkThreadStatus("animationThread");
	//
	//				if(appThread.isInterrupted = true){
	//					test2Pass = true;
	//					writeToLog("thread status is interrupted.");
	//				}
	//
	//				writeToLog("Test 2 completed!");
	//			}
	//		});
	//		Thread.sleep(2000);
	//		isTestPassed(test2Pass, 2);
	//	}

	@Test
	public void test2() throws InterruptedException{

		executor.execute(new Runnable() {

			@Override
			public void run() {

				writeToLog("Initialising Test2...");

				selectCheckBox("Debug");



				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}

				selectCheckBox("Debug");


				if (debugBoxSelected() == false){
					writeToLog("Debug mode is cleared");
					test2Pass = true;	
				}

				writeToLog("Test2` completed!");
			}
		});

		Thread.sleep(4000);
		isTestPassed(test2Pass, 2);
	}



	@Test
	public void test3() throws InterruptedException{
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				writeToLog("Initialising Test3...");

				clickButton("Reset"); 
				checkThreadStatus("animationThread");

				if(getIsAlive() == true){
					test3Pass = true;
					writeToLog("thread status is reseted.");
				}

				writeToLog("Test 3 completed!");

			}
		});
		Thread.sleep(2000);
		isTestPassed(test3Pass, 3);
	}


}




