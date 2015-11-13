package nl.fornax.obj;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * @author Fornax
 */
public class TreeDirectoryNode extends TreeItem{
	
	private int databaseId;

	public TreeDirectoryNode(int databaseId) {
		this.databaseId = databaseId;
	}

	public TreeDirectoryNode(int databaseId, Object value) {
		super(value);
		this.databaseId = databaseId;
	}

	public TreeDirectoryNode(int databaseId, Object value, Node graphic) {
		super(value, graphic);
		this.databaseId = databaseId;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}
	
	
}
