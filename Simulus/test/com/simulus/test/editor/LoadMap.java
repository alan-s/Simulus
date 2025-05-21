package com.simulus.test.editor;

import java.io.File;

import javafx.application.Platform;

import org.jemmy.fx.SceneDock;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.EditorApp;
import com.simulus.controller.EditorControlsController;
import com.simulus.test.TestCaseBaseCode;
import com.simulus.view.Tile;


/**
 * {@code LoadMap} tests the functionality of loading a map
 *
 */
public class LoadMap extends TestCaseBaseCode {
	
	private boolean test1Pass;
	private File mapFile = new File(EditorControlsController.class.getResource("/resources/maps/RoadWorks.map").getPath());
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();
		writeToFile("test/com/simulus/result/LoadMap.txt", true);
		writeLog.flush();
		writeLog.WriteToLog("FX App thread started \n");

		executor.execute(new Runnable() {

			@Override
			public void run() {
				clickButton("Editor");

			}
		});

	}

	@Test
	public void test() throws InterruptedException {
		Thread.sleep(2000);

		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				writeToLog("Initialising Test1...");
				Tile[][] mapTiles = EditorApp.getInstance().getMap().getTiles();
				
			EditorApp.getInstance().loadMap(mapFile);
			
			
			Tile[][] newMapTiles = EditorApp.getInstance().getMap().getTiles();
			
				if(mapTiles != newMapTiles){ //Pass condition 
											 //New MapTiles should be different after loading
					test1Pass = true;
					writeToLog("New Map loaded.");
				}
				else{
					writeToLog("Load map failed.");
				}
			
				writeToLog("Test 1 Completed");
			}
		});

	
		Thread.sleep(3000);
		isTestPassed(test1Pass, 1);


	}

}
