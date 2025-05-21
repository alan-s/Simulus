package com.simulus.view.vehicles;

import javafx.application.Platform;
import javafx.scene.shape.Circle;

import com.simulus.controller.SimulationController;
import com.simulus.view.map.Lane;

/**
 * An extension the {@link javafx.scene.shape.Circle}
 * Depicts the area in which an ambulance influences the behaviour of other vehicles.
 * Vehicles that interesect this area are either slowed down or forced to move into 
 * the adjacent lane (if they are in the same lane as the ambulance).
 */
public class AreaOfEffect extends Circle{
	
	/**
	 * @param centerX {@link Circle#Circle(double, double, double)}
	 * @param centerY {@link Circle#Circle(double, double, double)}
	 * @param radius {@link Circle#Circle(double, double, double)}
	 * @param a The {@link com.simulus.view.vehicles.Ambulance} this AoE belongs to.
	 * @see javafx.scene.shape.Circle#Circle(double, double, double)
	 */
	public AreaOfEffect(double centerX, double centerY, double radius, Ambulance a){
		super(centerX, centerY, radius);
	}
	
	/**
	 * Sets the AoE center to the center of its associated ambulance and forces intersecting
	 * vehicles to either slow down or move out of the ambulance's lane.
	 */
	public void updatePosition(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setCenterX(getCar().getBoundsInParent().getMaxX() - getCar().getWidth()/2);
				setCenterY(getCar().getBoundsInParent().getMaxY() - getCar().getHeight()/2);
			}
		});	
		
		for(Vehicle v: SimulationController.getInstance().getMap().getVehicles()){
			
			if(this.getBoundsInParent().intersects(v.getBoundsInParent()) && !(v instanceof EmergencyCar)){
				if(v.getDirection() == getCar().getDirection() && (v.getCurrentTile()  instanceof Lane) && getCar().getCurrentTile() instanceof Lane) {						
					
					Lane l = (Lane) v.getCurrentTile();
					if(l.getLaneNo() ==  ((Lane)getCar().getCurrentTile()).getLaneNo()) //if v is in same lane, force it over
						v.changeLane();
					else //if car is in adjacent lane, make it slow down and allow cars to merge
						v.allowMerge();
				}
			}
		}
	}
	
	/**
	 * @return The EmergencyCar object that this AoE is associated to.
	 */
	private EmergencyCar getCar() {
		return ((Ambulance) getParent()).getCar();
	}

}
