package com.simulus.view.vehicles;

import java.util.Random;

import javafx.scene.paint.Color;

import com.simulus.controller.SimulationController;
import com.simulus.util.Configuration;
import com.simulus.util.enums.Behaviour;
import com.simulus.util.enums.Direction;
import com.simulus.view.Tile;
import com.simulus.view.intersection.CustomPath;
import com.simulus.view.intersection.IntersectionTile;
import com.simulus.view.map.Lane;

/**
 * An extension to {@link com.simulus.view.vehicles.Vehicle}.
 * Describes a car object on the GUI.
 */
public class Truck extends Vehicle {

	public static final int TRUCKWIDTH = 8 / (Configuration.getGridSize()/40);
	public static final int TRUCKLENGTH = 14 / (Configuration.getGridSize()/40);
	
	private static final Color COLOUR = Color.LIGHTSALMON;
	
	/**
	 * Sets the appearance, position and direction of the truck.
	 * Cars have an acceleration of 0.5m/s^2 and a random max. speed within the maxspeed range
	 * set by the simulation, at least 10km/h (i.e. 1.111px per tick).
	 * 
	 * @param posX The X position of the truck on the scene
	 * @param posY The Y position of the truck on the scene
	 * @param dir The direction the truck should start moving in
	 */
	public Truck(double posX, double posY, Direction dir) {
		super(posX, posY, TRUCKWIDTH, TRUCKLENGTH, dir);

		behavior = Behaviour.getRandomBehaviour();
		
		switch (dir) {
		case NORTH:
		case SOUTH:
			setWidth(TRUCKWIDTH);
			setHeight(TRUCKLENGTH);
			break;
		case EAST:
		case WEST:
			setWidth(TRUCKLENGTH);
			setHeight(TRUCKWIDTH);
			break;
		default:
			break;
		}
	
		setFill(COLOUR);
		Random rand = new Random();
		double speedInMps = ((double)SimulationController.getInstance().getMaxCarSpeed()*1000)/3600;
		//0.5m/s^2; 1m=tilesize/5; 1 tick = 1/10s; 0-80
		acceleration = (0.5d * Configuration.getTileSize()/5)/10;
		//Any speed within the maxspeed range, at least 10km/h (i.e. 1.111px per tick)
		maxSpeed = rand.nextDouble()*((speedInMps * (Configuration.getTileSize()/5))/10 -1.111d) + 1.111d;

		addToCanvas();
	}

	/**
	 * @see com.simulus.view.vehicles.Vehicle#moveVehicle()
	 */
	@Override
	public void moveVehicle() {
			
		if(isTransitioning()){
			checkTransitionBlockage();
			updateTransitionTiles();
			return;
		}

		try {
            Tile nextTile = null;
			switch (dir) {
			case NORTH:
                if(currentTile.getGridPosY()-1 < 0) {
                    SimulationController.getInstance().removeVehicle(this);
                    return;
                }

                nextTile = map[getCurrentTile().getGridPosX()][getCurrentTile().getGridPosY() - 1];
				break;
			case SOUTH:
                if(currentTile.getGridPosY()+1 >= map.length) {
                    SimulationController.getInstance().removeVehicle(this);
                    return;
                }

                nextTile = map[getCurrentTile().getGridPosX()][getCurrentTile().getGridPosY() + 1];
				break;
			case EAST:
                if(currentTile.getGridPosX()+1 >= map.length) {
                    SimulationController.getInstance().removeVehicle(this);
                    return;
                }

                nextTile = map[getCurrentTile().getGridPosX() + 1][getCurrentTile().getGridPosY()];
                break;
			case WEST:
                if(currentTile.getGridPosX()-1 < 0) {
                    SimulationController.getInstance().removeVehicle(this);
                    return;
                }

                nextTile = map[getCurrentTile().getGridPosX() - 1][getCurrentTile().getGridPosY()];
				break;
			default:
				break;
			}
			 
			if (nextTile.isOccupied()) {
				tempDir = Direction.NONE;
				if(nextTile.isBlock())
					changeLane();
			} else if(nextTile instanceof IntersectionTile) { 
				if(currentTile instanceof Lane && !currentTile.isRedLight()) {
					IntersectionTile t = (IntersectionTile) nextTile;
				 	currentIntersection = t.getIntersection();
				 	CustomPath p = t.getRandomPath();
				 	if(p != null)
				 		followPath(t.getRandomPath());
				 	else tempDir = Direction.NONE;
				 } else tempDir = Direction.NONE;
			} else tempDir = getDirection(); //if next tile is not occupied and not an intersection, carry on.
			
            //Slow the car down if cautious and slow car in front
            if(tempBehavior == Behaviour.CAUTIOUS)
                if(nextTile != null && nextTile.getOccupier()!=null && nextTile.getOccupier().maxSpeed < maxSpeed)
                    maxSpeed = nextTile.getOccupier().maxSpeed;
            
		} catch (ArrayIndexOutOfBoundsException e) {
            SimulationController.getInstance().removeVehicle(this);
		}  
		
		//Moves the car in the direction it should go.
		move(dir);
	}
}
