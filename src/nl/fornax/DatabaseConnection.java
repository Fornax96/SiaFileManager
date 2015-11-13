package nl.fornax;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.fornax.obj.DbDir;
import nl.fornax.obj.DbFile;

/**
 * @author Fornax
 */
public class DatabaseConnection {

	private final File dbFile;
	private final SQLiteConnection db;

	public DatabaseConnection() {
		dbFile = new File(new ConfigDir().getConfigDir() + Constants.DATABASE_NAME);

		Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.SEVERE);

		if (!dbFile.exists()) {
			init();
		}
			
		db = new SQLiteConnection(dbFile);
		
		try {
			db.open();
		} catch (SQLiteException ex) {
			Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void close() {
		db.dispose();
	}

	public synchronized DbFile getFileInfo(int id) throws SQLiteException {
		SQLiteStatement st = db.prepare(
			"SELECT * FROM file "
			+ "WHERE id = " + id
		);
		
		if(st == null){
			return null;
		}

		DbFile f = new DbFile();
		f.setId(st.columnInt(0));
		f.setDirectoryId(st.columnInt(1));
		f.setName(st.columnString(2));
		f.setSize(st.columnLong(3));
		f.setSiadFileHandle(st.columnString(4));
		f.setSiadAsciiString(st.columnString(5));

		st.dispose();

		return f;
	}
	
	public synchronized DbFile getFileByHandle(String handle) throws SQLiteException {
		SQLiteStatement st = db.prepare(
			"SELECT * FROM file "
			+ "WHERE siad_file_handle = \"" + handle + "\""
		);
		
		DbFile f = new DbFile();
		f.setId(st.columnInt(0));
		f.setDirectoryId(st.columnInt(1));
		f.setName(st.columnString(2));
		f.setSize(st.columnLong(3));
		f.setSiadFileHandle(st.columnString(4));
		f.setSiadAsciiString(st.columnString(5));

		st.dispose();

		return f;
	}

	public synchronized DbDir getDirectoryInfo(int id) throws SQLiteException {
		SQLiteStatement st = db.prepare(
			"SELECT * FROM dir "
			+ "WHERE id = " + id
		);
		if (st.step()) {
			DbDir dir = new DbDir(
				st.columnInt(0),
				st.columnString(1),
				st.columnInt(2)
			);

			st.dispose();
			return dir;
		} else {
			st.dispose();
			return null;
		}
	}

	public synchronized List<DbFile> getAllFiles() throws SQLiteException {
		SQLiteStatement st = db.prepare(
			"SELECT * FROM file"
		);

		List<DbFile> list = new ArrayList();
		while (st.step()) {
			DbFile f = new DbFile();
			f.setId(st.columnInt(0));
			f.setDirectoryId(st.columnInt(1));
			f.setName(st.columnString(2));
			f.setSize(st.columnLong(3));
			f.setSiadFileHandle(st.columnString(4));
			f.setSiadAsciiString(st.columnString(5));
			
			list.add(f);
		}
		st.dispose();

		return list;
	}

	public synchronized List<DbFile> getFilesInDirectory(int directory) throws SQLiteException {
		SQLiteStatement st = db.prepare(
			"SELECT * FROM file "
			+ "WHERE directory_id = " + directory
		);

		List<DbFile> list = new ArrayList();
		while (st.step()) {
			DbFile f = new DbFile();
			f.setId(st.columnInt(0));
			f.setDirectoryId(st.columnInt(1));
			f.setName(st.columnString(2));
			f.setSize(st.columnLong(3));
			f.setSiadFileHandle(st.columnString(4));
			f.setSiadAsciiString(st.columnString(5));
			
			list.add(f);
		}
		st.dispose();

		return list;
	}

	public synchronized List<DbDir> getDirectoryChildren(Integer id) throws SQLiteException {
		// Woo, Ternary!
		String where = (id == null) ? "IS NULL" : "= " + id;
		
		SQLiteStatement st = db.prepare(
			"SELECT * FROM dir "
			+ "WHERE parent_id " + where
		);

		List<DbDir> list = new ArrayList();
		while (st.step()) {
			list.add(new DbDir(
				st.columnInt(0),
				st.columnString(1),
				st.columnInt(2)
			));
		}

		st.dispose();

		return list;
	}
	
	public synchronized void addDirectory(String name, Integer parent) throws SQLiteException {
		db.exec("INSERT INTO dir "
			+ "(name, parent_id) "
			+ "VALUES "
			+ "(\"" + name + "\", \"" + parent + "\")"
		);
	}
	
	public synchronized void addFile(DbFile f) throws SQLiteException {
		db.exec("INSERT INTO file "
			+ "(directory_id, name, size, siad_file_handle, siad_ascii) "
			+ "VALUES "
			+ "(\"" + f.getDirectoryId() + "\", "
			+ "\"" + f.getName() + "\", "
			+ f.getSize() + ", "
			+ "\"" + f.getSiadFileHandle() + "\", "
			+ "\"" + f.getSiadAsciiString() + "\")"
		);
		
		notifyNewFileListeners(f);
	}
	
	
	private synchronized void init() {
		try {
			SQLiteConnection initdb = new SQLiteConnection(dbFile);
			initdb.open(true);
			
			initdb.exec("CREATE TABLE dir ("
				+ "id INTEGER PRIMARY KEY, "
				+ "name VARCHAR(100) NOT NULL, "
				+ "parent_id INTEGER DEFAULT NULL)"
			);

			initdb.exec("INSERT INTO dir (name) VALUES (\"Documents\")");
			initdb.exec("INSERT INTO dir (name) VALUES (\"Music\")");
			initdb.exec("INSERT INTO dir (name) VALUES (\"Videos\")");
			initdb.exec("INSERT INTO dir (name) VALUES (\"Pictures\")");
			initdb.exec("INSERT INTO dir (name) VALUES (\"Backups\")");
			initdb.exec("INSERT INTO dir (name) VALUES (\"Import\")");

			initdb.exec("CREATE TABLE file ("
				+ "id INTEGER PRIMARY KEY, "
				+ "directory_id INTEGER DEFAULT NULL,"
				+ "name VARCHAR(1000),"
				+ "size INTEGER NOT NULL,"
				+ "siad_file_handle VARCHAR(1000) NOT NULL,"
				+ "siad_ascii VARCHAR(5000) DEFAULT NULL)"
			);
			
			initdb.dispose();
		} catch (SQLiteException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private static final List<DatabaseChangeListener> listeners = new ArrayList();
	
	public static void addListener(DatabaseChangeListener listener){
		listeners.add(listener);
	}
	
	private static void notifyNewFileListeners(DbFile newFile){
		listeners.stream().forEach((listener) -> {
			listener.databaseFileAdded(newFile);
		});
	}
}
