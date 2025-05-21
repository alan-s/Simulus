package com.simulus.view.intersection;

import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import com.simulus.util.enums.Direction;
import com.simulus.util.enums.TurningDirection;
import com.simulus.view.Tile;

/**
 * An extension to the JavaFX {@link javafx.scene.shape.Path} class. Allows storing
 * additional information like the tiles a path starts/ends on, its length in pixel,
 * the direction the path ends in and whether the path is active, i.e. whether vehicles
 * are actually allowed to follow the path.
 */
public class CustomPath extends Path {

	private Tile startTile;
	private Tile endTile;
	private Direction endDir;
	private double pathDistance;
	private TurningDirection turn;
	private boolean isActive = false;
	private boolean unavailable = false;

	/**
	 * @param turn The direction this path turns in
	 * @param pathDistance The length of the path in px
	 * @param st The tile IN FRONT OF  the the intersection and the start of the path
	 * @param et The tile BEHIND the intersection and the end of the path
	 * @param ed The direction the car moves into after following  the path
	 * @param elements {@link javafx.scene.shape.Path#Path(PathElement...)}
	 * @see javafx.scene.shape.Path
	 */
	public CustomPath(TurningDirection turn, double pathDistance, Tile st, Tile et,
			Direction ed, PathElement... elements) {
		super(elements);
		startTile = st;
		endTile = et;
		endDir = ed;
		this.pathDistance = pathDistance;
		this.turn = turn;
	}

	public CustomPath(Tile et,PathElement... elements) {
		super(elements);
		endTile = et;
	}

	public void setStartTile(Tile t) {
		startTile = t;
	}

	public void setEndTile(Tile t) {
		endTile = t;
	}

	public Tile getStartTile() {
		return startTile;
	}

	public Tile getEndTile() {
		return endTile;
	}

	public void setEndDir(Direction d) {
		endDir = d;
	}

	public Direction getEndDirection() {
		return endDir;
	}

	public double getDistance() {
		return pathDistance;
	}

	public TurningDirection getTurn() {
		return turn;
	}

	public boolean getActive() {
		return isActive;
	}

	public void setActive(boolean b) {
		//Dont allow setting unavailable paths being set to active
		if(unavailable && b)
			return;
		
		isActive = b;
	}
	
	public void setUnavailable(boolean b) {
		unavailable = b;
	}
	
	public boolean isUnavailable() {
		return unavailable;
	}

	@Override
	public String toString() {
		return "CustomPath [startTile=" + startTile + ", endTile=" + endTile
				+ ", endDir=" + endDir + ", pathDistance=" + pathDistance
				+ ", turn=" + turn + ", isActive=" + isActive + "]";
	}
	
}
