package com.simulus.controller;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.simulus.EditorApp;
import com.simulus.MainApp;
import com.simulus.io.FileIO;
import com.simulus.io.SimConfigXML;
import com.simulus.util.Configuration;
import com.simulus.util.ResourceBuilder;
import com.simulus.util.enums.VehicleColorOption;
import com.sun.javafx.stage.StageHelper;

/**
 * The JavaFX controller for the root layout of the simulator. 
 * Manages the menubar with its items and contains the borderpane 
 * for the general layout of the application.
 */
public class RootLayoutController implements Initializable {

    @FXML
    MenuBar menuBar;
    @FXML
    MenuItem newMapMItem;
    @FXML
    MenuItem openMapMItem;
    @FXML
    MenuItem editMapMItem;
    @FXML
    MenuItem openSimMItem;
    @FXML
    MenuItem saveSimMItem;
    @FXML 
    MenuItem exportStatsMItem;
    @FXML
    MenuItem closeMItem;
    @FXML
    MenuItem aboutMItem;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

        menuBar.setUseSystemMenuBar(true);

        closeMItem.setOnAction((event) -> Platform.exit());

        newMapMItem.setOnAction((event) ->{
        	Alert alert = new Alert(AlertType.CONFIRMATION);
        	alert.initOwner(MainApp.getInstance().getPrimaryStage());
        	alert.setTitle("Opening Editor");
        	alert.setHeaderText("Opening the Editor will close the current simulation.");
        	alert.setContentText("Continue?");
        	Optional<ButtonType> result = alert.showAndWait();
        	if(result.get() != ButtonType.OK)
        		return;

            try {
            	SimulationController.getInstance().getAnimationThread().interrupt();
				MainApp.getInstance().stop();
				
				for(Stage s : StageHelper.getStages())
					Platform.runLater(() -> s.close());
			} catch (Exception e) {
				e.printStackTrace();
			}
            
        	EditorApp editor = EditorApp.getInstance(); 
        	if(editor == null)
        		editor = new EditorApp();
            editor.start(new Stage());
        });

        /*
         * Opens the current map in the map editor. Shuts down the simulator.
         */
        editMapMItem.setOnAction((event) -> {
        	
        	Alert alert = new Alert(AlertType.CONFIRMATION);
        	alert.initOwner(MainApp.getInstance().getPrimaryStage());
        	alert.setTitle("Opening Editor");
        	alert.setHeaderText("Opening the Editor will close the current simulation.");
        	alert.setContentText("Continue?");
        	Optional<ButtonType> result = alert.showAndWait();
        	if(result.get() != ButtonType.OK)
        		return;

        	try {
            	SimulationController.getInstance().getAnimationThread().interrupt();
				MainApp.getInstance().stop();
				
				for(Stage s : StageHelper.getStages())
					Platform.runLater(() -> s.close());
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	EditorApp editor = EditorApp.getInstance(); 
        	if(editor == null)
        		editor = new EditorApp();
            editor.start(new Stage());
            editor.loadMap(SimulationController.getInstance().getLastLoadedMap());
        });

        openMapMItem.setOnAction((event) -> {
        	SimulationController.getInstance().getAnimationThread().interrupt();
        	FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Map...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Simulus Map Files", "*.map"));
            File dir = new File(System.getProperty("user.home") + "/Simulus/maps");
            if(dir.canRead())
            	fileChooser.setInitialDirectory(dir);
            else fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFile = fileChooser.showOpenDialog(MainApp.getInstance().getPrimaryStage());
            if (selectedFile != null){
                SimulationController.getInstance().getMap().loadMap(selectedFile); 
            }
           
        });
        
        /*
         * Exports the current simulation's statistics to a user-defined csv file. 
         */
        exportStatsMItem.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Map XML...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV File (*.csv)", "*.csv"));
            File dir = new File(System.getProperty("user.home") + "/Simulus/stats");
            if(dir.canRead())
            	fileChooser.setInitialDirectory(dir);
            else fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setInitialFileName("simStatExport.csv");
            File selectedFile = fileChooser.showSaveDialog(MainApp.getInstance().getPrimaryStage());
            File sourceFile = Configuration.getTempStatsFile();
    			if (sourceFile != null && selectedFile !=null){
        			try {
        				FileIO fio = new FileIO();
    					fio.copyFile(sourceFile, selectedFile);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
        });
        
        /*
         * Reads and applies a simulation configuration file.
         */
    	openSimMItem.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Simulation XML...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Simulus Sim Files", "*.sim"));
            File dir = new File(System.getProperty("user.home") + "/Simulus/sim");
            if(dir.canRead())
            	fileChooser.setInitialDirectory(dir);
            else fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setInitialFileName("CustomSim.sim");
            File selectedFile = fileChooser.showOpenDialog(MainApp.getInstance().getPrimaryStage());
            
            if(selectedFile != null){
                SimConfigXML sxml = new SimConfigXML();
                sxml.readXML(selectedFile.getPath());
                
        		MainApp.getInstance().getControlsController().numCarSlider.setValue(sxml.getNoCars());
        		MainApp.getInstance().getControlsController().tickrateSlider.setValue(sxml.getTickRate()); 
        		MainApp.getInstance().getControlsController().spawnrateSlider.setValue(sxml.getSpawnRate()); 
        		MainApp.getInstance().getControlsController().maxcarspeedSlider.setValue(sxml.getMaxSpeedCars()); 
        		MainApp.getInstance().getControlsController().cartruckratioSlider.setValue(sxml.getCarTruckRatio()); 
        		MainApp.getInstance().getControlsController().debugCheckbox.setSelected(sxml.isDebugMode()); 
        		MainApp.getInstance().getControlsController().recklessnormalSlider.setValue(sxml.getRecklessNormRatio()); 
        		
    			if(sxml.getCarColourOption().equals("User")) {
    				MainApp.getInstance().getControlsController().carcolorComboBox.getSelectionModel().select(VehicleColorOption.USER);
    				MainApp.getInstance().getControlsController().carcolorPicker.setValue(Color.web(sxml.getCarColour())); 
    				MainApp.getInstance().getControlsController().carcolorPicker.setDisable(false);
    			}else if (sxml.getCarColourOption().equals("Speed")){
    				MainApp.getInstance().getControlsController().carcolorComboBox.getSelectionModel().select(VehicleColorOption.SPEED);
    				MainApp.getInstance().getControlsController().carcolorPicker.setDisable(true);
    			}else if (sxml.getCarColourOption().equals("Behavior")){
    				MainApp.getInstance().getControlsController().carcolorComboBox.getSelectionModel().select(VehicleColorOption.BEHAVIOUR);
    				MainApp.getInstance().getControlsController().carcolorPicker.setDisable(true);
    			}
    			
    			if(sxml.getTruckColourOption().equals("User")) {
    				MainApp.getInstance().getControlsController().truckcolorComboBox.getSelectionModel().select(VehicleColorOption.USER);
    				MainApp.getInstance().getControlsController().truckcolorPicker.setValue(Color.web(sxml.getTruckColourOption())); 
    				MainApp.getInstance().getControlsController().truckcolorPicker.setDisable(false);
    			}else if (sxml.getTruckColourOption().equals("Speed")){
    				MainApp.getInstance().getControlsController().truckcolorComboBox.getSelectionModel().select(VehicleColorOption.SPEED);
    				MainApp.getInstance().getControlsController().truckcolorPicker.setDisable(true);
    			}else if (sxml.getTruckColourOption().equals("Behavior")){
    				MainApp.getInstance().getControlsController().truckcolorComboBox.getSelectionModel().select(VehicleColorOption.BEHAVIOUR);
    				MainApp.getInstance().getControlsController().truckcolorPicker.setDisable(true);
    			}
    			updateAppTitle(selectedFile.getName());
            }
            
        });
    	
    	/*
    	 * Stores the current simulation configuration in a user-defined file.
    	 */
        saveSimMItem.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Simulation XML...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Simulus Sim Files", "*.sim"));
            fileChooser.setInitialFileName("CustomSim.sim");
            File dir = new File(System.getProperty("user.home") + "/Simulus/sim");
            if(dir.canRead())
            	fileChooser.setInitialDirectory(dir);
            else fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFile = fileChooser.showSaveDialog(MainApp.getInstance().getPrimaryStage());
            
            if (selectedFile != null){
            	SimConfigXML sxml = new SimConfigXML();
        		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        		Date date = new Date();
        		
                sxml.writeXML(selectedFile.getPath(), "Simulation File", dateFormat.format(date), "A simulation parameter file", "Team Simulus", 
                		MainApp.getInstance().getControlsController().numCarSlider.getValue(), 
                		MainApp.getInstance().getControlsController().tickrateSlider.getValue(), 
                		MainApp.getInstance().getControlsController().spawnrateSlider.getValue(), 
                		MainApp.getInstance().getControlsController().maxcarspeedSlider.getValue(), 
                		MainApp.getInstance().getControlsController().cartruckratioSlider.getValue(), 
                		MainApp.getInstance().getControlsController().debugCheckbox.isSelected(), 
                		MainApp.getInstance().getControlsController().recklessnormalSlider.getValue(), 
                		MainApp.getInstance().getControlsController().carcolorComboBox.getSelectionModel().getSelectedItem().toString(), 
                		MainApp.getInstance().getControlsController().carcolorPicker.getValue().toString(), 
                		MainApp.getInstance().getControlsController().truckcolorComboBox.getSelectionModel().getSelectedItem().toString(), 
                		MainApp.getInstance().getControlsController().truckcolorPicker.getValue().toString());
                
                updateAppTitle(selectedFile.getName());
            }
            
        });

        aboutMItem.setOnAction((event) -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);
            ImageView img = new ImageView(ResourceBuilder.getLogoSmall());
            img.setPreserveRatio(true);
            img.setFitHeight(75.0d);
            Label infoPanel = new Label("Simulus Traffic Simulation Ver. 1.0");
            Label tileInfo = new Label("Textures thanks to Kenney Vleugels (www.kenney.nl) - CC0");
            Label iconInfo = new Label("Editor icons thanks to FatCow (www.fatcow.com/free-icons)");
            Hyperlink link = new Hyperlink("http://github.com/leorohr/simulus");
            Button okayBtn = new Button("Okay");
            okayBtn.setOnAction((btnEvent) -> dialog.close());

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(5.0d);
            vbox.setPadding(new Insets(0.0d, 0.0d, 5.0d, 0.0d));
            vbox.getChildren().addAll(img, infoPanel, link, tileInfo, iconInfo, okayBtn);

            dialog.setScene(new Scene(vbox));
            dialog.show();
        });
	}

	/**
	 * Updates the application title bar to display the loaded map name and/or parameter file
	 * @param fileName the name of the parameter file loaded
	 */
	private void updateAppTitle(String fileName){
		String title = MainApp.getInstance().getPrimaryStage().getTitle();
		int index = MainApp.getInstance().getPrimaryStage().getTitle().indexOf("  -  Parameters: ");

		if(index == -1){	
		}else{
			title = title.substring(0, index);
		}
		MainApp.getInstance().getPrimaryStage().setTitle(
				title + "  -  Parameters: " +
						fileName.replace(".sim", ""));	
	}

}
