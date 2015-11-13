package nl.fornax.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import nl.fornax.FileListChangeListener;
import nl.fornax.FileMonitor;
import nl.fornax.misc.TableNoSelectionView;
import nl.fornax.obj.SiaFile;
import nl.fornax.obj.UploadQueueRow;

/**
 * @author Fornax
 */
public class UploadQueueController implements Initializable, FileListChangeListener{

	@FXML
	private TableView<UploadQueueRow> tableUploads;
	@FXML
	private TableColumn tableColUploadName;
	@FXML
	private TableColumn tableColUploadSize;
	@FXML
	private TableColumn tableColUploadProgress;
	@FXML
	private TableColumn tableColUploadId;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableUploads.setPlaceholder(new Label("Active file transfers will show up here"));
		
		tableUploads.setSelectionModel(new TableNoSelectionView(tableUploads));
		
		tableColUploadName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColUploadProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));
		tableColUploadId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColUploadSize.setCellValueFactory(new PropertyValueFactory<>("size"));
		
		tableColUploadName.setSortable(false);
		tableColUploadProgress.setSortable(false);
		tableColUploadId.setSortable(false);
		tableColUploadSize.setSortable(false);
		
		FileMonitor.addListener(this);
	}

	@Override
	public void fileListChanged(List<SiaFile> newList) {
		
		List<UploadQueueRow> transfers = new ArrayList();
		List<SiaFile> activeTransfers = FileMonitor.getActiveTransfers();
		
		
		for(SiaFile f : activeTransfers){
			UploadQueueRow r = new UploadQueueRow();
			r.setName(f.getFileName());
			r.setProgress(f.getUploadProgress());
			r.setSize(f.getSize());
			r.setId(f.getSiaHandle()); // TODO: Add destination
			
			transfers.add(r);
		}
		
		transfers.sort((UploadQueueRow o1, UploadQueueRow o2) -> {
			Double d1 = o1.getProgress();
			Double d2 = o2.getProgress();
			return d1.compareTo(d2);
		});
		
		Collections.reverse(transfers);
		
		ObservableList<UploadQueueRow> data = FXCollections.observableArrayList();

		transfers.stream().forEach((UploadQueueRow file) -> {
			data.add(file);
		});
		
		// This method runs in the FileMonitor thread, so we need to move out of there
		Platform.runLater(() -> {tableUploads.setItems(data);});
		
	}
}
