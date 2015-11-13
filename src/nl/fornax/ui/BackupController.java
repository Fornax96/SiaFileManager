/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fornax.ui;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import net.lingala.zip4j.util.Zip4jConstants;
import nl.fornax.DirectorySize;
import nl.fornax.UploadManager;
import nl.fornax.Zipper;
import nl.fornax.ZipperListener;

/**
 * FXML Controller class
 *
 * @author Fornax
 */
public class BackupController implements Initializable, ZipperListener {

	
	private DirectoryChooser dirChooser;
	@FXML
	private Button btnChooseDir;
	@FXML
	private Button btnChooseHomeDir;
	@FXML
	private Label lblDirectory;
	@FXML
	private Label lblSize;
	@FXML
	private Button btnBackup;
	
	// Progress Bar Elements
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label lblProgress;
	
	private File backupPath;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		progressBar.setProgress(0);
		btnChooseDir.setOnAction(btnChooseDirListener);
		btnChooseHomeDir.setOnAction(btnChooseHomeDirListener);
		btnBackup.setOnAction(btnBackupListener);
		
		dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Pick a directory to back up to Sia");
		
		btnBackup.setDisable(true);
	}
	
	private final EventHandler<ActionEvent> btnChooseHomeDirListener = (ActionEvent e) -> {
		setBackupDirectory(new File(System.getProperty("user.home")));
	};
	
	private final EventHandler<ActionEvent> btnChooseDirListener = (ActionEvent e) -> {
		File dir = dirChooser.showDialog(btnChooseDir.getScene().getWindow());
		
		// Check if the user pressed cancel
		if(dir != null){
			setBackupDirectory(dir);
		}
	};
	
	private void setBackupDirectory(File dir){
		lblDirectory.setText("Directory Chosen: " + dir.getAbsolutePath());
		lblSize.setText("Calculating directory size........");
		
		// Calculating size may take a while, make sure the UI doesn't freeze
		new Thread(() -> {
			long totalSize = DirectorySize.calculate(dir.toPath());
			DecimalFormat format = new DecimalFormat("###,##0.## GB");
			
			StringBuilder sizeFormatted = new StringBuilder();
			
			sizeFormatted.append("Total size of directory: ")
				.append(format.format(totalSize / 1000000000D));
			
			if(totalSize > 10000000000L){
				sizeFormatted.append("! This may get expensive to upload!");
			}
			
			Platform.runLater(() -> {lblSize.setText(sizeFormatted.toString());});
		}, "DirectorySizeCalculator").start();
		
		backupPath = dir;
		btnBackup.setDisable(false);
	}
	
	private final EventHandler<ActionEvent> btnBackupListener = (ActionEvent e) -> {
		if(backupPath == null){
			lblDirectory.setText("Please select a directory to backup first.");
			btnBackup.setDisable(false);
			return;
		}
		
		btnBackup.setDisable(true);
		
		List files = new ArrayList();
		files.add(backupPath);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		
		File destination = new File("/tmp/SFM_Backup_" + sdf.format(new Date()) + ".zip");
		
		Zipper zipper = new Zipper(files, destination);
		
		zipper.addListener(this);
		
		zipper.zip(Zip4jConstants.COMP_DEFLATE, Zip4jConstants.DEFLATE_LEVEL_FASTEST);
	};

	@Override
	public void progressChanged(double newProgress, String currentFile) {
		Platform.runLater(() -> {
			progressBar.setProgress(newProgress);
			lblProgress.setText("Adding: " + currentFile);
		});
	}

	@Override
	public void zippingCompleted(File zip) {
		Platform.runLater(() -> {
			progressBar.setProgress(1);
			lblProgress.setText("Done zipping, starting upload. "
				+ "Check the \"Upload Queue\" tab to see upload progress.");
			
			btnBackup.setDisable(false);
		});
		
		new UploadManager().addFileToQueue(zip, 5);
	}
}
