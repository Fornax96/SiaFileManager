package nl.fornax.obj;

/**
 * @author Fornax
 */
public class DbFile {
	private int id;
	private int directoryId;
	private String name;
	private long size;
	private String siadFileHandle;
	private String siadAsciiString;

	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public int getDirectoryId() {return directoryId;}
	public void setDirectoryId(int directoryId) {this.directoryId = directoryId;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public long getSize() {return size;}
	public void setSize(long size) {this.size = size;}
	public String getSiadFileHandle() {return siadFileHandle;}
	public void setSiadFileHandle(String siadFileHandle) {this.siadFileHandle = siadFileHandle;}
	public String getSiadAsciiString() {return siadAsciiString;}
	public void setSiadAsciiString(String siadAsciiString) {this.siadAsciiString = siadAsciiString;}
	
	
}
