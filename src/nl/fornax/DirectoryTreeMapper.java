package nl.fornax;

import com.almworks.sqlite4java.SQLiteException;
import nl.fornax.obj.DbDir;
import nl.fornax.obj.TreeDirectoryNode;

/**
 * @author Fornax
 */
public class DirectoryTreeMapper {
	private final TreeDirectoryNode root;
	private final DatabaseConnection db;
	
	public DirectoryTreeMapper(TreeDirectoryNode root){
		this.root = root;
		db = new DatabaseConnection();
	}
	
	public void map() throws SQLiteException{
		for(DbDir dir : db.getDirectoryChildren(null)){
			TreeDirectoryNode child = addToLink(dir, root);
			addChildren(dir, child);
		}
		
		db.close();
	}
	
	// This shit is bananas. B-A-NAN-A-S!
	private void addChildren(DbDir dir, TreeDirectoryNode parent) throws SQLiteException{
		for(DbDir dirNode : db.getDirectoryChildren(dir.getId())){
			TreeDirectoryNode child = addToLink(dirNode, parent);
			addChildren(dirNode, child);
		}
	}
	
	private TreeDirectoryNode addToLink(DbDir dir, TreeDirectoryNode parentNode){
		TreeDirectoryNode item = new TreeDirectoryNode(dir.getId(), dir.getName());
		
		parentNode.getChildren().add(item);
		return item;
	}
}
