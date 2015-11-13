package nl.fornax.obj;

/**
 * @author Fornax
 */
public class FileTableRow {
	private String name;
	private long size;
	private long timeRemaining;
	private String mime;
	private String id;

	public FileTableRow(String name, long size, long timeRemaining, String mime, String id) {
		this.name = name;
		this.size = size;
		this.timeRemaining = timeRemaining;
		this.mime = mime;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(long timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
