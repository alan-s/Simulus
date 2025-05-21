package com.simulus.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import com.simulus.MainApp;
import com.simulus.controller.SimulationController;
import com.simulus.util.Configuration;
import com.simulus.util.enums.Direction;
import com.simulus.view.map.Lane;
import com.simulus.view.vehicles.Vehicle;

/**
 * The base class for all types of tiles. The {@link com.simulus.view.map.Map} is composed of
 * Tile objects.
 */
public class Tile extends Group {

	private int gridPosX;
	private int gridPosY;
	private Vehicle occupier;
	private Line redLight = null;
    protected Rectangle frame;
    protected boolean isOccupied;
    protected boolean isBlock;
    protected boolean isRedLight = false;

    /**
     * @param posX The x-coordinate of the top left corner
     * @param posY The y-coordinate of the top left corner
     * @param width The tile's width in px
     * @param height The tile's height in px
     * @param gridPosX The x position of the tile in the grid
     * @param gridPosY The y position of the tile in the grid
     */
	public Tile(double posX, double posY, double width, double height,
			int gridPosX, int gridPosY) {
		frame = new Rectangle(posX, posY, width, height);
		this.gridPosX = gridPosX;
		this.gridPosY = gridPosY;
		occupier = null;
		isOccupied = false;
		frame.setFill(Color.TRANSPARENT);
		if(MainApp.getInstance() != null){
			frame.setStroke(Color.TRANSPARENT);
		}else{
			frame.setStroke(Color.BLACK);
		}
		frame.setStrokeWidth(0.1);
		this.getChildren().add(frame);
	}

	/**
	 * Sets the tile to occupied or free. Passing in the vehicle makes sure that
	 * only the current occupier can set the tile's occupation status.
	 * 
	 * @param isOccupied Whether the is occupied or not
	 * @param occupier The vehicle that calls this method
	 */
	public void setOccupied(boolean isOccupied, Vehicle occupier) {
		if (!this.isOccupied && isOccupied) {
			this.occupier = occupier;
			this.isOccupied = true;
		} else if (occupier.equals(getOccupier()) && !isOccupied) {
			this.occupier = null;
			this.isOccupied = false;
		}
		redrawTile();
	}

	/**
	 * Sets the tile to occupied or free without checking whether the caller is
	 * the currently occupying car.
	 * 
	 * @param isOccupied Whether the tile is occupied or not.
	 */
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
		redrawTile();
	}

	/**
	 * If the app is currently in debug mode, this method draws the tiles
	 * occupied by vehicles in green.
	 */
	public void redrawTile() {
		if (!SimulationController.getInstance().isDebug())
			return;

		if (isOccupied()) {
			frame.setFill(Color.GREEN);
		} else {
			if (this instanceof Lane)
				((Lane) this).redraw();
			else
				frame.setFill(Color.TRANSPARENT); // keep intersections
													// transparent
		}
	}
	
	/*
	 * Getter & Setter
	 */
	public double getWidth() {
		return frame.getWidth();
	}

	public double getHeight() {
		return frame.getHeight();
	}

	public Rectangle getFrame() {
		return frame;
	}

	public double getCenterX() {
		return getX() + (getWidth() / 2);
	}

	public double getCenterY() {
		return getY() + (getHeight() / 2);
	}

	public Vehicle getOccupier() {
		return occupier;
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	public int getGridPosX() {
		return gridPosX;
	}

	public int getGridPosY() {
		return gridPosY;
	}

	public boolean isBlock(){
		return isBlock;
	}

	public boolean isRedLight() {
		return isRedLight;
	}

	/**
	 * @return The x-coordinate of the top left corner of the tile.
	 */
	public double getX() {
		return frame.getX();
	}

	/**
	 * @return The y-coordinate of the top left corner of the tile.
	 */
	public double getY() {
		return frame.getY();
	}

	/**
	 * Makes this tile a redlight, i.e. draws a red line on one of the sides of the the frame,
	 * according to {@code dir}
	 * @param b Whether the tile should be a redlight or not.
	 * @param dir The traffic direction that should be blocked by the red light 
	 */
	public void setIsRedLight(boolean b, Direction dir) {
		isRedLight = b;
		if(isRedLight) {
			
			int tileSize = Configuration.getTileSize();
			switch(dir) {
			case NORTH:
				redLight = new Line(frame.getX(), frame.getY(), frame.getX()+tileSize, frame.getY());
				break;
			case SOUTH:
				redLight = new Line(frame.getX(), frame.getY()+tileSize, frame.getX()+tileSize, frame.getY()+tileSize);
				break;
			case EAST:
				redLight = new Line(frame.getX()+tileSize, frame.getY(), frame.getX()+tileSize, frame.getY()+tileSize);
				break;
			case WEST:
				redLight = new Line(frame.getX(), frame.getY(), frame.getX(), frame.getY()+tileSize);
				break;
			default:
				break;
			}
			
			redLight.setStroke(Color.RED);
			redLight.getStrokeDashArray().add(5d);
			redLight.setStrokeWidth(2d);
			getChildren().add(redLight);
		}
		else getChildren().remove(redLight);
	}
	
	@Override
	public String toString() {
		return "X: " + gridPosX + " Y: " + gridPosY + " Is Occupied: "
				+ isOccupied + " By: " + occupier;
	}

}
