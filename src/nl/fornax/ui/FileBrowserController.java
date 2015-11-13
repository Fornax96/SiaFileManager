package nl.fornax.ui;

import com.almworks.sqlite4java.SQLiteException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import nl.Fornax.SiaRenter;
import nl.fornax.DatabaseConnection;
import nl.fornax.DirectoryTreeMapper;
import nl.fornax.FileTracker;
import nl.fornax.SiaFilesTable;
import nl.fornax.UploadManager;
import nl.fornax.obj.DbFile;
import nl.fornax.obj.FileTableRow;
import nl.fornax.obj.TreeDirectoryNode;

/**
 * @author Fornax
 */
public class FileBrowserController implements Initializable {

	@FXML
	private TreeView directoriesTree;
	@FXML
	private TableView<FileTableRow> tableFiles;
	@FXML
	private TableColumn tableColName;
	@FXML
	private TableColumn tableColSize;
	@FXML
	private TableColumn tableColId;
	
	private UploadManager uploadManager;
	private DirectoryChooser downloadDirChooser;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableFiles.setPlaceholder(new Label("Drop files here to upload them to the selected directory"));
		
		directoriesTree.getSelectionModel().selectedItemProperty().addListener(treeItemSelectedListener);
		tableFiles.setOnDragDropped(dragDroppedListener);
		tableFiles.setOnDragOver(dragOverListener);
		tableFiles.getSelectionModel().setCellSelectionEnabled(true);
		tableFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tableColName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColSize.setCellValueFactory(new PropertyValueFactory<>("size"));
		tableColId.setCellValueFactory(new PropertyValueFactory<>("id"));
		
		uploadManager = new UploadManager();
		downloadDirChooser = new DirectoryChooser();
		
		updateFileTree();
	}
	
	public List<FileTableRow> getSelectedItems(){
		return tableFiles.getSelectionModel().getSelectedItems();
	}
	
	public void updateFileTree() {
		TreeItem<String> rootNode = new TreeItem("TREE_ROOT");
		rootNode.setExpanded(true);

		TreeDirectoryNode dirNode = new TreeDirectoryNode(-1, "Directories");
		dirNode.setExpanded(true);
		
		DirectoryTreeMapper mapper = new DirectoryTreeMapper(dirNode);
		try {
			mapper.map();
		} catch (SQLiteException ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}

		rootNode.getChildren().add(dirNode);

		TreeItem<String> otherFilesNode = new TreeItem("Other Sia Files");
		rootNode.getChildren().add(otherFilesNode);

		directoriesTree.setRoot(rootNode);
	}

	private void treeDirectorySelected(int id) {
		selectedDirectory = id;
		
		if(id < 0){return;}
		
		DatabaseConnection dbc = new DatabaseConnection();
		try {
			List<DbFile> files = dbc.getFilesInDirectory(id);
			
			ObservableList<FileTableRow> data = FXCollections.observableArrayList();
			
			files.stream().forEach((file) -> {
				data.add(new FileTableRow(
					file.getName(),
					file.getSize(),
					1000,
					"Not supported",
					file.getSiadFileHandle()
				));
			});
			
			tableFiles.setItems(data);
		} catch (SQLiteException ex) {
			// TODO: Directory not found
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		dbc.close();
	}
	
	public void doDownloadSelected(){
		List<FileTableRow> list = getSelectedItems();
		
		if(list.isEmpty()){
			return;
		}else if(list.size() == 1){
			downloadDirChooser.setTitle("Select a directory to download file to");
		}else{
			downloadDirChooser.setTitle("Select a directory to download files to");
		}
		
		File dir = downloadDirChooser.showDialog(tableFiles.getScene().getWindow());
		
		if(dir == null){
			return;
		}
		
		// TODO: Maybe a good idea to implement a real download queue system
		
		SiaRenter renter = new SiaRenter();
		
		// Start a new Thread for HTTP requests so it doesn't clog the UI thread
		new Thread(() -> {
			for(FileTableRow f : list){
				DbFile dbFile = FileTracker.getFileByHandle(f.getId());

				String fileName;
				if(dbFile == null){
					fileName = f.getId();
				}else{
					fileName = dbFile.getName();
				}

				try {
					renter.download(f.getId(), dir.getCanonicalPath() + "/" + fileName);
				} catch (IOException ex) {
					Logger.getLogger(FileBrowserController.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}).start();
	}
	
	private int selectedDirectory = -1;

	private final ChangeListener treeItemSelectedListener = (
		ObservableValue observable, Object oldValue, Object newValue
	) -> {
		if (newValue.getClass().equals(TreeDirectoryNode.class)) {
			TreeDirectoryNode item = (TreeDirectoryNode) newValue;
			treeDirectorySelected(item.getDatabaseId());
		} else{
			TreeItem item = (TreeItem) newValue;
			
			if (item.getValue().equals("Other Sia Files")) {
				new SiaFilesTable().fill(tableFiles);
			}
		}
	};

	
	private final EventHandler<DragEvent> dragDroppedListener = (DragEvent event) ->{
		Dragboard db = event.getDragboard();
		boolean success;
		
		if(db.hasFiles()){
			for(File f : db.getFiles()){
				uploadManager.addFileToQueue(f, selectedDirectory);
			}
			
			success = true;
		}else{
			success = false;
		}
		
		event.setDropCompleted(success);
		event.consume();
	};
	
	private final EventHandler<DragEvent> dragOverListener = (DragEvent event) -> {
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		} else {
			event.consume();
		}
	};
}
