package nl.fornax;

import com.almworks.sqlite4java.SQLiteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.fornax.obj.DbDir;
import nl.fornax.obj.DbFile;

/**
 * @author Fornax
 */
public class FileTracker implements DatabaseChangeListener{
	private static Map<String, DbFile> files;
	
	public void init(){
		if (files == null){
			try {
				files = new HashMap();
				
				List<DbFile> allFiles = new DatabaseConnection().getAllFiles();
				
				allFiles.stream().forEach((f) -> {
					files.put(f.getSiadFileHandle(), f);
				});
				
				DatabaseConnection.addListener(this);
			} catch (SQLiteException ex) {
				Logger.getLogger(FileTracker.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public static DbFile getFileByHandle(String handle){
		return files.getOrDefault(handle, null);
	}

	@Override
	public void databaseFileAdded(DbFile newFile) {
		files.put(newFile.getSiadFileHandle(), newFile);
	}

	@Override
	public void databaseDirAdded(DbDir newDir) {
		// TODO 
	}
}
