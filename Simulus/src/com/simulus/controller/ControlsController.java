package com.simulus.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;

import com.simulus.MainApp;
import com.simulus.io.CsvExport;
import com.simulus.util.Configuration;
import com.simulus.util.ResourceBuilder;
import com.simulus.util.enums.VehicleColorOption;
import com.simulus.view.map.Map;



/**
 * The JavaFX controller for the right side control panel of the simulator.
 */
public class ControlsController implements Initializable {

	@FXML
	Button startButton;
    @FXML
    Button resetButton;
	@FXML
	Slider numCarSlider;
	@FXML
	Slider tickrateSlider;
	@FXML
	Slider maxcarspeedSlider;
	@FXML
	Slider spawnrateSlider;
	@FXML
	Slider cartruckratioSlider;
	@FXML
	Slider recklessnormalSlider;
	@FXML 
	Label numCarLabel;
	@FXML
	Label tickrateLabel;
	@FXML
	Label maxcarspeedLabel;
	@FXML
	Label spawnrateLabel;
	@FXML
	Label cartruckratioLabel;
	@FXML
	Label recklessnormalLabel;
	@FXML
	Button randTrafficLightsButton;
	@FXML
	Button spawnAmbulanceButton;
	@FXML
	ComboBox<VehicleColorOption> carcolorComboBox;
	@FXML
	ColorPicker carcolorPicker;
	@FXML
	ComboBox<VehicleColorOption> truckcolorComboBox;
	@FXML
	ColorPicker truckcolorPicker;
	@FXML
	CheckBox debugCheckbox;
	@FXML
	Button helpButton;

	@FXML
	ImageView resizeIcon;
    @FXML
    TitledPane statisticsPane;
	@FXML
    LineChart<Number, Number> numVehiclesChart;
    @FXML
    LineChart<Number, Number> avgSpeedChart;
    @FXML
    LineChart<Number, Number> congestionChart;
    @FXML
    LineChart<Number, Number> waitingTimeChart;

    private static int MAX_DATA_POINTS = 100;

    private int dataCount = 0;
    private LineChart.Series<Number, Number> numVehiclesSeries;
    private LineChart.Series<Number, Number> avgSpeedSeries;
    private LineChart.Series<Number, Number> congestionSeries;
    private LineChart.Series<Number, Number> waitingTimeSeries;
    private LineChart.Series<Number, Number> emWaitingTimeSeries; //waiting time of emergency vehicles
    private double durationCount = 0;
    private CsvExport csv;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//Allowing to pop-out the statistics panel
		resizeIcon.setImage(ResourceBuilder.getResizeIcon());
		resizeIcon.setOnMouseClicked((event) -> {

			Stage chartStage = new Stage();
			chartStage.setTitle("Statistics");
			numVehiclesChart.getXAxis().setPrefWidth(400);
			avgSpeedChart.getXAxis().setPrefWidth(400);
			congestionChart.getXAxis().setPrefWidth(400);
			waitingTimeChart.getXAxis().setPrefWidth(400);
			VBox chartBox = new VBox(numVehiclesChart, avgSpeedChart, congestionChart, waitingTimeChart);
			
			chartStage.setScene(new Scene(chartBox, 500, 800));
			chartStage.setX(MainApp.getInstance().getPrimaryStage().getX() + 600);
			chartStage.setY(MainApp.getInstance().getPrimaryStage().getY());
			chartStage.setOnCloseRequest((WindowEvent) -> {
				numVehiclesChart.getXAxis().setPrefWidth(200);
				avgSpeedChart.getXAxis().setPrefWidth(200);
				congestionChart.getXAxis().setPrefWidth(200);
				waitingTimeChart.getXAxis().setPrefWidth(200);
				
				statisticsPane.setContent((new VBox(numVehiclesChart, avgSpeedChart, congestionChart, waitingTimeChart)));
				statisticsPane.expandedProperty().set(true);
			});
			chartStage.show();
			
			
		}); 
		
		//Initialise charts
		numVehiclesSeries = new LineChart.Series<Number, Number>();
        numVehiclesChart.getData().addAll(numVehiclesSeries);
        
        avgSpeedSeries = new LineChart.Series<Number, Number>();
        avgSpeedChart.getData().addAll(avgSpeedSeries);
        
        congestionSeries = new LineChart.Series<Number, Number>();
        congestionChart.getData().addAll(congestionSeries);
        
        waitingTimeSeries = new LineChart.Series<Number, Number>();
        waitingTimeSeries.setName("Avg. Waiting Time of Cars/Trucks");
        waitingTimeChart.getData().addAll(waitingTimeSeries);
        
        emWaitingTimeSeries = new LineChart.Series<Number, Number>();
        emWaitingTimeSeries.setName("Avg. Waiting Time of Ambulances");
        waitingTimeChart.getData().addAll(emWaitingTimeSeries);

        //Initialise sliders
        SimulationController simulationController = SimulationController.getInstance();
	
		tickrateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            tickrateLabel.setText(String.valueOf(newValue.intValue()));
            simulationController.setTickTime(newValue.intValue());
        });
		
		numCarSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			numCarLabel.setText(String.valueOf(newValue.intValue()));
			simulationController.setMaxCars(newValue.intValue());
		});
		
		spawnrateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			spawnrateLabel.setText(String.valueOf(newValue.intValue()));
			simulationController.setSpawnRate(newValue.intValue());
		});
		
		maxcarspeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			maxcarspeedLabel.setText(String.valueOf(newValue.intValue()));
			simulationController.setMaxCarSpeed(newValue.intValue());
		});
		
		cartruckratioSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			double roundedValue = (double) ((int)(newValue.doubleValue()*10))/10;
			cartruckratioLabel.setText(String.valueOf(roundedValue));
			simulationController.setCarTruckRatio(roundedValue);
		});
		
		recklessnormalSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			double roundedValue = (double) ((int)(newValue.doubleValue()*10))/10;
			recklessnormalLabel.setText(String.valueOf(roundedValue));
			simulationController.setRecklessNormalRatio(roundedValue);
		});
		
		randTrafficLightsButton.setOnAction((event) -> {
			simulationController.getMap().randomiseTrafficLights();
		});
		
		spawnAmbulanceButton.setOnAction((event) -> {
			simulationController.spawnAmbulance();
		});
		
		//Initialise Colouroptions
		carcolorComboBox.getItems().addAll(VehicleColorOption.values());
		carcolorComboBox.getSelectionModel().select(0);
		carcolorComboBox.setOnAction((event) -> { 
	
			if(carcolorComboBox.getSelectionModel().getSelectedItem() == VehicleColorOption.USER) {
				carcolorPicker.setDisable(false);
			} else carcolorPicker.setDisable(true);
			
			simulationController.getMap().setCarColorOption(carcolorComboBox.getSelectionModel().getSelectedItem());
		});
		
		truckcolorComboBox.getItems().addAll(VehicleColorOption.SPEED, VehicleColorOption.USER);
		truckcolorComboBox.getSelectionModel().select(0);
		truckcolorComboBox.setOnAction((event) -> { 
			
			if(truckcolorComboBox.getSelectionModel().getSelectedItem() == VehicleColorOption.USER) {
				truckcolorPicker.setDisable(false);
			} else truckcolorPicker.setDisable(true);
			
			simulationController.getMap().setTruckColorOption(truckcolorComboBox.getSelectionModel().getSelectedItem());
		});
				
		//Initialise Buttons
		startButton.setOnAction((event) -> {
			
			csv = new CsvExport(Configuration.getTempStatsFile());
			csv.generateCsvFile("sim duration (s)", "no of vehicles", "avg speed (km/h)", "congestion %", "avg waiting time(s) - cars/trucks", "avg waiting time(s) - ambulance");
			
			simulationController.startSimulation();

		});

        resetButton.setOnAction((event) -> {
    		simulationController.resetSimulation(true);
        });
       
		debugCheckbox.setOnAction((event) -> {
			simulationController.setDebugFlag(debugCheckbox.isSelected());
		});
		
		helpButton.setOnAction((event) -> {
			Popup help = new Popup();
			help.setAutoHide(true);

			
			WebView textView = new WebView();
			textView.setMaxWidth(640);
			textView.setMaxHeight(480);
			WebEngine webEngine = textView.getEngine();
			webEngine.load(getClass().getResource("/resources/infofiles/simInfo.htm").toString());

			help.setX(MainApp.getInstance().getPrimaryStage().getX() + (800 - textView.getMaxWidth())/2);
			help.setY(MainApp.getInstance().getPrimaryStage().getY() + (800 - textView.getMaxHeight())/2);
			help.getContent().add(textView);
			help.show(MainApp.getInstance().getPrimaryStage()); 
		});
	}

	/**
	 * Retrieves new datapoints from the map and ensures that only {@code MAX_DATA_POINTS}-many
	 * points are showing in the charts.
	 */
	void updateCharts() {
		
		Map map = SimulationController.getInstance().getMap();
		dataCount++;
		
		//*100/100 is to round to two decimal places
		int vehicleCount  = map.getVehicleCount();
		double avgSpeed = (double) Math.round(map.getAverageSpeed()*100)/100;
		double congestion = (double) Math.round(map.getCongestionValue()*100*100)/100;
		double avgWait = (double) Math.round(map.getAvgWaitingTime()/10*100)/100; //1 tick simulates 0.1secs in real-time
		double avgEmWait = (double) Math.round(map.getAvgEmWaitingTime()/10*100)/100;
    	
    	//Update Vehicle Count Chart
		numVehiclesSeries.getData().add(new LineChart.Data<>(dataCount, vehicleCount));
    	
    	//Update Avg. Speed Chart
        avgSpeedSeries.getData().add(new LineChart.Data<>(dataCount, avgSpeed));
        
        //Update Congestion Chart
        congestionSeries.getData().add(new LineChart.Data<>(dataCount, congestion));
        
        //Update Waiting Time Chart
        waitingTimeSeries.getData().add(new LineChart.Data<>(dataCount, avgWait)); //1 tick simulates 0.1secs in real-time
        emWaitingTimeSeries.getData().add(new LineChart.Data<>(dataCount, avgEmWait));
        
        //Write stats to temp file
        durationCount++;
        csv.appendRow(String.valueOf(500*durationCount/1000), String.valueOf(vehicleCount), String.valueOf(avgSpeed), String.valueOf(congestion),
    			String.valueOf(avgWait), String.valueOf(avgEmWait));
     
        if(dataCount%MAX_DATA_POINTS == 0) {
            waitingTimeSeries.getData().remove(0, 10);
            emWaitingTimeSeries.getData().remove(0, 10);
            congestionSeries.getData().remove(0, 10);
            avgSpeedSeries.getData().remove(0, 10);
            numVehiclesSeries.getData().remove(0, 10);
            
            ((NumberAxis)congestionChart.getXAxis()).setLowerBound((0 > dataCount-MAX_DATA_POINTS ? 0 : dataCount-MAX_DATA_POINTS));
            ((NumberAxis)congestionChart.getXAxis()).setUpperBound(dataCount);
            ((NumberAxis)waitingTimeChart.getXAxis()).setLowerBound((0 > dataCount-MAX_DATA_POINTS ? 0 : dataCount-MAX_DATA_POINTS));
            ((NumberAxis)waitingTimeChart.getXAxis()).setUpperBound(dataCount);   
            ((NumberAxis)avgSpeedChart.getXAxis()).setLowerBound((0 > dataCount-MAX_DATA_POINTS ? 0 : dataCount-MAX_DATA_POINTS));
            ((NumberAxis)avgSpeedChart.getXAxis()).setUpperBound(dataCount);
            ((NumberAxis)numVehiclesChart.getXAxis()).setLowerBound((0 > dataCount-MAX_DATA_POINTS ? 0 : dataCount-MAX_DATA_POINTS));
            ((NumberAxis)numVehiclesChart.getXAxis()).setUpperBound(dataCount);
            
        }   
	}
	
	/**
     * Resets all chart data.
     */
    public void resetCharts() {
    	numVehiclesSeries.getData().remove(0, numVehiclesSeries.getData().size()-1);
    	avgSpeedSeries.getData().remove(0, avgSpeedSeries.getData().size()-1);
    	congestionSeries.getData().remove(0, congestionSeries.getData().size()-1);
    	waitingTimeSeries.getData().remove(0, waitingTimeSeries.getData().size()-1);
    	emWaitingTimeSeries.getData().remove(0, emWaitingTimeSeries.getData().size()-1);
    	
    	//Series to start at [0,0]
    	numVehiclesSeries.getData().add(new Data<>(0d, 0d));
    	avgSpeedSeries.getData().add(new Data<>(0d, 0d));
    	congestionSeries.getData().add(new Data<>(0d, 0d));
    	waitingTimeSeries.getData().add(new Data<>(0d, 0d));
    	emWaitingTimeSeries.getData().add(new Data<>(0d, 0d)); 
    	
    	durationCount = 0;
    }
    
    /**
     * Reset all settings to default
     */
    public void resetSettings() {
    	debugCheckbox.setSelected(false);
    	carcolorComboBox.getSelectionModel().select(0);
    	truckcolorComboBox.getSelectionModel().select(0);
    	numCarSlider.setValue(25);
    	tickrateSlider.setValue(50);
    	spawnrateSlider.setValue(25);
    	maxcarspeedSlider.setValue(50);
    	cartruckratioSlider.setValue(0.7d);
    	recklessnormalSlider.setValue(0.3d);
    }
    
	
	/*
	 * Getter & Setter
	 */
	
    public Color getCarColor() {
    	return carcolorPicker.getValue();
    }
    
    public Color getTruckColor() {
    	return truckcolorPicker.getValue();
    }
    
    public void setAmbulanceButtonDisabled(boolean b) {
    	spawnAmbulanceButton.disableProperty().setValue(b);
    }
    
    public void setStartButtonDisabled(boolean b) {
    	startButton.disableProperty().setValue(b);
    }

    public void setResetButtonDisabled(boolean b) {
    	resetButton.disableProperty().setValue(b);
    }
    
    public void setDebugBoxDisabled(boolean b) {
    	debugCheckbox.disableProperty().setValue(b);
    }
}
