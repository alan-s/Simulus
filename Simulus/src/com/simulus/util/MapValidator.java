package com.simulus.util;

import java.util.List;

import com.simulus.util.enums.Direction;
import com.simulus.view.Tile;
import com.simulus.view.intersection.IntersectionTile;
import com.simulus.view.map.Land;
import com.simulus.view.map.Lane;

/**
 * Validation class for validating Editor created Maps
 */
public class MapValidator {

	Tile[][] mapTiles;

	/**
	 * Takes a double array of Tiles and checks each tile for its type, 
	 * calls are then made to the appropriate method to check Lane or Intersection tiles
	 * 
	 * @param passedTiles the double array tile set
	 * @return valid - boolean stating if map is valid or not
	 */
	public boolean validateMap(Tile[][] passedTiles){
		mapTiles = passedTiles;
		Boolean valid = true;
		Boolean emptyMap = true;

		for (int x = 0 ;  x < mapTiles.length; x++) {
			for(int y = 0; y < mapTiles[x].length; y++) {

				Tile t = mapTiles[x][y];

				while (valid) {
					/* Validate Lane tiles */
					if (t instanceof Lane) { // Another lane or an intersection is expected
						valid = validateLane(t);
						emptyMap = false;
						break;

						/* Validate Intersection tiles */
					} else if (t instanceof IntersectionTile) { // Check for a complete intersection with exits
						valid = validateIntersection(t);
						emptyMap = false;
						break;

						/* Validate Land tiles */
					} else if (t instanceof Land) { // Land tiles are automatically valid
						break;

						/* Validate empty tiles */
					} else { // Tile is empty and as such it is valid
						break;
					}
				} // while
			} // Inner For
		} // Outer For
		if (emptyMap) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Takes a Lane tile and checks its surrounding tiles to check that the Lane continues,
	 *  meets an Intersection or reaches the edge of the map.
	 *  
	 * @param t The Lane tile to be checked
	 * @return valid - boolean stating if the tile is valid or not
	 */
	private boolean validateLane(Tile t) {
		boolean valid = true;

		int x = t.getGridPosX();
		int y = t.getGridPosY();

		Direction dir = ((Lane) t).getDirection();
		int laneNo = ((Lane) t).getLaneNo();

		if (dir == Direction.NORTH || dir == Direction.SOUTH) {
			/* Check previous tile */
			if (!(y-1 < 0)) {
				Tile tPrev = mapTiles[x][y-1];
				if (tPrev instanceof Lane) {
					if (dir == ((Lane) tPrev).getDirection() &&
							laneNo == ((Lane) tPrev).getLaneNo()) {
						// The Road continues
					} else {
						valid = false;
					}
				} else if (tPrev instanceof IntersectionTile) {
					// This is OK
				} else {
					valid = false;
				}
			}
			/* Check next tile */
			if (!(y+1 >= mapTiles[0].length)) {
				Tile tNext = mapTiles[x][y+1];
				if (tNext instanceof Lane) {
					if (dir == ((Lane) tNext).getDirection() &&
							laneNo == ((Lane) tNext).getLaneNo()) {
						// The Road continues
					} else {
						valid = false;
					}
				} else if (tNext instanceof IntersectionTile) {
					// This is OK
				} else {
					valid = false;
				}
			}
			// Check for Road connection without Intersection
			if (laneNo == 3) { // Get the lane across
				if (!(x+1 >= mapTiles.length)) {
					Tile tNext = mapTiles[x+1][y];
					if (tNext instanceof Lane) {
						if (((Lane) tNext).getDirection() == Direction.EAST ||
								((Lane) tNext).getDirection() == Direction.WEST) {
							valid = false; // Road cannot start from another Road
						}
					} else if(tNext instanceof IntersectionTile) {
						valid = false; // Intersection cannot be next to a Road
					}
				}
			}

		} else { // Direction is WESTEAST
			/* Check previous tile */
			if (!(x-1 < 0)) {
				Tile tPrev = mapTiles[x-1][y];
				if (tPrev instanceof Lane) {
					if (dir == ((Lane) tPrev).getDirection() &&
							laneNo == ((Lane) tPrev).getLaneNo()) {
						// The Road continues
					} else {
						valid = false;
					}
				} else if (tPrev instanceof IntersectionTile) {
					// This is OK
				} else {
					valid = false;
				}
			}
			/* Check next tile */
			if (!(x+1 >= mapTiles.length)) {
				Tile tNext = mapTiles[x+1][y];
				if (tNext instanceof Lane) {
					if (dir == ((Lane) tNext).getDirection() &&
							laneNo == ((Lane) tNext).getLaneNo()) {
						// The Road continues
					} else {
						valid = false;
					}
				} else if (tNext instanceof IntersectionTile) {
					// This is OK
				} else {
					valid = false;
				}
			}
			// Check for Road connection without Intersection
			if (laneNo == 3) { // Get the lane down
				if (!(y+1 >= mapTiles.length)) {
					Tile tNext = mapTiles[x][y+1];
					if (tNext instanceof Lane) {
						if (((Lane) tNext).getDirection() == Direction.NORTH ||
								((Lane) tNext).getDirection() == Direction.SOUTH) {
							valid = false; // Road cannot start from another Road
						}
					} else if(tNext instanceof IntersectionTile) {
						valid = false; // Intersection cannot be next to a Road
					}
				}
			}
		}
		return valid;
	}

	/**
	 * Takes an Intersection tile and checks its surrounding tiles to check that the intersection is complete
	 * and that it has at least two roads that run to/from it.
	 *  
	 * @param t The Intersection tile to be checked
	 * @return valid - boolean stating if the tile is valid or not
	 */
	private boolean validateIntersection(Tile t) {
		boolean valid = true;
		int exitCount = 0;

		// X Across Y Down
		List<Tile> intersection =((IntersectionTile) t).getIntersection().getTiles();

		/* check for a complete intersection */
		for (Tile it : intersection) {
			if (it instanceof IntersectionTile) {
				// Valid
			} else {
				valid = false;
				break;
			}
		}

		/* Check intersection exits */
		for (int i = 0; i < 4; i++) { // North side of intersection
			int newX = intersection.get(i).getGridPosX();
			int newY = intersection.get(i).getGridPosY();

			if (!(newY-1 < 0)) {
				Tile iExit = mapTiles[newX][(newY-1)];

				if (iExit instanceof Lane) {
					if ((((Lane) iExit).getDirection() == Direction.NORTH &&
							(((Lane) iExit).getLaneNo() == 0 || ((Lane) iExit).getLaneNo() == 1))
							|| (((Lane) iExit).getDirection() == Direction.SOUTH &&
							(((Lane) iExit).getLaneNo() == 2 || ((Lane) iExit).getLaneNo() == 3))) {
						exitCount++;
					}
				}

			}
		}

		for (int i = 12; i < 16; i++) { // South side of intersection
			int newX = intersection.get(i).getGridPosX();
			int newY = intersection.get(i).getGridPosY();

			if (!(newY+1 >= mapTiles[0].length)) {
				Tile iExit = mapTiles[newX][(newY+1)];

				if (iExit instanceof Lane) {
					if ((((Lane) iExit).getDirection() == Direction.NORTH &&
							(((Lane) iExit).getLaneNo() == 0 || ((Lane) iExit).getLaneNo() == 1))
							|| (((Lane) iExit).getDirection() == Direction.SOUTH &&
							(((Lane) iExit).getLaneNo() == 2 || ((Lane) iExit).getLaneNo() == 3))) {
						exitCount++;
					}
				}
			}
		}

		for (int i = 0; i < 13; i += 4) { // West side of intersection
			int newX = intersection.get(i).getGridPosX();
			int newY = intersection.get(i).getGridPosY();

			if (!(newX-1 < 0)) {
				Tile iExit = mapTiles[newX-1][(newY)];

				if (iExit instanceof Lane) {
					if ((((Lane) iExit).getDirection() == Direction.EAST &&
							(((Lane) iExit).getLaneNo() == 0 || ((Lane) iExit).getLaneNo() == 1))
							|| (((Lane) iExit).getDirection() == Direction.WEST &&
							(((Lane) iExit).getLaneNo() == 2 || ((Lane) iExit).getLaneNo() == 3))) {
						exitCount++;
					}
				}
			}
		}

		for (int i = 3; i < 16; i += 4) { // East side of intersection
			int newX = intersection.get(i).getGridPosX();
			int newY = intersection.get(i).getGridPosY();

			if (!(newX+1 >= mapTiles.length)) {
				Tile iExit = mapTiles[newX+1][(newY)];

				if (iExit instanceof Lane) {
					if ((((Lane) iExit).getDirection() == Direction.EAST &&
							(((Lane) iExit).getLaneNo() == 0 || ((Lane) iExit).getLaneNo() == 1))
							|| (((Lane) iExit).getDirection() == Direction.WEST &&
							(((Lane) iExit).getLaneNo() == 2 || ((Lane) iExit).getLaneNo() == 3))) {
						exitCount++;
					}
				}				
			}
		}

		if (exitCount < 2) {
			valid = false;
		}

		return valid;

	}


}
