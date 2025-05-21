package com.simulus.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * The JavaFX controller for the right side control panel of the simulator.
 */
public class IntersectionDialogController implements Initializable{

	@FXML 
	Slider nsPhaseSlider;
	@FXML 
	Slider wePhaseSlider;
	@FXML
	Label nsPhaseLabel;
	@FXML
	Label wePhaseLabel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		nsPhaseSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			double roundedValue = (double) ((int)(newValue.doubleValue()*10))/10;
			nsPhaseLabel.setText(String.valueOf(roundedValue));
		});
		
		wePhaseSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			double roundedValue = (double) ((int)(newValue.doubleValue()*10))/10;
			wePhaseLabel.setText(String.valueOf(roundedValue));
		});
	}
	
	public long getNsPhase() {
		return (long) (nsPhaseSlider.getValue() * 1000);
	}
	
	public long getWePhase() {
		return (long) (wePhaseSlider.getValue() * 1000);
	}
	
	public void setNsPhase(long nsPhase) {
		nsPhaseSlider.setValue(((double)nsPhase)/1000);
	}
	
	public void setWePhase(long wePhase) {
		wePhaseSlider.setValue(((double)wePhase)/1000);
	}

}
