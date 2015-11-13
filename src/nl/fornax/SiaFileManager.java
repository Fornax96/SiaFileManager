/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fornax;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Fornax
 */
public class SiaFileManager extends Application {
	
	public static final String SFM_VERSION = "0.0.1";
	public static final String OPERATING_SYSTEM = System.getProperty("os.name");
	public static final String JAVA_VERSION = System.getProperty("java.version");
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/nl/fornax/ui/main.fxml"));
		root.getStylesheets().add(getClass().getResource("/nl/fornax/res/dark.css").toString());
		
		primaryStage.setTitle("Sia File Manager ~ By Fornax");
		primaryStage.setScene(new Scene(root));
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest((e) ->{
			shutdown();
		});
		
		// Start the file monitor
		new FileMonitor();
		
		// Start the database tracker
		new FileTracker().init();
	}
	
	public static void shutdown() {
		// TODO: Stop threads 'n shit
		System.exit(0);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
