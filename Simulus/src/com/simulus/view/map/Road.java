package com.simulus.view.map;

import com.simulus.util.Configuration;
import com.simulus.util.enums.Direction;
import com.simulus.util.enums.Orientation;
import com.simulus.view.Tile;
import com.simulus.view.TileGroup;

import javafx.scene.Group;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a roadblock. Roadblock refers to four adjacent lanes, hence one roadblock takes up a 1x4 space
 * in the grid of tiles.
 */
public class Road extends Group implements TileGroup{

	private Lane[] lanes = new Lane[4];
	private Orientation orientation;
	
	/**
	 * @param gridPosX The leftmost/top x-coordinate of the road in the grid.
	 * @param gridPosY The leftmost/top y-coordinate of the road in the grid.
	 * @param orientation The orientation of the Road.
	 */
	public Road(int gridPosX, int gridPosY, Orientation orientation){
		
		int tileSize = Configuration.getTileSize();
		this.orientation = orientation;
		
		switch(orientation){
		case NORTHSOUTH:
			for (int i = 0; i < lanes.length; i++) {
				if(i<2) 
					lanes[i] = new Lane((gridPosX + i)*tileSize, gridPosY*tileSize,
										tileSize, tileSize, gridPosX+i, gridPosY, Direction.NORTH, i, false);
				else
					lanes[i] = new Lane((gridPosX + i)*tileSize, gridPosY * tileSize,
										tileSize, tileSize, gridPosX+i, gridPosY, Direction.SOUTH, i, false);
			}
			break;
		case WESTEAST:
			for (int i = 0; i < lanes.length; i++) {
				if(i<2) 
					lanes[i] = new Lane(gridPosX*tileSize, (gridPosY + i)*tileSize,
										tileSize, tileSize, gridPosX, gridPosY+i, Direction.EAST, i, false);
				else
					lanes[i] = new Lane(gridPosX*tileSize, (gridPosY + i)*tileSize,
										tileSize, tileSize, gridPosX, gridPosY+i, Direction.WEST, i, false);
			}
			break;
		default:
			break;
		}
		
		this.getChildren().addAll(lanes);
	}

    /**
     * @return A list containing the 4 tiles that this roadblock is comprised of.
     */
	@Override
	public List<Tile> getTiles() {
		
		return Arrays.asList(lanes);
	}

    /**
     * @return The orientation of this roadblock.
     */
	public Orientation getOrientation() {
		return this.orientation;
	}
}
