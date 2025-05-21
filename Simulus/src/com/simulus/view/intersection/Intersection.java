package com.simulus.view.intersection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import com.simulus.controller.SimulationController;
import com.simulus.util.Configuration;
import com.simulus.util.ResourceBuilder;
import com.simulus.util.enums.Direction;
import com.simulus.util.enums.TurningDirection;
import com.simulus.view.Tile;
import com.simulus.view.TileGroup;
import com.simulus.view.map.Map;

/**
 * Contains all tiles belonging to the intersection.
 * Is a runnable, allowing it to change the traffic lights according to the switchtime.
 */
public class Intersection extends Group implements TileGroup, Runnable {
	
	public IntersectionTile[][] tiles = new IntersectionTile[4][4];
	private long weSwitchTime;
	private long nsSwitchTime;
	private static int tileSize = Configuration.getTileSize();
	private ArrayList<CustomPath> turningPaths;
	private boolean nsAllowRight = Math.random() > 0.5d;
	private boolean weAllowRight = Math.random() > 0.5d;
	private boolean hasTrafficLights = true;
	public static final double arcDistanceShort = 90*(Math.PI/180)*(tileSize/2);
	public static final double arcDistanceMedium = 90*(Math.PI/180)*(tileSize*1.5);
	public static final double arcDistanceLong = 90*(Math.PI/180)*(tileSize*2.5);
	public static final double arcDistanceVeryLong = 90*(Math.PI/180)*(tileSize*3.5);
	public static final double straightDistance = 4*tileSize;
	
	/**
	 * @param xPos x coordinate of the top left tile of the intersection in the grid
	 * @param yPos y coordinate of the top left tile of the intersection in the grid
	 */
	public Intersection(int xPos, int yPos) {
		tileSize = Configuration.getTileSize();
		nsSwitchTime = (long) (3000 + Math.random()*3000);
		weSwitchTime = (long) (3000 + Math.random()*3000);
		turningPaths = new ArrayList<>();
		
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = new IntersectionTile((xPos+i)*tileSize, (yPos+j)*tileSize, tileSize, tileSize, xPos+i, yPos+j);
				tiles[i][j].getFrame().setFill(new ImagePattern(ResourceBuilder.getBoxjunctionTexture()));
				
				((IntersectionTile) tiles[i][j]).setIntersection(this);
				this.getChildren().add(SimulationController.getInstance().getMap().getTiles()[i][j]);
			}
		}
	}
	
	/**
	 * Opens the configuration dialog for this intersection.
	 */
	public void configureDialog() {
		if(!hasTrafficLights)
			return;
		
		IntersectionConfigDialog dialog = new IntersectionConfigDialog(toString(), nsSwitchTime, weSwitchTime); 
		Optional<java.util.Map<String, Long>> result = dialog.showAndWait();
		
		if(result.isPresent()) {
			nsSwitchTime = result.get().get("nsphase");
			weSwitchTime = result.get().get("wephase");
		}
	}

	/**
	 * Computes all turning paths available at this intersection.
	 * The paths are added to the intersection tile they start in.
	 * @param m The map of tiles for referencing the start and end tile
	 */
	public void addTurningPaths(Tile[][] m){

		//Left, top
		IntersectionTile t = tiles[0][0];
		//Turn left - 0
		t.getTurningPaths().add(new CustomPath(	TurningDirection.LEFT,arcDistanceShort, m[t.getGridPosX()-1][t.getGridPosY()], m[t.getGridPosX()][t.getGridPosY()-1], Direction.NORTH, new MoveTo(t.getX(), t.getCenterY()),				
											new ArcTo(	tileSize/2, tileSize/2,
														0.0d,
														tiles[0][0].getCenterX(), tiles[0][0].getY(),
														false, false)));
		
		//Turn right - 1
		t.getTurningPaths().add(new CustomPath(	TurningDirection.RIGHT,arcDistanceVeryLong, m[t.getGridPosX()-1][t.getGridPosY()], m[t.getGridPosX()+3][t.getGridPosY()+4], Direction.SOUTH, new MoveTo(t.getX(), t.getCenterY()),				
											new ArcTo(	tileSize*3.5, tileSize*3.5,
														0.0d,
														tiles[3][3].getCenterX(), tiles[3][3].getY() + tileSize,
														false, true)));
		
		//Straight - 2
		t.getTurningPaths().add(new CustomPath(	TurningDirection.STRAIGHT, straightDistance, m[t.getGridPosX()-1][t.getGridPosY()], m[t.getGridPosX()+4][t.getGridPosY()], Direction.EAST,new MoveTo(t.getX(), t.getCenterY()),				
											new LineTo(tiles[3][0].getX() + tileSize, tiles[3][0].getCenterY())));
		
		//Left, second from top
		t = tiles[0][1];
		//Turn right - 3 
		t.getTurningPaths().add(new CustomPath(TurningDirection.RIGHT,	arcDistanceLong, m[t.getGridPosX()-1][t.getGridPosY()], m[t.getGridPosX()+2][t.getGridPosY()+3], Direction.SOUTH,new MoveTo(t.getX(), t.getCenterY()),				
											new ArcTo(	tileSize*2.5, tileSize*2.5,
														0.0d,
														tiles[2][3].getCenterX(), tiles[2][3].getY() + tileSize,
														false, true)));
		
		//Turn left - 4
		t.getTurningPaths().add(new CustomPath(TurningDirection.LEFT,	arcDistanceMedium, m[t.getGridPosX()-1][t.getGridPosY()], m[t.getGridPosX()+1][t.getGridPosY()-2], Direction.NORTH,new MoveTo(t.getX(), t.getCenterY()),				
											new ArcTo(	tileSize*1.5, tileSize*1.5,
														0.0d,
														tiles[1][0].getCenterX(), tiles[1][0].getY(),
														false, false)));
		
		//Straight - 5
		t.getTurningPaths().add(new CustomPath(	TurningDirection.STRAIGHT,straightDistance, m[t.getGridPosX()-1][t.getGridPosY()], m[t.getGridPosX()+4][t.getGridPosY()], Direction.EAST,new MoveTo(t.getX(), t.getCenterY()),				
											new LineTo(tiles[3][1].getX() + tileSize, tiles[3][1].getCenterY())));

		
		//Bottom, leftmost
		t = tiles[0][3];
		//Turn left - 6
		t.getTurningPaths().add(new CustomPath(TurningDirection.LEFT,arcDistanceShort, m[t.getGridPosX()][t.getGridPosY()+1], m[t.getGridPosX()-1][t.getGridPosY()], Direction.WEST, new MoveTo(t.getCenterX(), t.getY() + tileSize),				
											new ArcTo(	tileSize/2, tileSize/2,
														0.0d,
														t.getX(), t.getCenterY(),
														false, false)));

		//Turn right - 7
		t.getTurningPaths().add(new CustomPath(TurningDirection.RIGHT, arcDistanceVeryLong, m[t.getGridPosX()][t.getGridPosY()+1], m[t.getGridPosX()+4][t.getGridPosY()-3], Direction.EAST, new MoveTo(t.getCenterX(), t.getY() + tileSize),				
											new ArcTo(	tileSize*3.5, tileSize*3.5,
														0.0d,
														tiles[3][0].getX() + tileSize, tiles[3][0].getCenterY(),
														false, true)));
		//Straight - 8
		t.getTurningPaths().add(new CustomPath( TurningDirection.STRAIGHT,straightDistance, m[t.getGridPosX()][t.getGridPosY()+1], m[t.getGridPosX()][t.getGridPosY()-4], Direction.NORTH, new MoveTo(t.getCenterX(), t.getY() + tileSize),				
											new LineTo(t.getCenterX(), tiles[0][0].getY())));

		//Bottom, second from left
		t = tiles[1][3];
		//Turn right - 9
		t.getTurningPaths().add(new CustomPath(TurningDirection.RIGHT,arcDistanceLong, m[t.getGridPosX()][t.getGridPosY()+1], m[t.getGridPosX()+3][t.getGridPosY()-2],Direction.EAST, new MoveTo(t.getCenterX(), t.getY() + tileSize),				
											new ArcTo(	tileSize*2.5, tileSize*2.5,
														0.0d,
														tiles[3][1].getX() + tileSize, tiles[3][1].getCenterY(),
														false, true)));
		
		//Turn left - 10
		t.getTurningPaths().add(new CustomPath(TurningDirection.LEFT,arcDistanceMedium, m[t.getGridPosX()][t.getGridPosY()+1], m[t.getGridPosX()-2][t.getGridPosY()-1],Direction.WEST, new MoveTo(t.getCenterX(), t.getY() + tileSize),				
											new ArcTo(	tileSize*1.5, tileSize*1.5,
														0.0d,
														tiles[0][2].getX(), tiles[0][2].getCenterY(),
														false, false)));

		//Straight - 11
		t.getTurningPaths().add(new CustomPath(TurningDirection.STRAIGHT,straightDistance, m[t.getGridPosX()][t.getGridPosY()+1], m[t.getGridPosX()][t.getGridPosY()-4], Direction.NORTH,new MoveTo(t.getCenterX(), t.getY() + tileSize),				
											new LineTo(t.getCenterX(), tiles[0][0].getY())));

		//Top, second from right
		t = tiles[2][0];
		//Turn right - 12
		t.getTurningPaths().add(new CustomPath(TurningDirection.RIGHT,arcDistanceLong, m[t.getGridPosX()][t.getGridPosY()-1], m[t.getGridPosX()-3][t.getGridPosY()+2],Direction.WEST,new MoveTo(t.getCenterX(), t.getY()),				
											new ArcTo(	tileSize*2.5, tileSize*2.5,
														0.0d,
														tiles[0][2].getX(), tiles[0][2].getCenterY(),
														false, true)));
		//Straight - 13
		t.getTurningPaths().add(new CustomPath(TurningDirection.STRAIGHT,straightDistance, m[t.getGridPosX()][t.getGridPosY()-1], m[t.getGridPosX()][t.getGridPosY()+4],Direction.SOUTH,new MoveTo(t.getCenterX(), t.getY()),				
											new LineTo(tiles[2][3].getCenterX(), tiles[2][3].getY() + tileSize)));

		//Turn left - 14
		t.getTurningPaths().add(new CustomPath(TurningDirection.LEFT, arcDistanceMedium, m[t.getGridPosX()][t.getGridPosY()-1], m[t.getGridPosX()+2][t.getGridPosY()+1], Direction.EAST, new MoveTo(t.getCenterX(), t.getY()),				
				new ArcTo(	tileSize*1.5, tileSize*1.5,
							0.0d,
							tiles[3][1].getX() + tileSize, tiles[3][1].getCenterY(),
							false, false)));
		
		//Top, rightmost
		t = tiles[3][0];
		//Turn left - 15
		t.getTurningPaths().add(new CustomPath(TurningDirection.LEFT,arcDistanceShort, m[t.getGridPosX()][t.getGridPosY()-1], m[t.getGridPosX()+1][t.getGridPosY()], Direction.EAST,new MoveTo(t.getCenterX(), t.getY()),				
											new ArcTo(	tileSize/2, tileSize/2,
														0.0d,
														tiles[3][0].getX() + tileSize, tiles[3][0].getCenterY(),
														false, false)));
		//Turn right - 16
		t.getTurningPaths().add(new CustomPath(TurningDirection.RIGHT,arcDistanceVeryLong, m[t.getGridPosX()][t.getGridPosY()-1], m[t.getGridPosX()-4][t.getGridPosY()+3], Direction.WEST,new MoveTo(t.getCenterX(), t.getY()),				
											new ArcTo(	tileSize*3.5, tileSize*3.5,
														0.0d,
														tiles[0][3].getX(), tiles[0][3].getCenterY(),
														false, true)));
	
		
		//Straight - 17
		t.getTurningPaths().add(new CustomPath(TurningDirection.STRAIGHT,straightDistance, m[t.getGridPosX()][t.getGridPosY()-1], m[t.getGridPosX()][t.getGridPosY()+4],Direction.SOUTH,new MoveTo(t.getCenterX(), t.getY()),				
											new LineTo(tiles[3][3].getCenterX(), tiles[3][3].getY() + tileSize)));
		
		
		//Right, second from bottom
		t = tiles[3][2];
		//Turn right - 18
		t.getTurningPaths().add(new CustomPath(TurningDirection.RIGHT,arcDistanceLong, m[t.getGridPosX()+1][t.getGridPosY()], m[t.getGridPosX()-2][t.getGridPosY()-3],Direction.NORTH,new MoveTo(t.getX()+tileSize, t.getCenterY()),				
											new ArcTo(	tileSize*2.5, tileSize*2.5,
														0.0d,
														tiles[1][0].getCenterX(), tiles[1][0].getY(),
														false, true)));
		
		//Turn left - 19
		t.getTurningPaths().add(new CustomPath(TurningDirection.LEFT,arcDistanceMedium, m[t.getGridPosX()+1][t.getGridPosY()], m[t.getGridPosX()-1][t.getGridPosY()+2],Direction.SOUTH, new MoveTo(t.getX()+tileSize, t.getCenterY()),				
											new ArcTo(	tileSize*1.5, tileSize*1.5,
														0.0d,
														tiles[2][3].getCenterX(), tiles[2][3].getY() + tileSize,
														false, false)));
		
		//Straight - 20
		t.getTurningPaths().add(new CustomPath(TurningDirection.STRAIGHT,straightDistance, m[t.getGridPosX()+1][t.getGridPosY()], m[t.getGridPosX()-4][t.getGridPosY()],Direction.WEST,new MoveTo(t.getX() + tileSize, t.getCenterY()),				
											new LineTo(tiles[0][3].getX(), t.getCenterY())));
		
		//Right, bottom
		t = tiles[3][3];
		//Turn left - 21
		t.getTurningPaths().add(new CustomPath(TurningDirection.LEFT,arcDistanceShort, m[t.getGridPosX()+1][t.getGridPosY()], m[t.getGridPosX()][t.getGridPosY()+1],Direction.SOUTH,new MoveTo(t.getX()+tileSize, t.getCenterY()),				
											new ArcTo(	tileSize/2, tileSize/2,
														0.0d,
														tiles[3][3].getCenterX(), tiles[3][3].getY()+ tileSize,
														false, false)));
		
		//Turn right - 22
		t.getTurningPaths().add(new CustomPath(TurningDirection.RIGHT,arcDistanceVeryLong, m[t.getGridPosX()+1][t.getGridPosY()], m[t.getGridPosX()-3][t.getGridPosY()-4],Direction.NORTH, new MoveTo(t.getX()+tileSize, t.getCenterY()),				
											new ArcTo(	tileSize*3.5, tileSize*3.5,
														0.0d,
														tiles[0][0].getCenterX(), tiles[0][0].getY(),
														false, true)));
		
		//Straight - 23
		t.getTurningPaths().add(new CustomPath(TurningDirection.STRAIGHT,straightDistance, m[t.getGridPosX()+1][t.getGridPosY()], m[t.getGridPosX()-4][t.getGridPosY()],Direction.WEST,new MoveTo(t.getX() + tileSize, t.getCenterY()),				
											new LineTo(tiles[0][3].getX(), t.getCenterY())));
		
		
		//Add all paths to the intersection's list
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles.length; j++) {
				IntersectionTile t1 = tiles[i][j];
				turningPaths.addAll(t1.getTurningPaths());
				
				//Add them to the tile as well
				for(CustomPath p : t1.getTurningPaths()) {
					if(!p.isUnavailable()) {
						p.setStroke(Color.TRANSPARENT);
						p.setFill(Color.TRANSPARENT);
						t1.getChildren().add(p);
					}
				}
					
					
			}
		}
	}
	
	private void flipPaths(IntersectionTile t, boolean allowRight) {
		
		for(CustomPath p : t.getTurningPaths()) {
			switch(p.getTurn()) {
			case LEFT:
				p.setActive(true);
				break;
			case RIGHT:
				p.setActive(nsAllowRight);
				break;
			case STRAIGHT:
				boolean intersects = false;
				for(CustomPath p2 : turningPaths) {
					if( !((Path) Shape.intersect(p, p2)).getElements().isEmpty() 
							&& !p.isUnavailable() && !p2.isUnavailable())
						intersects = true;
				}
				if(intersects)
					p.setActive(!nsAllowRight);
				else p.setActive(nsAllowRight);
				break;
			default:
				break;
			
			}
		}	
	}
	
	/*
	 * De-/Activates paths in order to not allow cars to go straight, while the opposite
	 * direction's cars turn right.
	 */
	private void flipAllowRight() {
		nsAllowRight = !nsAllowRight;
		weAllowRight = !weAllowRight;
		
    	//Northsouth	
		flipPaths(tiles[0][3], nsAllowRight);
		flipPaths(tiles[1][3], nsAllowRight);
		flipPaths(tiles[2][0], nsAllowRight);
		flipPaths(tiles[3][0], nsAllowRight);
    	
    	//Westeast
		flipPaths(tiles[0][0], weAllowRight);
		flipPaths(tiles[0][1], weAllowRight);
		flipPaths(tiles[3][2], weAllowRight);
		flipPaths(tiles[3][3], weAllowRight);
		
        //Update path representation in debug
    	if(SimulationController.getInstance().isDebug()) {
    		hideAllPaths();
    		showAllPaths();
    	}
    }
    
	
	/**
	 * Visualise all paths - used in debug mode.
	 */
	public void showAllPaths() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles.length; j++) {
				IntersectionTile t = tiles[i][j];
                t.getFrame().setFill(Color.TRANSPARENT);
				
                for(CustomPath p : t.getTurningPaths()) {
					if(p.getActive() && !p.isUnavailable()) {
						p.setStroke(Color.GREEN);
						p.setFill(Color.TRANSPARENT);
					}
				}
			}
		}
					
	}

	/**
	 * Hides the visualisation of the paths through an intersection. 
	 * Is used when leaving the debug mode.
	 */
    public void hideAllPaths() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
            	IntersectionTile t = tiles[i][j];
                t.getFrame().setFill(new ImagePattern(ResourceBuilder.getBoxjunctionTexture()));
                Iterator<CustomPath> it = t.getTurningPaths().iterator();
                while(it.hasNext()) {
                	it.next().setStroke(Color.TRANSPARENT);
                }
            }
        }
    }
    
    /**
     * Returns a the tile of the intersection at the given position.
     * @param posX The x-coordinate of the desired tile.
     * @param posY The y-coordinate of the desired tile.
     * @return The tile of the intersection at the given position.
     */
    public IntersectionTile getTileAt(double posX, double posY){
    	for(int i = 0; i < tiles.length; i++)
    		for(int j = 0; j < tiles[0].length; j++)
    			if(posX < tiles[i][j].getBoundsInParent().getMaxX() && posX > tiles[i][j].getBoundsInParent().getMinX()
    					&& posY < tiles[i][j].getBoundsInParent().getMaxY() && posY > tiles[i][j].getBoundsInParent().getMinY())
    				return tiles[i][j];
    	return null;
    }
    
    /**
     * The run method to switch activate and switch the traffic lights
     */
	@Override
	public void run() {
		
		//Check whether the intersection has an active straight path
		//if not, it does not need trafficlights
		
		boolean hasStraightPath = false;
		boolean hasCurvedPath = false;
		for(CustomPath p : turningPaths) {
			if(p.getActive()) {
				if(p.getTurn() == TurningDirection.STRAIGHT)
					hasStraightPath = true;
				else if(p.getTurn() == TurningDirection.LEFT || p.getTurn() == TurningDirection.RIGHT)	
					hasCurvedPath = true;
			}
		}
		//Store whether this intersections need traffic lights or not
		hasTrafficLights = hasStraightPath && hasCurvedPath;
		
		if(!hasTrafficLights) {			
			//Clear the intersections every 1.5 seconds
			while(!Thread.currentThread().isInterrupted()){
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					return;
				}
				for(Tile[] ts : tiles)
					for(Tile t : ts)
						t.setOccupied(false);
			}
			
			return;
		}
		
		while(!Thread.currentThread().isInterrupted()){
			try{
			//change from turning right to going straight and vv
			flipAllowRight(); 
			
			//Westeast red
			Platform.runLater(() ->{
				
				Map map = SimulationController.getInstance().getMap();

                map.setRedTrafficLight(tiles[0][0].getGridPosX() - 1, tiles[0][0].getGridPosY(), Direction.EAST);
                map.setRedTrafficLight(tiles[0][0].getGridPosX() - 1, tiles[0][1].getGridPosY(), Direction.EAST);

                map.setRedTrafficLight(tiles[3][2].getGridPosX() + 1, tiles[3][2].getGridPosY(), Direction.WEST);
                map.setRedTrafficLight(tiles[3][3].getGridPosX() + 1, tiles[3][3].getGridPosY(), Direction.WEST);
                
                	
			});

			//red lights overlap for 1.75seconds to allow vehicles to clear the junction
			Thread.sleep(1750);
			for(Tile[] ts : tiles)
				for(Tile t : ts)
					t.setOccupied(false);
            
			//Northsouth green
            Platform.runLater(() ->{
            	
            	Map map = SimulationController.getInstance().getMap();

            	map.setGreenTrafficLight(tiles[0][3].getGridPosX(), tiles[0][3].getGridPosY() + 1);
                map.setGreenTrafficLight(tiles[1][3].getGridPosX(), tiles[1][3].getGridPosY() + 1);

                map.setGreenTrafficLight(tiles[2][0].getGridPosX(), tiles[2][0].getGridPosY() - 1);
                map.setGreenTrafficLight(tiles[3][0].getGridPosX(), tiles[3][0].getGridPosY() - 1);
                
                
                
			});
	    	
			Thread.sleep(nsSwitchTime);
	    		
			//Northsouth red
	    	Platform.runLater(() ->{
	    		  Map map = SimulationController.getInstance().getMap();
	    		
                  map.setRedTrafficLight(tiles[0][3].getGridPosX(), tiles[0][3].getGridPosY() + 1, Direction.NORTH);
                  map.setRedTrafficLight(tiles[1][3].getGridPosX(), tiles[1][3].getGridPosY() + 1, Direction.NORTH);

                  map.setRedTrafficLight(tiles[2][0].getGridPosX(), tiles[2][0].getGridPosY() - 1, Direction.SOUTH);
                  map.setRedTrafficLight(tiles[3][0].getGridPosX(), tiles[3][0].getGridPosY() - 1, Direction.SOUTH);
	    	});
	    	
	    	//red lights overlap for 1.75seconds to allow vehicles to clear the junction
	    	Thread.sleep(1750);
	    	for(Tile[] ts : tiles)
				for(Tile t : ts)
					t.setOccupied(false);
	    	
	    	//Westeast green
	    	Platform.runLater(() ->{
	    		Map map = SimulationController.getInstance().getMap();
	    		
	    		map.setGreenTrafficLight(tiles[0][0].getGridPosX() - 1, tiles[0][0].getGridPosY());
	            map.setGreenTrafficLight(tiles[0][0].getGridPosX() - 1, tiles[0][1].getGridPosY());

	            map.setGreenTrafficLight(tiles[3][2].getGridPosX() + 1, tiles[3][2].getGridPosY());
	            map.setGreenTrafficLight(tiles[3][3].getGridPosX() + 1, tiles[3][3].getGridPosY());
	           
	    	});
	    	
	    	Thread.sleep(weSwitchTime);
	    	
			}catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	
	/*
	 * Getter & Setter
	 */
	
	/**
	 * @see com.simulus.view.TileGroup#getTiles()
	 */
	@Override
	public List<Tile> getTiles() {
		List<Tile> l = new ArrayList<>();
		for (int i = 0; i < tiles.length; i++) {
            Collections.addAll(l, tiles[i]);
		}
		return l;
	}
	
	public ArrayList<CustomPath> getTurningPaths(){
		return turningPaths;
	}
	
	public long getNsSwitchTime(){
		return nsSwitchTime;
	}
	
	public void setNsSwitchTime(long switchTime) {
		this.nsSwitchTime = switchTime;
	}
	
	public long getWeSwitchTime(){
		return weSwitchTime;
	}
	
	public void setWeSwitchTime(long switchTime) {
		this.weSwitchTime = switchTime;
	}
	
	public boolean hasTrafficLights() {
		return this.hasTrafficLights;
	}
	@Override
	public String toString() {
		return "<" +  tiles[0][0].getGridPosX() + ", " + tiles[0][0].getGridPosY() + ">";
		
	}
}
