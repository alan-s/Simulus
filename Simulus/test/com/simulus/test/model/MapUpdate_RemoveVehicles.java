package com.simulus.test.model;
import java.util.ArrayList;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;
import com.simulus.view.vehicles.Vehicle;

/**
 * {@code MapUpdate_RemoveVehicles} tests map update on Remove Vehicles
 *
 */

public class MapUpdate_RemoveVehicles extends TestCaseBaseCode {

	private boolean test1Pass = false;
	private boolean test2Pass = false;
	private ArrayList<Vehicle> ExpectedCarRemoveList = new ArrayList<>();
	private ArrayList<Vehicle> ActualCarRemoveList = new ArrayList<>();


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {


		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();

		writeToFile("test/com/simulus/result/MapUpdate_RemoveVehicles.txt", true);
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
				SimulationController.getInstance().setTickTime(20);
				SimulationController.getInstance().setMaxCarSpeed(120);

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

				ExpectedCarRemoveList = SimulationController.getInstance().getMap().getToBeRemovedList();				

				test1Pass= true;
				writeToLog("Test 1 completed!");

			}
		});

		Thread.sleep(2000);
		isTestPassed(test1Pass, 1);
	}


	@Test
	public void test2() throws InterruptedException{

		executor.execute(new Runnable() {

			@Override
			public void run() {
				writeToLog("Initialising Test2...");

				ActualCarRemoveList = SimulationController.getInstance().getMap().getToBeRemovedList();

				if (!ExpectedCarRemoveList.equals(ActualCarRemoveList)){
					test2Pass = true;
				}

				writeToLog("Test 2 completed!");
			}
		});
		Thread.sleep(4000);
		isTestPassed(test2Pass, 2);
	}


}
