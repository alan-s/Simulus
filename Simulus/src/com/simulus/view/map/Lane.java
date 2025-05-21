package com.simulus.view.map;

import com.simulus.util.ResourceBuilder;
import com.simulus.util.enums.Direction;
import com.simulus.view.Tile;

import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

/**
 * An extension to {@link com.simulus.view.Tile}.
 * Lane tiles are the only tiles vehicles can move on. 
 * It provides a direction to move in and the lane number, which is used to 
 * determine the vehicles current position within the road.
 */
public class Lane extends Tile{

    public static final Color COLOR = Color.GREY;

	private Direction dir;
	private int laneNo;

	/**
	 * @param dir The lane's direction
	 * @param laneNo The lane's number within the road. Numbering starts at 0 with leftmost
	 * 			lane in North/South roads or the top lane in East/West roads
	 * @param isBlock Whether this lane tile is blocked
	 * @param posX {@link Tile#Tile(double, double, double, double, int, int)}
	 * @param posY {@link Tile#Tile(double, double, double, double, int, int)}
	 * @param width {@link Tile#Tile(double, double, double, double, int, int)}
	 * @param height {@link Tile#Tile(double, double, double, double, int, int)}
	 * @param gridPosX {@link Tile#Tile(double, double, double, double, int, int)}
	 * @param gridPosY {@link Tile#Tile(double, double, double, double, int, int)}
	 * @see com.simulus.view.Tile#Tile(double, double, double, double, int, int)
	 */
	public Lane(double posX, double posY, double width, double height,
			int gridPosX, int gridPosY, Direction dir, int laneNo, boolean isBlock) {
		super(posX, posY, width, height, gridPosX, gridPosY);
		this.dir = dir;
		this.laneNo = laneNo;
		this.isBlock = isBlock;
        frame.setFill(
                new ImagePattern((dir == Direction.EAST || dir == Direction.WEST ?
                        ResourceBuilder.getEWLaneTexture() : ResourceBuilder.getNSLaneTexture())));
        if (isBlock == true){
        	isOccupied = true;
        	frame.setFill(new ImagePattern(ResourceBuilder.getBlockTexture()));
        }
	}
	
	/**
	 * Sets the Lane's texture to the correct image. Used in debug-mode to redraw tiles.
	 */
	public void redraw() {
		frame.setFill(
                new ImagePattern((dir == Direction.EAST || dir == Direction.WEST ?
                        ResourceBuilder.getEWLaneTexture() : ResourceBuilder.getNSLaneTexture())));
        if (isBlock == true){
        	frame.setFill(new ImagePattern(ResourceBuilder.getBlockTexture()));
        }
	}
	
	/*
	 * Getters & Setters
	 */

	/**
	 * @return The direction the lane is bound towards.
	 */
    public Direction getDirection() {
		return this.dir;
	}
	
    /**
     * A lane number is used to identify lanes available for overtaking cars.
     * @return The number of the lane.
     */
	public int getLaneNo(){
		return laneNo;
	}
}
