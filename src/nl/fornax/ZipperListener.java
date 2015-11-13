/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fornax;

import java.io.File;
import java.util.EventListener;

/**
 *
 * @author Fornax
 */
public interface ZipperListener extends EventListener{
	public void progressChanged(double newProgress, String currentTask);
	public void zippingCompleted(File zip);
}
