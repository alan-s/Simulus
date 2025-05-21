package com.simulus.controller;

import java.io.File;

import javafx.application.Platform;

import com.simulus.MainApp;
import com.simulus.util.enums.Behaviour;
import com.simulus.view.Tile;
import com.simulus.view.map.Lane;
import com.simulus.view.map.Map;
import com.simulus.view.vehicles.Ambulance;
import com.simulus.view.vehicles.Car;
import com.simulus.view.vehicles.EmergencyCar;
import com.simulus.view.vehicles.Truck;
import com.simulus.view.vehicles.Vehicle;

/**
 * A controller managing the simulation. Changes in simulation parameters,
 * starting and stopping the simulation are done here.
 * The class implements the singleton-pattern and can hence be used as central entry
 * point to reach out to the current instance of the map or the UI controllers. 
 */
public class SimulationController {

    //Simulation Parameters
    private double tickTime = 50; //in ms
    private int spawnDelay = 25; //a new car spawns every spawnRate'th tick
    private int maxCars = 25;
    private int maxCarSpeed = 50;
    private double carTruckRatio = 0.7d;		//the desired carCount/truckCount-fraction 
    private double recklessNormalRatio = 0.3d; 	//see above
    private int recklessCount = 0;
    private int truckCount = 0;
    private int ambulanceCount = 0;
    private boolean debugFlag = false;
    
    private Map map = new Map();
    private File lastLoadedMap;
    private AnimationThread animationThread;

    /* Singleton */
    private static SimulationController instance;
    public static SimulationController getInstance() {
        if (instance == null)
            instance = new SimulationController();

        return instance;
    }

    private SimulationController() {
        animationThread = new AnimationThread();
    }

    /**
     * Starts a new simulation
     */
    public void startSimulation() {
        if(animationThread.isInterrupted() || !animationThread.isAlive())
            animationThread = new AnimationThread();
        
        if(!animationThread.isAlive())
        	animationThread.start();

        MainApp.getInstance().getControlsController().spawnAmbulanceButton.disableProperty().set(false);
    }

    /**
     * Stops the current simulation, reloads the map depending on {@code reloadMap},
     * and resets the canvas, statistcs and settings to the default.
     * @param reloadMap Whether or not the current map should be reloaded from its file.
     */
    public void resetSimulation(boolean reloadMap) {
    	
    	if(reloadMap) {
    		map.stopChildThreads();
	    	map = new Map();
	    	map.loadMap(lastLoadedMap);
    	}
    	
    	animationThread.interrupt();
        MainApp.getInstance().resetCanvas();
        MainApp.getInstance().getControlsController().resetCharts();
        MainApp.getInstance().getControlsController().resetSettings();
        setDebugFlag(false);
        truckCount = 0;
        recklessCount = 0;
        ambulanceCount = 0;
        MainApp.getInstance().getControlsController().spawnAmbulanceButton.disableProperty().set(true);
        animationThread = new AnimationThread();
        map.drawMap(MainApp.getInstance().getCanvas());
        if(debugFlag)
        	map.showAllIntersectionPaths();
    }

    /**
     * The central thread responsible for running the simulation. 
     */
    private class AnimationThread extends Thread {

        @Override
        public void run() {
            long tickCount = 0;
            while(!Thread.currentThread().isInterrupted()) {
        		
            	if(tickCount * tickTime % 500 == 0) //add data every 500 ms
            		Platform.runLater(() -> MainApp.getInstance().getControlsController().updateCharts());

                if(tickCount % spawnDelay == 0) {
                    if (map.getVehicleCount() < maxCars) {
                        //If the car-truck ratio is not correct, spawn a truck, otherwise a car.
                        if (truckCount < (1 - carTruckRatio) * map.getVehicleCount()) {
                            Platform.runLater(() -> map.spawnRandomTruck());
                            truckCount++;
                        } else {
                        	//If the reckless-normal-ratio is not correct, spawn a reckless car.
                        	if(recklessCount < recklessNormalRatio * map.getVehicleCount()) {
                        		//30% of the reckless cars exhibit semi, i.e. sometimes reckless, behavior
                        		Platform.runLater(() -> map.spawnRandomCar(
                        				(Math.random() < 0.3 ? Behaviour.SEMI : Behaviour.RECKLESS)));
                        		recklessCount++;
                        	}
                        	else Platform.runLater(() -> map.spawnRandomCar(Behaviour.CAUTIOUS));
                        }
                    }
                }    
            	/*if(tickCount == 1)
            		 Platform.runLater(() -> map.spawnRandomCar(Behavior.RECKLESS));*/
            	
                Platform.runLater(() -> map.updateMap());
                //Increase tickCount or reset if overflown
                tickCount = (tickCount == Long.MAX_VALUE ? 0 : ++tickCount);
                
                try {
                    Thread.sleep((long) tickTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();   
                }
            }
        }

        public AnimationThread() {
            super("AnimationThread");
        }
    }

    /**
     * Offers a clean way to initialise the controller without calling getInstance()
     */
    public static void init() {
        if(instance == null)
            instance = new SimulationController();
    }

    /**
     * Notifies the map that the passed vehicle should be removed.
     * @param v The vehicle to be removed.
     */
    public void removeVehicle(Vehicle v) {
        map.removeVehicle(v);
        if(v instanceof Truck)
            truckCount--;
        if(v instanceof EmergencyCar) {
        	ambulanceCount--;
        	MainApp.getInstance().getControlsController().setAmbulanceButtonDisabled(false);
        }
        if(v instanceof Car) {
        	if(v.getBehavior() == Behaviour.SEMI || v.getBehavior() == Behaviour.RECKLESS)
        		recklessCount--;
        }
    }

    /**
     * If there a are no more than 4 ambulances on the map, this method reaches out to the map
     * to spawn an additional ambulance.
     */
    public void spawnAmbulance() {
    	if(ambulanceCount < 5) {
    		Platform.runLater(() -> map.spawnAmbulance());
	    	ambulanceCount++;
    	}
    }         
    
    /* * * 
     * Getter & Setter 
     * * */
    public boolean isDebug() {
        return debugFlag;
    }
    
    /**
     * Enables/disbales debug mode. In debug-mode, paths on intersections are visualised,
     * occupied tiles are coloured in green and the {@link com.simulus.view.vehicles.AreaOfEffect} of ambulances
     * are shown as a red circle surrounding them. 
     * @param debugFlag Whether debug mode should be enabled or not
     */
    public void setDebugFlag(boolean debugFlag) {
        this.debugFlag = debugFlag;
        if(debugFlag) {
            map.showAllIntersectionPaths();
            //Show AoE of ambulance
            for(Vehicle v : map.getVehicles()) {
            	if(v instanceof EmergencyCar) {
            		((Ambulance)v.getParent()).getAoE().setOpacity(0.25d);
            	}
            }
            	
        }
        else {
            map.hideAllIntersectionsPaths();
            //Hide AoE of ambulances
            for(Vehicle v : map.getVehicles()) {
            	if(v instanceof EmergencyCar) {
            		((Ambulance)v.getParent()).getAoE().setOpacity(0.0d);
            	}
            }

            //Clear debuginformation from canvas
            for(Tile[] t : map.getTiles()) {
	            for (int i = 0; i < t.length; i++) {
                	if(t[i] instanceof Lane)
                		((Lane) t[i]).redraw();
	            }
            }
        }
    }
    
    public double getTickTime() {
        return tickTime;
    }

    public void setTickTime(double tickTime) {
        this.tickTime = tickTime;
    }

    public int getSpawnRate() {
        return spawnDelay;
    }

    public void setSpawnRate(int spawnRate) {
        this.spawnDelay = spawnRate;
    }

    public int getMaxCars() {
        return maxCars;
    }

    public void setMaxCars(int maxCars) {
        this.maxCars = maxCars;
    }

    public int getMaxCarSpeed() {
        return maxCarSpeed;
    }

    public void setMaxCarSpeed(int maxCarSpeed) {
        this.maxCarSpeed = maxCarSpeed;
    }
    
    public int getAmbulanceCount(){
    	return ambulanceCount;
    }

    public double getCarTruckRatio() {
        return carTruckRatio;
    }

    public void setCarTruckRatio(double carTruckRatio) {
        this.carTruckRatio = carTruckRatio;
    }

    public void setRecklessNormalRatio(double recklessNormalRatio) {
    	this.recklessNormalRatio = recklessNormalRatio;
    }
    
    public Map getMap() {
        return map;
    }
    
    public void setMap(Map map) {
    	this.map = map;
    }
    
    public void setLastLoadedMap(File mapFile) {
    	lastLoadedMap = mapFile;
    }
    
    public File getLastLoadedMap() {
    	return lastLoadedMap;
    }
    
    public void incTruckCount() {
    	truckCount++;
    }
    
    public int getTruckCount(){
    	return truckCount;
    }
    
    public double getRecklessNormalRatio(){
    	return recklessNormalRatio;
    }
    
    public int getRecklessCount(){
    	return recklessCount;
    }
    
    public Thread getAnimationThread(){
    	return animationThread;
    } 
}