package nl.fornax.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import nl.fornax.UploadManager;

/**
 * @author Fornax
 */
public class MainController implements Initializable {

	private FileBrowserController fileBrowser;
	private UploadQueueController uploadQueue;
	private BackupController backup;
	
	@FXML private TabPane tabPane;
	@FXML private Tab tabFileBrowser;
	@FXML private Tab tabUploadQueue;
	@FXML private Tab tabBackup;

	// Button Controls
	@FXML private Button btnUpload;
	@FXML private Button btnDownload;
	@FXML private Button btnImport;
	@FXML private Button btnSettings;

	private UploadManager uploadManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			FXMLLoader fileBrowserLoader = new FXMLLoader(getClass().getResource("/nl/fornax/ui/fileBrowser.fxml"));
			tabFileBrowser.setContent(fileBrowserLoader.load());
			fileBrowser = fileBrowserLoader.getController();
			
			FXMLLoader uploadQueueLoader = new FXMLLoader(getClass().getResource("/nl/fornax/ui/uploadQueue.fxml"));
			tabUploadQueue.setContent(uploadQueueLoader.load());
			uploadQueue = uploadQueueLoader.getController();
			
			FXMLLoader backupLoader = new FXMLLoader(getClass().getResource("/nl/fornax/ui/backup.fxml"));
			tabBackup.setContent(backupLoader.load());
			backup = backupLoader.getController();
		} catch (IOException ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}

		btnDownload.setOnAction(downloadButtonListener);
	}

	protected UploadManager getUploadManager() {
		return uploadManager;
	}

	private final EventHandler<ActionEvent> downloadButtonListener = (ActionEvent e) -> {
		if(tabPane.getSelectionModel().getSelectedItem() == tabFileBrowser){
			fileBrowser.doDownloadSelected();
		}
	};
}
