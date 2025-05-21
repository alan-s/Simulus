package com.simulus.view.vehicles;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import com.simulus.MainApp;
import com.simulus.controller.SimulationController;
import com.simulus.util.enums.Direction;

/**
 * A composition of an {@link com.simulus.view.vehicles.AreaOfEffect}
 * and an {@link com.simulus.view.vehicles.EmergencyCar}. 
 */
public class Ambulance extends Group {
	
	private boolean isColoured = false;
	private AreaOfEffect AoE;
	private EmergencyCar frame;
	
	public static double AoERadius = 100;

	/**
	 * @param posX {@link Car#Car(double, double, Direction)}
	 * @param posY {@link Car#Car(double, double, Direction)}
	 * @param dir {@link Car#Car(double, double, Direction)}
	 * @see com.simulus.view.vehicles.Car#Car(double, double, Direction)
	 */
	public Ambulance(double posX, double posY, Direction dir) {
		frame = new EmergencyCar(posX, posY, dir);
		AoE = new AreaOfEffect(frame.getX() + frame.getWidth()/2, frame.getY() + frame.getHeight()/2, AoERadius, this);
		AoE.setFill(Color.ORANGERED);
		AoE.setOpacity(SimulationController.getInstance().isDebug() ? 0.25 : 0.0d); //show aoe only if in debug-mode
		this.getChildren().add(frame);
		getChildren().add(AoE);
		MainApp.getInstance().getCanvas().getChildren().add(this);
		
		Thread t = new Thread(){
			public void run(){
				while(!isInterrupted())
					try{
						sleep(600);
						Platform.runLater(() -> changeColour());
						sleep(600);
						Platform.runLater(() -> changeColour());
					}catch(Exception e){
						e.printStackTrace();
					}
			}
		};
		t.start();
		
	}
	
	/**
	 * Runs a transition to make the colour of the ambulance pulsate blue and yellow.
	 */
	public void changeColour(){
		FillTransition fill = new FillTransition(Duration.millis(300));
		ParallelTransition transition;
		if(!isColoured){
			fill.setToValue(Color.BLUE);
			transition = new ParallelTransition(frame, fill);
			transition.play();
			isColoured = true;
		}else{
			fill.setToValue(Color.YELLOW);
			transition = new ParallelTransition(frame, fill);
			transition.play();
			isColoured = false;
		}
	}

	/**
	 * Removes the ambulance from the simulation
	 */
	public void remove(){
		MainApp.getInstance().getCanvas().getChildren().remove(this);
	}
	
	/*
	 * Getters & Setters
	 */
	
	public void updateAoE(){
		AoE.updatePosition();
	}
	
	public EmergencyCar getCar(){
		return frame;
	}
	
	public Circle getAoE(){
		return AoE;
	}

}
