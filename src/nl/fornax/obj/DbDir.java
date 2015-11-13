package nl.fornax.obj;

/**
 * @author Fornax
 */
public class DbDir {
	private int id;
	private String name;
	private int parentId;

	public DbDir(int id, String name, int parentId) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
	}

	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public int getParentId() {return parentId;}
	public void setParentId(int parentId) {this.parentId = parentId;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
}
