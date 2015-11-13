package nl.fornax;

import com.almworks.sqlite4java.SQLiteException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.Fornax.SiaRenter;
import nl.fornax.obj.DbFile;

/**
 * @author Fornax
 */
public class UploadManager {
	
	public void addFileToQueue(File file, int destination){
		if(file.isDirectory()){
			// TODO: Make fancy status message
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
				"Uploading directories is not supported yet"
			);
			return;
		}
		
		if(!file.exists()){
			// TODO: Make fancy status message
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
				"File does not exist"
			);
			return;
		}
		
		if(FileMonitor.getFileByName(file.getName()) != null){
			// TODO: Make fancy status message
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
				"A file with this name already exists in your Sia wallet"
			);
			return;
		}
		
		String directoryName;
		try {
			directoryName = new DatabaseConnection().getDirectoryInfo(destination).getName();
		} catch (SQLiteException ex) {
			Logger.getLogger(UploadManager.class.getName()).log(Level.SEVERE, 
				"The directory you selected does not exist in the database, "
					+ "which is theoretically impossible!"
			);
			return;
		}
		
		String fileId = "SFM_" + RandString.generate(6);
		
		System.out.println("Calling upload API");
		try {
			SiaRenter renter = new SiaRenter();
			renter.upload(file.getAbsolutePath(), fileId);
		} catch (IOException ex) {
			// TODO: Handle broken siad connection
			Logger.getLogger(UploadManager.class.getName()).log(Level.SEVERE, null, ex);
			
			// Return to make sure this file will not get added to the db
			return;
		}
		
		System.out.println("Adding file to database");
		try {
			DbFile f = new DbFile();
			f.setDirectoryId(destination);
			f.setName(file.getName());
			f.setSize(file.length());
			f.setSiadFileHandle(fileId);
			
			new DatabaseConnection().addFile(f);
		} catch (SQLiteException ex) {
			// TODO: Handle this too
			Logger.getLogger(UploadManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		Logger.getLogger(getClass().getName()).log(Level.INFO, "File \"{0}\" added to upload queue", file.getName());
	}
}
