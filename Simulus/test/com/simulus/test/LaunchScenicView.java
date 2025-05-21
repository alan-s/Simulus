package com.simulus.test;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.scenicview.ScenicView;

import com.simulus.EditorApp;
import com.simulus.MainApp;
import com.simulus.Startup;
import com.simulus.controller.SimulationController;

/**
 * {@code LaunchScenicView} launches the Main with ScenicView.
 * ScenicView is a framework used to query UI elements 
 *
 */
public class LaunchScenicView extends Startup {


	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception { //Override start method

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Simulus");
		this.primaryStage.setResizable(false);
		
		ImageView img = new ImageView(Startup.class.getResource("/resources/simulus.png").toString());
		Button simBtn = new Button("Simulator");
		simBtn.setPrefSize(100.0d, 50.0d);
		simBtn.setOnAction((event) -> {
			MainApp app = new MainApp();
			app.start(primaryStage);
		});
		
		Button editorBtn = new Button("Editor");
		editorBtn.setPrefSize(100.0d, 50.0d);
		editorBtn.setOnAction((event) -> {
			EditorApp app = new EditorApp();
			app.start(primaryStage);
			ScenicView.show(EditorApp.getInstance().getRootLayout());
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
