package com.simulus.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import org.jemmy.fx.SceneDock;
import org.jemmy.fx.control.LabeledDock;
import org.jemmy.resources.StringComparePolicy;

import com.simulus.controller.SimulationController;
import com.simulus.test.WriteLog;
import com.simulus.view.vehicles.Car;
import com.simulus.view.vehicles.EmergencyCar;
import com.simulus.view.vehicles.Truck;
import com.simulus.view.vehicles.Vehicle;

/**
 * {@code TestCaseBaseCode} represent the code base that is used throughout test cases
 *
 */
public class TestCaseBaseCode {

	public final static Executor executor = Executors.newSingleThreadExecutor();
	public static LaunchApp appThread = new LaunchApp();
	public static  WriteLog writeLog = new WriteLog("test/com/simulus/result/TestCase1.txt", true);
	public static SceneDock scene;

	private Thread returnedThread;
	private ArrayList<Double> vihecleSpeed = new ArrayList<>();

	boolean threadFound = false;
	boolean isAlive = false;
	boolean isInterrupted = false;



	public static void loadDefaultMap(){
		File defaultMap = new File(SimulationController.class.getResource("/resources/maps/default.map").getPath());
		if (defaultMap.canRead()){
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					SimulationController.getInstance().getMap().loadMap(defaultMap);

				}
			});

		}
	}


	/**
	 * Write data to a file 
	 * @param fileLocation directory to store the file
	 * @param append Boolean 
	 */
	public static void writeToFile(String fileLocation, boolean append){
		writeLog = new WriteLog(fileLocation, append);
	}

	/**
	 * A quick way to log a test result to a file 
	 * @param status Boolean the test scenario in a test case
	 * @param testScenario Integer scenario specified in a test case
	 */
	public void isTestPassed(boolean status, Integer testScenario){

		if(status == false){
			writeToLog("Test scenario " + testScenario + " passed: " + status);
			writeToLog("Test scenario " + testScenario + " Failed.");
			fail("Test Scenario: " + testScenario + " Failed! ");
		}
		else{
			writeToLog("Test scenario " + testScenario + " passed: " + status);
		}
	}

	/**
	 * Write data to the file 
	 * @param text String The message need to be logged
	 */
	public void writeToLog(String text){
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					writeLog.WriteToLog(text);
					writeLog.WriteToLog(" ");
				} catch (IOException e) {}
			}

		});
	}


	/**
	 * Click a button 
	 * @param buttonName String The name of the button
	 */
	public static void clickButton(String buttonName) {
		executor.execute(new Runnable() {
			@Override
			public void run() {

				boolean buttonClick = false;

				//Identifying the input button 
				assertEquals(Button.class, new LabeledDock(scene.asParent(), buttonName, 
						StringComparePolicy.EXACT).wrap().getControl().getClass());


				//Find the label of the button 
				LabeledDock b = new LabeledDock(scene.asParent(), buttonName, StringComparePolicy.EXACT);

				if(!b.isPressed()){
					b.mouse().click(1);
					buttonClick=true;
				}	

				try {
					writeLog.WriteToLog(buttonName + " Button Detected \n") ;
					writeLog.WriteToLog(buttonName + " Button Clicked: " + buttonClick + "\n");
				} catch (IOException e) {}

			}
		});

	}

	/**
	 * Select a check box
	 * @param checkBoxName String the name of the checkBox
	 */
	public static void selectCheckBox(String checkBoxName) {
		executor.execute(new Runnable() {
			@Override
			public void run() {

				boolean buttonClick = false;

				//Identifying the input checkBox 
				assertEquals(CheckBox.class, new LabeledDock(scene.asParent(), checkBoxName, 
						StringComparePolicy.EXACT).wrap().getControl().getClass());


				//Find the label of the checkBox 
				LabeledDock cb = new LabeledDock(scene.asParent(), checkBoxName, StringComparePolicy.EXACT);


				if(!cb.isPressed()){
					cb.mouse().click(1);
					buttonClick=true;
				}	
				try {
					writeLog.WriteToLog(checkBoxName + " CheckBox Detected \n") ;
					writeLog.WriteToLog(checkBoxName + " CheckBox Clicked: " + buttonClick + "\n");
				} catch (IOException e) {}

			}
		});

	}


	/**
	 * Check the status of a thread
	 * @param threadName String the name of the thread
	 */
	public void checkThreadStatus(String threadName){
		searchThread(threadName);  //Query the status of the thread
		writeToLog(threadName + " found: " + threadFound);
	}


	/**
	 * Search a thread
	 * @param threadName Thread name to be checked
	 * @return instance of the specified searching thread
	 */
	public Thread searchThread(String threadName) {

		for(Thread allThread : Thread.getAllStackTraces().keySet()){
			if(allThread.getName().equals(threadName));

			threadFound = true;
			returnedThread = allThread; //Store thread in a separate variable

			chceckThreadStatus();
		}

		return returnedThread;

	}

	/**
	 * Check the status of a returned thread
	 */
	private void chceckThreadStatus( ) {
		if(returnedThread.isAlive()){
			isAlive = true;
		}
		if(returnedThread.isInterrupted()){
			isInterrupted = true;
		}
	}


	/**
	 * Remove all the emergency cars from the map
	 * @return null
	 */
	public Vehicle removeEmergencyCar(){
		for (Vehicle v : SimulationController.getInstance().getMap().getVehicles())
			if(v instanceof EmergencyCar){
				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						SimulationController.getInstance().removeVehicle(v);
					}
				});

			}
		return null;

	}

	/**
	 * Get all vehicles' speed in a array
	 * @return null
	 */
	public Vehicle getAllVihecleSpeed(){
		for (Vehicle v : SimulationController.getInstance().getMap().getVehicles())
			if(v instanceof Car || v instanceof Truck){
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						vihecleSpeed.add(v.getVehicleSpeed());
					}
				});
			}



		return null;

	}



	/**
	 * Check truck is spawned on the map 
	 * @return 
	 */
	public boolean truckIsSpawnedOnMap (){

		for(Vehicle v : SimulationController.getInstance().getMap().getVehicles())
			if(v instanceof Truck){
				return true;
			}

		return false;
	}

	/**
	 * Check car is spawned on the map 
	 * @return 
	 */
	public boolean carIsSpawnedOnMap(){
		for(Vehicle v : SimulationController.getInstance().getMap().getVehicles())
			if(v instanceof Car){
				return true;
			}

		return false;

	}


	/**
	 * Check ambulance is spawned on the map 
	 * @return 
	 */
	public boolean AmbulanceIsSpawnedOnMap(){
		for (Vehicle v : SimulationController.getInstance().getMap().getVehicles())
			if(v instanceof EmergencyCar){
				return true;
			}
		return false;
	}

	/**
	 * Check debug checkBox is selected from the UI
	 * @return 
	 */
	public boolean debugBoxSelected(){
		return	SimulationController.getInstance().isDebug();

	}

	/*
	 * Getter and Setter
	 */

	public ArrayList<Double> getVehicleSpeed(){
		return vihecleSpeed;
	}

	public boolean getIsAlive(){
		return isAlive;
	}



}




