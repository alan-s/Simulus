package com.simulus.view;

import java.util.List;

/**
 * Interface used in {@link com.simulus.view.map.Road} and
 * {@link com.simulus.view.intersection.Intersection}.
 * Allows grouping of tiles and retrieving all tiles that belong to
 * the group as a list. 
 */
public interface TileGroup {

	public List<Tile> getTiles();
}
