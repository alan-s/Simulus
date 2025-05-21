package com.simulus;


import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.simulus.controller.SimulationController;
import com.simulus.util.ResourceBuilder;

/**
 * The {@code Startup} class functions as a splash screen, providing users with the choice
 * between the Simulator ({@link MainApp}) and the Editor ({@link EditorApp}).
 * 
 * This is the application class that should be set as run-target, as it is the only one that
 * ensures smooth startup of both apps. 
 */
public class Startup extends Application {
	
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Simulus");
		this.primaryStage.setResizable(false);
		
		ImageView img = new ImageView(ResourceBuilder.getLogoSmall());
		Button simBtn = new Button("Simulator");
		simBtn.setPrefSize(100.0d, 50.0d);
		simBtn.setOnAction((event) -> {
			//Center on screen
			primaryStage.setX((Screen.getPrimary().getBounds().getWidth() - 1000)/2);
			primaryStage.setY((Screen.getPrimary().getBounds().getHeight() - 800)/2);
			
			MainApp app = new MainApp();
			app.start(primaryStage);
			File map = new File(System.getProperty("user.home") + "/Simulus/maps/default.map");
	        if(map.canRead())
	        	SimulationController.getInstance().getMap().loadMap(map);
		});
		
		Button editorBtn = new Button("Editor");
		editorBtn.setPrefSize(100.0d, 50.0d);
		editorBtn.setOnAction((event) -> {
			//Center on screen
			primaryStage.setX((Screen.getPrimary().getBounds().getWidth() - 1000)/2);
			primaryStage.setY((Screen.getPrimary().getBounds().getHeight() - 800)/2);
			
			EditorApp app = new EditorApp();
			app.start(primaryStage);
		});
		
		HBox btnBox = new HBox(simBtn, editorBtn);
		btnBox.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		btnBox.setAlignment(Pos.CENTER);
		btnBox.setSpacing(75.0d);
		
		VBox vbox = new VBox(img, btnBox);
		vbox.setSpacing(10.0d);
		vbox.setPadding(new Insets(0.0d, 0.0d, 10.0d, 0.0d));
		
		primaryStage.setScene(new Scene(vbox));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
