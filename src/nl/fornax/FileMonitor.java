package nl.fornax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.Fornax.SiaRenter;
import nl.Fornax.obj.UploadedFile;
import nl.fornax.obj.DbFile;
import nl.fornax.obj.SiaFile;

/**
 * @author Fornax
 */
public final class FileMonitor extends TimerTask{
	private static final Timer timer = new Timer("FileMonitor");
	private static SiaRenter renter = new SiaRenter();
	private static List<SiaFile> fileList = new ArrayList();
	private static List<SiaFile> activeTransfers = new ArrayList();
	
	// Used to keep track of equality
	private static List<UploadedFile> oldFiles = new ArrayList();
	
	private static boolean fastPolling = false;
	private static boolean isEnabled = false;
	
	@SuppressWarnings("LeakingThisInConstructor") // Nothing to worry about, NetBeans
	public FileMonitor(){
		if(!isEnabled){
			timer.schedule(this, 100, 2000);
			isEnabled = true;
		}
	}
	
	private static int runCounter = 2;

	@Override
	public void run() {
		if(!fastPolling){
			if(runCounter == 3){
				runCounter = 0;
			}else{
				runCounter++;	
				return;
			}
		}
		
		try {
			List<UploadedFile> newFiles = renter.listFiles();
			Collections.sort(newFiles, new UploadedFileComparator());
			
			if(!newFiles.equals(oldFiles)){
				oldFiles = newFiles;
				
				// Start with an empty list
				activeTransfers = new ArrayList();
				List<SiaFile> newFileList = new ArrayList();
				
				for(UploadedFile uf : newFiles){
					SiaFile f = new SiaFile();
					f.setAvailable(uf.isAvailable());
					f.setSiaHandle(uf.getNickname());
					f.setSize(uf.getFileSize());
					f.setUploadProgress(uf.getUploadProgress());
					f.setFileName(getFileName(uf.getNickname()));
					
					newFileList.add(f);
					
					if(f.getUploadProgress()< 99.9){
						activeTransfers.add(f);
					}
				}
				
				fileList = newFileList;
				
				notifyListeners();
				
				fastPolling = true;
			}else{
				fastPolling = false;
			}
		} catch (IOException ex) {
			// TODO: Create a nice status message saying that the connection could not be made
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Could not connect to siad API");
		}
	}
	
	private static final DatabaseConnection db = new DatabaseConnection();
	
	private static String getFileName(String handle){
		DbFile f = FileTracker.getFileByHandle(handle);
		
		return (f == null) ? "Not SFM file" : f.getName();
	}
	
	public static SiaFile getFileByName(String name){
		for(SiaFile f : fileList){
			if(f.getFileName().equals(name)){
				return f;
			}
		}
		
		return null;
	}
	
	public static List<SiaFile> getList(){
		return new ArrayList(fileList);
	}
	
	public static List<SiaFile> getActiveTransfers(){
		return new ArrayList(activeTransfers);
	}
	
	public static void updateSiaIpAndPort(String ip, int port){
		renter = new SiaRenter(ip, port);
	}
	
	private static final List<FileListChangeListener> listeners = new ArrayList();
	
	public static void addListener(FileListChangeListener listener){
		listeners.add(listener);
	}
	
	private static void notifyListeners(){
		listeners.stream().forEach((listener) -> {
			listener.fileListChanged(fileList);
		});
	}
}