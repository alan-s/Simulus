package com.simulus;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.simulus.controller.EditorControlsController;
import com.simulus.io.MapXML;
import com.simulus.util.Configuration;
import com.simulus.util.MapValidator;
import com.simulus.util.ResourceBuilder;
import com.simulus.util.enums.Direction;
import com.simulus.util.enums.Orientation;
import com.simulus.view.Tile;
import com.simulus.view.intersection.Intersection;
import com.simulus.view.intersection.IntersectionTile;
import com.simulus.view.map.Block;
import com.simulus.view.map.Dirt;
import com.simulus.view.map.Grass;
import com.simulus.view.map.Land;
import com.simulus.view.map.Lane;
import com.simulus.view.map.Map;
import com.simulus.view.map.Road;
import com.simulus.view.map.Water;


/**
 * {@code EditorApp} represents the map editor of the Simulus suite.
 * 
 * It should not be used as a run-target but only be started through the {@link com.simulus.Startup} application.
 */
public class EditorApp extends Application {

	private Stage editorStage;
	private BorderPane rootLayout;
	private Pane canvas;
	private Map editorMap;
	private int gridSize = Configuration.getGridSize();
	private int tileSize = Configuration.getTileSize();
	private boolean mapValidated;
	private Scene scene;

	private Image csrRoadEW = ResourceBuilder.getEWLaneCursor();
	private Image csrRoadNS = ResourceBuilder.getNSLaneCursor();
	private Image csrGrass = ResourceBuilder.getGrassCursor();
	private Image csrDirt = ResourceBuilder.getDirtCursor();
	private Image csrWater = ResourceBuilder.getWaterCursor();
	private Image csrBlock = ResourceBuilder.getBlockCursor();
	private Image csrIntersection = ResourceBuilder.getBoxjunctionCursor();
	private Image csrErase = ResourceBuilder.getEraseCursor();

	private boolean grassSelected = false;
	private boolean dirtSelected = false;
	private boolean waterSelected = false;
	private boolean blockSelected = false;
	private boolean eraserSelected = false;
	private boolean roadVerticalSelected = false;
	private boolean roadHorizontalSelected = false;
	private boolean interSelected = false;

	private int xFixed;
	private int yFixed;
	private boolean firstDrag = true;

	FileChooser fileChooser = new FileChooser();
	FileChooser.ExtensionFilter extFilter;
	File selectedFile;


	private static EditorApp instance;
	EditorControlsController ECC;
	
	private MapValidator mv = new MapValidator();

	public static EditorApp getInstance() {
		return instance;
	}

	public EditorApp() {
		super();
		// Synchronized call to access the application's instance
		synchronized (EditorApp.class) {
			instance = this;
		}
	}


	@Override
	public void start(final Stage editorStage) {

		this.editorStage = editorStage;
		this.editorStage.setTitle("Simulus - Map Editor");
		this.editorStage.setResizable(false);

		initRootLayout();
		showControls();

		this.editorMap = new Map();

		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for (int i = 0; i < editorMap.getTiles().length; i++)
					for (int p = 0; p < editorMap.getTiles()[0].length; p++) {
						if (event.getSceneX() > editorMap.getTiles()[i][p]
								.getBoundsInParent().getMinX()
								&& event.getSceneX() < editorMap.getTiles()[i][p]
										.getBoundsInParent().getMaxX()
										&& event.getSceneY() > editorMap.getTiles()[i][p]
												.getBoundsInParent().getMinY()
												&& event.getSceneY() < editorMap.getTiles()[i][p]
														.getBoundsInParent().getMaxY()) {

							Tile t = editorMap.getTiles()[i][p];

							if (t instanceof Lane || t instanceof IntersectionTile){

							} else {
								if (grassSelected) {
									if (event.isShiftDown()){
										floodFill("grass", i, p);
									}else{
										editorMap.addSingle(new 
												Grass(i*tileSize, p*tileSize, tileSize, tileSize, i, p));
									}
								} else if (dirtSelected) {
									if (event.isShiftDown()){
										floodFill("dirt", i, p);
									}else{
										editorMap.addSingle(new 
												Dirt(i*tileSize, p*tileSize, tileSize, tileSize, i, p));
									}
								} else if (waterSelected) {
									if (event.isShiftDown()){
										floodFill("water", i, p);
									}else{
										editorMap.addSingle(new Water(i*tileSize, p*tileSize, tileSize, tileSize, i, p));
									}
								} else if (interSelected) {
									if (! ((i+4 >= editorMap.getTiles().length) || (p+4 >= editorMap.getTiles().length))
											&& (i > 0 && p > 0)) { // Intersections cannot be placed on the edge of a map
										editorMap.addGroup(new Intersection(editorMap.getTiles()[i][p].getGridPosX(),
												editorMap.getTiles()[i][p].getGridPosY()));
									}
								} else if (roadVerticalSelected) {
									if (!(i+3 >= editorMap.getTiles().length)) {
										editorMap.addGroup(new Road(editorMap.getTiles()[i][p].getGridPosX(),
												editorMap.getTiles()[i][p].getGridPosY(), Orientation.NORTHSOUTH));										
									}
								} else if (roadHorizontalSelected) {
									if (!(p+3 >= editorMap.getTiles().length)) {
										editorMap.addGroup(new Road(editorMap.getTiles()[i][p].getGridPosX(),
												editorMap.getTiles()[i][p].getGridPosY(), Orientation.WESTEAST));										
									}
								}
							}

							if (eraserSelected) {
								if(t instanceof Land && !(t instanceof Block) ) {
									if (event.isShiftDown()){
										floodFill("eraser", i,p);
									}else{
										editorMap.removeSingle(editorMap.getTiles()[i][p]);	
									}
								} else if (t instanceof Lane || t instanceof IntersectionTile || t instanceof Block){
									groupErase(t);
								}
							}else if (blockSelected && t instanceof Lane) {
								editorMap.addSingle(new Lane(i*tileSize, p*tileSize, tileSize, tileSize, i, p, ((Lane) t).getDirection(), ((Lane) t).getLaneNo(), true));
							}
						}
					}
			}
		});


		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				firstDrag = true;
			}
		});

		/*
		 * Drag to draw for Land and road tiles
		 */
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {				
				for (int i = 0; i < editorMap.getTiles().length; i++) {
					for (int p = 0; p < editorMap.getTiles()[0].length; p++) {
						if (event.getSceneX() > editorMap.getTiles()[i][p]
								.getBoundsInParent().getMinX()
								&& event.getSceneX() < editorMap.getTiles()[i][p]
										.getBoundsInParent().getMaxX()
										&& event.getSceneY() > editorMap.getTiles()[i][p]
												.getBoundsInParent().getMinY()
												&& event.getSceneY() < editorMap.getTiles()[i][p]
														.getBoundsInParent().getMaxY()) {

							Tile t = editorMap.getTiles()[i][p];

							if (t instanceof Lane || t instanceof IntersectionTile){

							} else {
								if (grassSelected) {
									editorMap.addSingle(new Grass(i*tileSize, p*tileSize, tileSize, tileSize, i, p));
								} else if (dirtSelected) {
									editorMap.addSingle(new Dirt(i*tileSize, p*tileSize, tileSize, tileSize, i, p));
								} else if (waterSelected) {
									editorMap.addSingle(new Water(i*tileSize, p*tileSize, tileSize, tileSize, i, p));
								}else if (roadVerticalSelected) {
									if (!(i+3 >= editorMap.getTiles().length)) {
										if(firstDrag == true){
											xFixed = i;
											firstDrag = false;}
										else{
											
										}										
										editorMap.addGroup(new Road(editorMap.getTiles()[xFixed][p].getGridPosX(),
												editorMap.getTiles()[xFixed][p].getGridPosY(), Orientation.NORTHSOUTH));
									}
								} else if (roadHorizontalSelected) {
									if (!(p+3 >= editorMap.getTiles().length)) {
										if(firstDrag == true){
											yFixed = p;
											firstDrag = false;}
										else{
											
										}
										editorMap.addGroup(new Road(editorMap.getTiles()[i][yFixed].getGridPosX(),
												editorMap.getTiles()[i][yFixed].getGridPosY(), Orientation.WESTEAST));										
									}
								}
							}

							if (eraserSelected) {
								if(t instanceof Land && !(t instanceof Block) ) {
									editorMap.removeSingle(editorMap.getTiles()[i][p]);									
								} else if (t instanceof Lane || t instanceof IntersectionTile || t instanceof Block){
									groupErase(t);									
								}
							} else if (blockSelected && t instanceof Lane) {
								editorMap.removeSingle(t);
								editorMap.addSingle(new Lane(i*tileSize, p*tileSize, tileSize, tileSize, i, p, ((Lane) t).getDirection(), ((Lane) t).getLaneNo(), true));
							}
						}
					}
				}
			}
		});


		/**
		 * Ticking loop
		 */
		AnimationTimer timer = new AnimationTimer() {
			// When the timer is started, this method loops endlessly

			public void handle(long now) { // Increment the frame number
				getCanvas().getChildren().clear();
				editorMap.drawMap(canvas);
			}
		};
		timer.start();
	}
	
	//On application exit, set instance to null.  Used for correct operation of the Simulator
	@Override
	public void stop() throws Exception {
		super.stop();
		
		instance = null;
	}

	/**
	 * Initialise the root layout
	 */
	private void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(EditorApp.class
					.getResource("view/ui/EditorRootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			canvas = new Pane();
			rootLayout.setCenter(canvas);

			scene = new Scene(rootLayout);
			editorStage.setScene(scene);
			editorStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					Platform.exit();
					System.exit(0);
				}
			});
			editorStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showControls() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(EditorApp.class
					.getResource("view/ui/EditorControls.fxml"));
			AnchorPane controls = (AnchorPane) loader.load();
			ECC = (EditorControlsController) loader.getController();
			rootLayout.setRight(controls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks the group membership of a tile,
	 *  if tile is a Lane or Block then <code>removeGroup()</code> is called on LaneNo 0
	 *  if the tile is an IntersectionTile then removeGroup() is called on the Intersection 
	 * 
	 * @param t the tile whose group is to be erased
	 */
	private void groupErase(Tile t) {
		if (t instanceof Lane || t instanceof Block) {
			Direction dir = Direction.NONE;
			int laneNo = 0;
			if (t instanceof Lane) {
				dir = ((Lane) t).getDirection();
				laneNo = ((Lane) t).getLaneNo();
			} else if (t instanceof Block) {
				dir = ((Block) t).getDirection();
				laneNo = ((Block) t).getLaneNo();
			}
			int x = t.getGridPosX();
			int y = t.getGridPosY();
			Orientation orientation = Orientation.EMPTY;

			if (dir == Direction.NORTH || dir == Direction.SOUTH) {
				while (laneNo > 0) {
					x--;
					laneNo--;
				}
				orientation = Orientation.NORTHSOUTH;
			} else if (dir == Direction.WEST || dir == Direction.EAST) {
				while (laneNo > 0) {
					y--;
					laneNo--;
				}
				orientation = Orientation.WESTEAST;
			}
			editorMap.removeGroup(new Road(x, y, orientation));

		} else if (t instanceof IntersectionTile) {
			editorMap.removeGroup(((IntersectionTile) t).getIntersection());
		}
	}
	
	/**
	 * Flood fill used to quickly tag empty tiles on the map with the currently selected tile type.
	 * Stops when reaching an already present land tile, lane or intersection.
	 * Eraser tool removes any land tiles enclosed by the map edges lane or intersection.
	 * 
	 * @param tFill the land type to fill the empty area with {grass, dirt, water}
	 * @param xIn the starting X grid coordinate
	 * @param yIn the starting Y grid coordinate
	 */
	private void floodFill(String tFill, int xIn, int yIn){
		Tile[][] mapTiles = this.editorMap.getTiles();
		boolean[][] visited = new boolean[mapTiles.length][mapTiles.length];

		Tile t = this.editorMap.getTiles()[xIn][yIn];
		LinkedList<Tile> queue = new LinkedList<Tile>();
		queue.add(t);

		while (!queue.isEmpty()) {
			t = queue.getLast();
			queue.removeLast();
			xIn = t.getGridPosX();
			yIn = t.getGridPosY();

			if (t instanceof Land && tFill.equals("eraser")) {
				editorMap.removeSingle(editorMap.getTiles()[xIn][yIn]);
				visited[xIn][yIn] = true;

				if (xIn - 1 >= 0) {
					if (visited[xIn - 1][yIn] == false) {
						t = this.editorMap.getTiles()[xIn - 1][yIn];
						queue.add(t);
					}
				}
				if (xIn + 1 < mapTiles.length) {
					if (visited[xIn + 1][yIn] == false) {
						t = this.editorMap.getTiles()[xIn + 1][yIn];
						queue.add(t);
					}
				}
				if (yIn - 1 >= 0) {
					if (visited[xIn][yIn - 1] == false) {
						t = this.editorMap.getTiles()[xIn][yIn - 1];
						queue.add(t);
					}
				}
				if (yIn + 1 < mapTiles.length) {
					if (visited[xIn][yIn + 1] == false) {
						t = this.editorMap.getTiles()[xIn][yIn + 1];
						queue.add(t);
					}
				}
			}

			else if (!(t instanceof Lane || t instanceof Land || t instanceof IntersectionTile)) {

				switch (tFill) {
				case "grass":
					editorMap.addSingle(new Grass(xIn * tileSize, yIn
							* tileSize, tileSize, tileSize, xIn, yIn));
					break;
				case "dirt":
					editorMap.addSingle(new Dirt(xIn * tileSize,
							yIn * tileSize, tileSize, tileSize, xIn, yIn));
					break;
				case "water":
					editorMap.addSingle(new Water(xIn * tileSize, yIn
							* tileSize, tileSize, tileSize, xIn, yIn));
					break;
				}

				visited[xIn][yIn] = true;

				if (xIn - 1 >= 0) {
					if (visited[xIn - 1][yIn] == false) {
						t = this.editorMap.getTiles()[xIn - 1][yIn];
						queue.add(t);
					}
				}
				if (xIn + 1 < mapTiles.length) {
					if (visited[xIn + 1][yIn] == false) {
						t = this.editorMap.getTiles()[xIn + 1][yIn];
						queue.add(t);
					}
				}
				if (yIn - 1 >= 0) {
					if (visited[xIn][yIn - 1] == false) {
						t = this.editorMap.getTiles()[xIn][yIn - 1];
						queue.add(t);
					}
				}
				if (yIn + 1 < mapTiles.length) {
					if (visited[xIn][yIn + 1] == false) {
						t = this.editorMap.getTiles()[xIn][yIn + 1];
						queue.add(t);
					}
				}

			}
		}
	}

	
	/**
	 * Using {@link com.simulus.io.MapXML} class to read a Simulus map file and update the {@link com.simulus.controller.EditorControlsController}
	 * textfields.
	 * 
	 * @param file The source map file to load
	 */
	public void loadMap(File file){
		MapXML mxml = new MapXML();
		mxml.readXML(file);
		ECC.setMapName(mxml.mapName);
		ECC.setMapDate(mxml.mapCreationDate);
		ECC.setMapDesc(mxml.mapDescription);
		ECC.setMapAuthor(mxml.mapAuthor);
		ECC.setGridSize(mxml.numOfTiles);
		gridSize = Configuration.getGridSize();
		tileSize = Configuration.getTileSize();
		editorMap.loadEditorMap(file);
	}

	/**
	 * Using {@link com.simulus.io.MapXML} class to save a map
	 * 
	 * @param fileLocation the destination output file
	 */
	public void saveMap(String fileLocation){
		MapXML mxml = new MapXML();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
	
		mxml.writeXML(editorMap.getTiles(), fileLocation, ECC.getMapName(), dateFormat.format(date),
				ECC.getMapDesc(), ECC.getMapAuthor(), 800, Configuration.getGridSize(), mapValidated);

	}
	
	
	/**
	 * clears the grid and textfields on the import/export pane allowing a new map to be drawn
	 */
	public void clearMap(){
		gridSize = Configuration.getGridSize();
		tileSize = Configuration.getTileSize();
		this.editorMap = new Map();
		ECC.setMapName("");
		ECC.setMapDate("");
		ECC.setMapDesc("");
		ECC.setMapAuthor("");
	}
	
	
	public void selectButton(Button b) {

		switch (b.getId()) {
		case "grassButton":
			grassSelected = true;
			dirtSelected = false;
			waterSelected = false;
			blockSelected = false;
			eraserSelected = false;
			roadVerticalSelected = false;
			roadHorizontalSelected = false;
			interSelected = false;
			canvas.setCursor(new ImageCursor(csrGrass));
			break;
		case "dirtButton":
			grassSelected = false;
			dirtSelected = true;
			waterSelected = false;
			eraserSelected = false;
			blockSelected = false;
			roadVerticalSelected = false;
			roadHorizontalSelected = false;
			interSelected = false;
			canvas.setCursor(new ImageCursor(csrDirt));
			break;
		case "waterButton":
			grassSelected = false;
			dirtSelected = false;
			waterSelected = true;
			eraserSelected = false;
			blockSelected = false;
			roadVerticalSelected = false;
			roadHorizontalSelected = false;
			interSelected = false;
			canvas.setCursor(new ImageCursor(csrWater));
			break;
		case "blockButton":
			grassSelected = false;
			dirtSelected = false;
			waterSelected = false;
			eraserSelected = false;
			blockSelected = true;
			roadVerticalSelected = false;
			roadHorizontalSelected = false;
			interSelected = false;
			canvas.setCursor(new ImageCursor(csrBlock));
			break;
		case "eraserButton":
			grassSelected = false;
			dirtSelected = false;
			waterSelected = false;
			eraserSelected = true;
			blockSelected = false;
			roadVerticalSelected = false;
			roadHorizontalSelected = false;
			interSelected = false;
			canvas.setCursor(new ImageCursor(csrErase));
			break;
		case "roadVerticalButton":
			grassSelected = false;
			dirtSelected = false;
			waterSelected = false;
			eraserSelected = false;
			blockSelected = false;
			roadVerticalSelected = true;
			roadHorizontalSelected = false;
			interSelected = false;
			canvas.setCursor(new ImageCursor(csrRoadNS));
			break;
		case "roadHorizontalButton":
			grassSelected = false;
			dirtSelected = false;
			waterSelected = false;
			eraserSelected = false;
			blockSelected = false;
			roadVerticalSelected = false;
			roadHorizontalSelected = true;
			interSelected = false;
			canvas.setCursor(new ImageCursor(csrRoadEW));
			break;
		case "interButton":
			grassSelected = false;
			dirtSelected = false;
			waterSelected = false;
			eraserSelected = false;
			blockSelected = false;
			roadVerticalSelected = false;
			roadHorizontalSelected = false;
			interSelected = true;
			canvas.setCursor(new ImageCursor(csrIntersection));
			break;
		case "openMapButton":
			openMapDialog();
			break;
		case "saveMapButton":
			saveMapDialog();
			break;
		case "clearMapButton":
			clearMap();
			break;
		case "validateMapButton":
			mapValidated = mv.validateMap(this.editorMap.getTiles());
			if (mapValidated) {
				validationPassDialog();
			} else {
				validationFailDialog();
			}
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * Dialog displayed when validation has failed
	 */
	public void validationFailDialog(){
    	Alert alert = new Alert(AlertType.WARNING);
    	alert.initOwner(EditorApp.getInstance().getPrimaryStage());
    	alert.setTitle("Map Validation Failed");
    	alert.setHeaderText("This map has not passed validation! \n It can be saved but cannot be used in the simulator.");
    	alert.showAndWait();
	}
	
	/**
	 * Dialog displayed when validation has passed
	 */
	public void validationPassDialog(){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.initOwner(EditorApp.getInstance().getPrimaryStage());
    	alert.setTitle("Map Validation Passed");
    	alert.setHeaderText("This map has passed validation! \n It can be saved and used in the simulator.");
    	alert.showAndWait();
	}

	/**
	 * File chooser dialog to allow the user to selecte a location to save the map file to
	 * 
	 * @return the output file selected
	 */
	public File saveMapDialog(){
		mapValidated = mv.validateMap(this.editorMap.getTiles());
		
		if(!mapValidated){
			validationFailDialog();
		} 
		
		fileChooser = new FileChooser();
		fileChooser.setTitle("Save Map XML...");
		extFilter = new FileChooser.ExtensionFilter("Simulus Map Files (*.map)", "*.map");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialFileName(ECC.getMapName());
		File dir = new File(System.getProperty("user.home") + "/Simulus/maps");
        if(dir.canRead())
        	fileChooser.setInitialDirectory(dir);
        else fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		selectedFile = fileChooser.showSaveDialog(editorStage);
		if (selectedFile != null) {
			if(!selectedFile.getName().contains(".map"))
				selectedFile = new File(selectedFile.getAbsolutePath() + ".map");
			saveMap(selectedFile.getPath());	
		}
		return selectedFile;
	}

	/**
	 * Dialog to allow the user to select a map file to load
	 */
	public void openMapDialog(){
		fileChooser = new FileChooser();
		fileChooser.setTitle("Open Map XML...");
		extFilter = new FileChooser.ExtensionFilter("Simulus Map Files (*.map)", "*.map");
		fileChooser.getExtensionFilters().add(extFilter);
		File dir = new File(System.getProperty("user.home") + "/Simulus/maps");
        if(dir.canRead())
        	fileChooser.setInitialDirectory(dir);
        else fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		selectedFile = fileChooser.showOpenDialog(editorStage);
		if (selectedFile != null) {
			loadMap(selectedFile);
		}
	}


	public Stage getPrimaryStage() {
		return editorStage;
	}
	
	public BorderPane getRootLayout(){
		return rootLayout;
	}
	
	public void getFloodFill(String tFill, int xIn, int yIn){
		floodFill(tFill, xIn, yIn);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public int getGridSize() {
		return gridSize;
	}

	public Pane getCanvas() {
		return canvas;
	}

	public Map getMap() {
		return editorMap;
	}
	
	public boolean getMapValidated(){
		mapValidated = mv.validateMap(this.editorMap.getTiles());
		return mapValidated;
	}


}
