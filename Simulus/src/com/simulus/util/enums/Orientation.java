/*
 * @(#) Orientation.java 
 * 
 * Copyright (c) 2015 Team Simulus at King's College London. All Rights Reserved. 
 * 
 * This software is the confidential and proprietary of Team Simulus 
 * 7CCSMGPR Group Project(14~15 SEM2 1) at Kings' College London. 
 * You shall not disclose such confidential information and shall use it only in accordance 
 * with the agreement and regulation set by Simulus and the Department of Informatics at King's College London . 
 * 
 * The software is set to be reviewed by module coordinator of 7CCSMGPR Group Project(14~15 SEM2 1)
 * 
 */

/* 
 * VERSION HISTORY:
 * 
 * 1.00 03/02/2015 Initial Version  
 */


package com.simulus.util.enums;

/**
 * The orientation of a road-tile.
 */
public enum Orientation {
	
	EMPTY, NORTHSOUTH, WESTEAST, INTERSECTION;
	
	/**
	 * @return Randomly returns one of the two direction included by the orientation or <code>null</code>
	 * if it is called on an intersection.
	 */
	public Direction randomDirection() {
		if(this == NORTHSOUTH) {
			if(Math.random() > 0.5)
				return Direction.NORTH;
			else return Direction.SOUTH;
		} else if(this == WESTEAST) {
			if(Math.random() > 0.5)
				return Direction.EAST;
			else return Direction.WEST;
		}
		
		return null;
	}

}
