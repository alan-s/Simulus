package com.simulus.test.model;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.controller.SimulationController;
import com.simulus.test.TestCaseBaseCode;


/**
 * {@code AnimationThread} tests execution of AnimationThread and detect its presence 
 */
public class AnimationThread extends TestCaseBaseCode {

	private boolean test1Pass;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();
		writeToFile("test/com/simulus/result/AnimationThread.txt", true);
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
				writeToLog("Initialising Test1...");

				SimulationController.getInstance().startSimulation();
				writeToLog("Simulation started.");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (SimulationController.getInstance().getAnimationThread().isAlive()){
					test1Pass = true;
					writeToLog("Thread status is alive.");
				}

			}
		});

		Thread.sleep(4000);
		isTestPassed(test1Pass, 1);

	}



}




