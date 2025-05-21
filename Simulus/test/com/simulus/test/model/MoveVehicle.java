package com.simulus.test.model;

import javafx.application.Platform;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;
import com.simulus.util.enums.*;
import com.simulus.view.Tile;
import com.simulus.view.vehicles.Car;
import com.simulus.view.vehicles.Vehicle;

/**
 *{@code MoveVehicle} tests Vehicle's movement - move forward  
 */
public class MoveVehicle extends TestCaseBaseCode {

	private boolean test1Pass;
	private boolean test2Pass;
	private boolean test3Pass;
	Behaviour behaviour = Behaviour.CAUTIOUS;
	Tile vehiclePostion;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();
		writeToFile("test/com/simulus/result/MoveVehicle.txt", true);
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

		Thread.sleep(4000);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				writeToLog("Initialising Test1...");
				
				SimulationController.getInstance().getMap().spawnRandomCar(behaviour); //Spawn a car with a behaviour
				writeToLog("A car is spanwed.");
				test1Pass = true;
				writeToLog("Test 1 completed!");
			}
		});

		Thread.sleep(4000);
		isTestPassed(test1Pass, 1);

	}
	@Test
	public void test2() throws InterruptedException {
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				writeToLog("Initialising Test2...");
				writeToLog("Getting car detail on the map.");
				for (Vehicle v : SimulationController.getInstance().getMap().getVehicles()){
					if(v instanceof Car){

						vehiclePostion = v.getCurrentTile();
						
						for (int i=0; i<200;i++){
							v.move(v.getDirection());
						}
						writeToLog("Car has moved on the map.");

					}
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				test2Pass= true;
				writeToLog("Test 2 completed!");
			}

		});

		Thread.sleep(4000);
		isTestPassed(test2Pass, 2);

	}

	@Test
	public void test3() throws InterruptedException {

		Thread.sleep(4000);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				writeToLog("Initialising Test3...");
				Tile afterMovePosition = null;
				for (Vehicle v : SimulationController.getInstance().getMap().getVehicles()){
					if(v instanceof Car){
						afterMovePosition = v.getCurrentTile();
					}
				}

				if(vehiclePostion != afterMovePosition){ //Pass condition
					test3Pass= true;
					writeToLog("Test 3 completed!");
				}

			}
		});

		Thread.sleep(4000);
		isTestPassed(test3Pass, 3);

	}




}




