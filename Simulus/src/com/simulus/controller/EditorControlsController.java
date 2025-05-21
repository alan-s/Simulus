package com.simulus.controller;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;

import com.simulus.EditorApp;
import com.simulus.MainApp;
import com.simulus.util.Configuration;
import com.simulus.util.ResourceBuilder;

/**
 * The JavaFX controller for the control panel of the map editor.
 */
public class EditorControlsController implements Initializable {

	@FXML
	Button grassButton;
	@FXML
	Button dirtButton;
	@FXML
	Button waterButton;
	@FXML
	Button blockButton;
	@FXML
	Button eraserButton;
	@FXML
	Button roadHorizontalButton;
	@FXML
	Button roadVerticalButton;
	@FXML
	Button interButton;
	@FXML
	Button openMapButton;
	@FXML
	Button saveMapButton;
	@FXML
	Button clearMapButton;
	@FXML
	Button validateMapButton;
	@FXML
	Button simulateButton;
	@FXML
	Button gridIncButton;
	@FXML
	Button gridDecButton;
	@FXML
	Label gridSizeLabel;
	@FXML
	TextField nameTextField;
	@FXML
	TextField dateTextField;
	@FXML
	TextField descTextField;
	@FXML
	TextField authorTextField;
	@FXML 
	Hyperlink simulusLink;
	@FXML
	Button helpButton;
	@FXML
	ImageView logoImg;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		logoImg.setImage(ResourceBuilder.getLogoSmall());
		
		grassButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});
		
		dirtButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});
		
		waterButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});
		
		blockButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});
		
		eraserButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});
	
		roadHorizontalButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});

		roadVerticalButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});

		interButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});

		openMapButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});
		
		validateMapButton.setOnAction((event) -> {
			EditorApp.getInstance().selectButton((Button) event.getSource());
		});

		saveMapButton.setOnAction((event) -> {
			if ("".equals(getMapName())){
				mapNameDialog();
			}else {
				EditorApp.getInstance().selectButton((Button) event.getSource());
			}	
		});
		
		clearMapButton.setOnAction((event) -> {
        	Alert alert = new Alert(AlertType.CONFIRMATION);
        	alert.initOwner(EditorApp.getInstance().getPrimaryStage());
        	alert.setTitle("New Map");
        	alert.setHeaderText("The current map will be lost!");
        	alert.setContentText("Continue?");
        	Optional<ButtonType> result = alert.showAndWait();
        	if(result.get() != ButtonType.OK)
        		return;
        	EditorApp.getInstance().selectButton((Button) event.getSource());
		});
		
		simulusLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                openWebpage("http://github.com/leorohr/simulus");
            }
        });
		
		gridIncButton.setOnAction((event) -> {
        	if(gridChangeDialog()){
    			switch(getGridSize()){
    			case 40:
    				setGridSize(60);
    			break;
    			case 60:
    				setGridSize(80);
    			break;
    			case 80:
    				setGridSize(40);
    			break;
    			}
    			EditorApp.getInstance().clearMap();
        	}
		});
		
		gridDecButton.setOnAction((event) -> {
        	if(gridChangeDialog()){
    			switch(getGridSize()){
    			case 40:
    				setGridSize(80);
    			break;
    			case 60:
    				setGridSize(40);
    			break;
    			case 80:
    				setGridSize(60);
    			break;
    			}
    			EditorApp.getInstance().clearMap();
        	}
		});
		
		simulateButton.setOnAction((event) -> {
			//validate map
			Alert alert = new Alert(AlertType.CONFIRMATION);
        	alert.initOwner(EditorApp.getInstance().getPrimaryStage());
        	alert.setTitle("Opening Simulator");
        	alert.setHeaderText("Opening the Simulator will close the Editor.");
        	alert.setContentText("Save current map and continue?");
        	Optional<ButtonType> result = alert.showAndWait();
        	if(result.get() == ButtonType.OK){        		
        		if(EditorApp.getInstance().getMapValidated() && "".equals(getMapName()) == false){
                	//Save map to file
                	File mapFile = EditorApp.getInstance().saveMapDialog();
                	if(mapFile == null)
                		return;
                	
                	//Open map in simulator
                    MainApp app = MainApp.getInstance(); 
            		if(app == null)
            			app = new MainApp();
            		app.start(new Stage());
            		SimulationController.getInstance().getMap().loadMap(mapFile);

                    try {
                    	EditorApp.getInstance().getPrimaryStage().close();
        				EditorApp.getInstance().stop();
        				
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}else{
        			if ("".equals(getMapName())){
        				mapNameDialog();
        			}else{
        				EditorApp.getInstance().saveMapDialog();
        			}
        		}
        	} else{
        		return;
        	}
		});
		
		helpButton.setOnAction((event) -> {
			Popup help = new Popup();
			help.setAutoHide(true);
			
			WebView textView = new WebView();
			textView.setMaxWidth(640);
			textView.setMaxHeight(480);
			WebEngine webEngine = textView.getEngine();
			webEngine.load(getClass().getResource("/resources/infofiles/editorTutorial.htm").toString());
			
			help.setX(EditorApp.getInstance().getPrimaryStage().getX() + (800 - textView.getMaxWidth())/2);
			help.setY(EditorApp.getInstance().getPrimaryStage().getY() + (800 - textView.getMaxHeight())/2);
			help.getContent().add(textView);
			help.show(EditorApp.getInstance().getPrimaryStage()); 
           
		});
	}
	
	public static void openWebpage(String urlString) {
	    try {
	        Desktop.getDesktop().browse(new URL(urlString).toURI());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private boolean gridChangeDialog(){
		Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.initOwner(EditorApp.getInstance().getPrimaryStage());
    	alert.setTitle("Grid Size");
    	alert.setHeaderText("Altering grid size will clear the current map!");
    	alert.setContentText("Continue?");
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK) {
    		return true;
    	} else {
    		return false;
    	}
	}
	
	private void mapNameDialog(){
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(EditorApp.getInstance().getPrimaryStage());
		alert.setTitle("Map Name");
		alert.setHeaderText("A map name is required!");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() != ButtonType.OK)
			return;
	}

	public void setMapName(String name) {
	        nameTextField.setText(name);
	    }
	
	public String getMapName() {
        return nameTextField.getText();
    }
	
	public void setMapDate(String date) {
        dateTextField.setText(date);
    }

	public String getMapDate() {
		return dateTextField.getText();
	}
	
	public void setMapDesc(String description) {
        descTextField.setText(description);
    }

	public String getMapDesc() {
		return descTextField.getText();
	}
	
	public void setMapAuthor(String author) {
        authorTextField.setText(author);
    }

	public String getMapAuthor() {
		return authorTextField.getText();
	}
	
	
	public int getGridSize(){
		int value = 60;
		switch(gridSizeLabel.getText()){
		case "40 x 40":
			value = 40;
			break;
		case "60 x 60":
			value = 60;
			break;
		case "80 x 80":
			value = 80;
			break;
		}
		return value;
	}
	
	public void setGridSize(int size){
		switch(size){
		case 40:
			gridSizeLabel.setText("40 x 40");
			Configuration.setGridSize(40);
		break;
		case 60:
			gridSizeLabel.setText("60 x 60");
			Configuration.setGridSize(60);
		break;
		case 80:
			gridSizeLabel.setText("80 x 80");
			Configuration.setGridSize(80);
		break;
		}
	}

}
