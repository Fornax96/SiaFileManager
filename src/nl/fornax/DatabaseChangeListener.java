/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fornax;

import java.util.EventListener;
import nl.fornax.obj.DbDir;
import nl.fornax.obj.DbFile;

/**
 *
 * @author Fornax
 */
public interface DatabaseChangeListener extends EventListener{
	public void databaseFileAdded(DbFile newFile);
	public void databaseDirAdded(DbDir newDir);
}
