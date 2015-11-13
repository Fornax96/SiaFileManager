package nl.fornax;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;

/**
 * @author Fornax
 */
public class Zipper {

	private final List<File> files;
	private final File destination;
	private long totalWork;
	
	public Zipper(List<File> files, File destination){
		this.files = files;
		this.destination = destination;
	}

	/**
	 * Starts the zipping thread.
	 * 
	 * Does not block execution, you can follow progress by calling addListener(this)
	 * 
	 * @param compressionMethod The method used for compression, 
	 * defined in Zip4jConstants.COMP
	 * @param compressionLevel The strength of the compression, 
	 * defined in Zip4jConstants.DEFLATE_LEVEL
	 */
	public void zip(int compressionMethod, int compressionLevel) {
		final Thread thread = new Thread(() -> {
			try {
				ZipFile zipfile = new ZipFile(destination);
				ZipParameters parameters = new ZipParameters();
				parameters.setCompressionMethod(compressionMethod);
				parameters.setCompressionLevel(compressionLevel);
				
				files.stream().forEach((File f) -> {
					totalWork += DirectorySize.calculate(f.toPath());
				});
				
				startMonitoringThread(zipfile.getProgressMonitor());
				
				for (File f : files) {
					if (f.isDirectory()) {
						zipfile.addFolder(f, parameters);
					} else if (f.isFile()) {
						zipfile.addFile(f, parameters);
					}
				}
				
				notifyCompletionListeners(zipfile.getFile());
			} catch (ZipException ex) {
				notifyChangeListeners(1, "Zipping Failed");
				notifyCompletionListeners(null);
				
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception during zipping", ex);
			}
			
			timer.cancel();
		}, "ZipperThread");

		thread.start();
	}
	
	private final Timer timer = new Timer("ZipperMonitor");

	private void startMonitoringThread(ProgressMonitor monitor) {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(monitor.getResult() == ProgressMonitor.RESULT_CANCELLED
					|| monitor.getResult() == ProgressMonitor.RESULT_ERROR
				){
					timer.cancel();
				}
				
				long completed = monitor.getWorkCompleted();
				
				notifyChangeListeners(
					(totalWork == 0L || completed == 0L) ? 0D : (double) completed / totalWork,
					monitor.getFileName()
				);
			}
		}, 100, 50);
	}

	private final List<ZipperListener> listeners = new ArrayList();

	public void addListener(ZipperListener listener) {
		listeners.add(listener);
	}

	private void notifyChangeListeners(double newProgress, String currentTask) {
		listeners.stream().forEach((listener) -> {
			listener.progressChanged(newProgress, currentTask);
		});
	}

	private void notifyCompletionListeners(File zip) {
		timer.cancel();
		
		listeners.stream().forEach((listener) -> {
			listener.zippingCompleted(zip);
		});
	}
}
