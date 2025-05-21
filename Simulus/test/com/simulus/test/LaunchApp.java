package com.simulus.test;

import com.simulus.Startup;
import javafx.application.Application;


/**
 * {@code LaunchApp} launches the Startup class in a thread
 */
public class LaunchApp {

	private Thread appThread;

	/**
	 * Launch Startup class in a thread
	 */
	public void launchApp(){

		appThread = new Thread("JavaFX Init Thread") {
			public void run() {

				Application.launch(Startup.class, new String[0]);
			}
		};

		appThread.setDaemon(true);
		appThread.start();
	}


}
