package nl.fornax;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import nl.fornax.obj.FileTableRow;
import nl.fornax.obj.SiaFile;

/**
 * @author Fornax
 */
public class SiaFilesTable {

	public void fill(TableView tv) {
		List<SiaFile> fileList = FileMonitor.getList();

		ObservableList<FileTableRow> data = FXCollections.observableArrayList();

		fileList.stream().forEach((file) -> {
			data.add(new FileTableRow(
				file.getFileName(),
				file.getSize(),
				file.getTimeRemaining(),
				"Type unknown",
				file.getSiaHandle()
			));
		});

		tv.setItems(data);
	}
}
