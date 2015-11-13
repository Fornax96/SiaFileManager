package nl.fornax;

import java.util.EventListener;
import java.util.List;
import nl.fornax.obj.SiaFile;

/**
 * @author Fornax
 */
public interface FileListChangeListener extends EventListener{
	public void fileListChanged(List<SiaFile> newList);
}
