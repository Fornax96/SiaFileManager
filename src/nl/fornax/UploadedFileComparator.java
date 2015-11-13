package nl.fornax;

import java.util.Comparator;
import nl.Fornax.obj.UploadedFile;

/**
 * @author Fornax
 */
public class UploadedFileComparator implements Comparator<UploadedFile>{

	@Override
	public int compare(UploadedFile o1, UploadedFile o2) {
		return o1.getNickname().compareTo(o2.getNickname());
	}
}
