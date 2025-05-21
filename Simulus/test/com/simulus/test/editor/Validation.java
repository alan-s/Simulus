package com.simulus.test.editor;

import com.simulus.util.Configuration;
import com.simulus.util.MapValidator;
import com.simulus.util.enums.Direction;
import com.simulus.util.enums.Orientation;
import com.simulus.view.intersection.Intersection;
import com.simulus.view.map.Lane;
import com.simulus.view.map.Road;
import com.simulus.EditorApp;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import org.jemmy.fx.SceneDock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.simulus.test.TestCaseBaseCode;
import com.simulus.view.Tile;
import com.simulus.view.TileGroup;

/**
 * {@code Validation}  tests the functionality of validation
 * when a map is constructed correctly
 */
public class Validation extends TestCaseBaseCode {

	private boolean test1Pass;
	private boolean test2Pass;
	private int tileSize = Configuration.getTileSize();
	private ArrayList<Lane> entryPoints = new ArrayList<>();

	private Tile[][] tiles = new Tile[Configuration.getGridSize()][Configuration.getGridSize()];



	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		appThread.launchApp();
		Thread.sleep(1000);
		scene = new SceneDock();
		writeToFile("test/com/simulus/result/Validation.txt", true);
		writeLog.flush();
		writeLog.WriteToLog("FX App thread started \n");

		executor.execute(new Runnable() {

			@Override
			public void run() {
				clickButton("Editor");

			}
		});

	}

	@After
	public void tearDown() throws Exception {
		Thread.sleep(3000);
	}

	/*
	 * Add all the lanes as a group
	 */
	private void addGroup(TileGroup g) {

		List<Tile> l = g.getTiles();
		for (Tile t : l) {
			tiles[t.getGridPosX()][t.getGridPosY()] = t;

			if (g instanceof Road) {
				if (t.getGridPosX() == tiles.length - 1
						&& ((Road) g).getOrientation() == Orientation.WESTEAST
						&& ((Lane) t).getDirection() == Direction.WEST) {
					entryPoints.add((Lane) t);
				} else if (t.getGridPosX() == 0
						&& ((Road) g).getOrientation() == Orientation.WESTEAST
						&& ((Lane) t).getDirection() == Direction.EAST) {
					entryPoints.add((Lane) t);
				} else if (t.getGridPosY() == tiles.length - 1
						&& ((Road) g).getOrientation() == Orientation.NORTHSOUTH
						&& ((Lane) t).getDirection() == Direction.NORTH) {
					entryPoints.add((Lane) t);
				} else if (t.getGridPosY() == 0
						&& ((Road) g).getOrientation() == Orientation.NORTHSOUTH
						&& ((Lane) t).getDirection() == Direction.SOUTH) {
					entryPoints.add((Lane) t);
				}
			}
		}
	}

	/*
	 * Create a basic hash map 
	 */
	private void createHashStyleMap() {
		// init all tiles
		for (int i = 0; i < tiles.length; i++) {
			for (int p = 0; p < tiles[0].length; p++) {
				tiles[i][p] = new Tile(i * tileSize, p * tileSize, tileSize,
						tileSize, i, p);
			}
		}
		//Construct the road from i position = 0
		//Road is created at a boundary 
		//Hence an valid map
		for (int i = 0; i < tiles.length; i++) {
			addGroup(new Road(i, 7, Orientation.WESTEAST));
			addGroup(new Road(7, i, Orientation.NORTHSOUTH));

			addGroup(new Road(i, 18, Orientation.WESTEAST));
			addGroup(new Road(18, i, Orientation.NORTHSOUTH));

			addGroup(new Road(i, 29, Orientation.WESTEAST));
			addGroup(new Road(29, i, Orientation.NORTHSOUTH));
		}

		addGroup(new Intersection(7, 7));
		addGroup(new Intersection(7, 18));
		addGroup(new Intersection(7, 29));

		addGroup(new Intersection(18, 7));
		addGroup(new Intersection(18, 18));
		addGroup(new Intersection(18, 29));

		addGroup(new Intersection(29, 7));
		addGroup(new Intersection(29, 18));
		addGroup(new Intersection(29, 29));
	}

	@Test
	public void test1() throws InterruptedException {
		Thread.sleep(2000);

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				writeToLog("Initialising Test1...");

				createHashStyleMap();

				EditorApp.getInstance().getMap().setTiles(tiles);
				EditorApp.getInstance().getMap().drawMap(EditorApp.getInstance().getCanvas());

				test1Pass = true;
				writeToLog("Test 1 Completed");
				EditorApp.getInstance().getMap().drawMap(EditorApp.getInstance().getCanvas());

			}
		});


		Thread.sleep(3000);
		isTestPassed(test1Pass, 1);

	}
	@Test
	public void test2() throws InterruptedException {
		Thread.sleep(2000);

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				MapValidator map = new MapValidator();
				boolean valid = map.validateMap(EditorApp.getInstance().getMap().getTiles()); //Validation should fail

				if (valid == true){
					test2Pass = true;
				}
				writeToLog("Test 2 Completed");
			}
		});


		Thread.sleep(4000);
		isTestPassed(test2Pass, 2);

	}




}
