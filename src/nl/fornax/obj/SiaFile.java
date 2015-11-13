package nl.fornax.obj;

/**
 * @author Fornax
 */
public class SiaFile {
	private String fileName;
	private String siaHandle;
	private double uploadProgress;
	private long size;
	private boolean available;
	private long timeRemaining;

	public String getFileName() {return fileName;}
	public void setFileName(String fileName) {this.fileName = fileName;}
	public String getSiaHandle() {return siaHandle;}
	public void setSiaHandle(String siaHandle) {this.siaHandle = siaHandle;}
	public double getUploadProgress() {return uploadProgress;}
	public void setUploadProgress(double uploadProgress) {this.uploadProgress = uploadProgress;}
	public long getSize() {return size;}
	public void setSize(long size) {this.size = size;}
	public boolean isAvailable() {return available;}
	public void setAvailable(boolean available) {this.available = available;}
	public long getTimeRemaining() {return timeRemaining;}
	public void setTimeRemaining(long timeRemaining) {this.timeRemaining = timeRemaining;}
}
