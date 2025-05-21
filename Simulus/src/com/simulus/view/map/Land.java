package com.simulus.view.map;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import com.simulus.util.ResourceBuilder;
import com.simulus.util.enums.LandType;
import com.simulus.view.Tile;

/**
 * An extension to {@link com.simulus.view.Tile}.
 * Lands tiles cannot be driven on by vehicles.
 * There are several types of Land tile that are distinguished by 
 * an applied texture to give maps a better visual feel.
 */
public class Land extends Tile {

	// Default LandType is EMPTY
	private LandType landType = LandType.EMPTY;

	/**
	 * Creates a Land Tile object that contains its size (height, width), 
	 * Map coordinates (posX, posY) and array position (gridPosX, gridPosY), 
	 * along with a LandType to determine the image that will be drawn on the map.
	 * 
	 * @param posX X coordinate position from top-left corner of Map
	 * @param posY Y coordinate position from top-left corner of Map
	 * @param width Width of the Land tile
	 * @param height Height of the Land tile
	 * @param gridPosX X Position in the Map array
	 * @param gridPosY Y Position in the Map array
	 * @param landType Type of Land tile to use
	 */
	public Land(double posX, double posY, double width, double height,
			int gridPosX, int gridPosY, LandType landType) {
		super(posX, posY, width, height, gridPosX, gridPosY);
		this.landType = landType;
		
		if (landType == LandType.EMPTY) {
			getFrame().setFill(Color.TRANSPARENT);
		} else {
			getFrame().setFill(new ImagePattern(getLandImage(this.landType)));
		}
		
	}
	
	/**
	 * Returns the LandType of the Land tile
	 * 
	 * @return the landType set for the Land tile
	 */
	public LandType getLandType() {
		return this.landType;
	}
	
	/**
	 * Returns the appropriate image file for the given LandType
	 * 
	 * @param landtype the LandType of the tile
	 * @return image - the image representation of the LandType
	 */
	private Image getLandImage(LandType landtype) {
		Image image;
		switch(this.landType) {
		case GRASS :
			image = ResourceBuilder.getGrassTexture();
			break;
		case DIRT :
			image = ResourceBuilder.getDirtTexture();
			break;
		case WATER :
			image = ResourceBuilder.getWaterTexture();
		break;
		case BLOCK :
			image = ResourceBuilder.getBlockTexture();
		break;
		default :
			image = null;
		}
		
		return image;
	}
	

}
