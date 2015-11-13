package nl.fornax;

import java.io.File;

/**
 * @author Fornax
 */
public class ConfigDir {

	private static String configDir;

	public ConfigDir() {
		if (configDir == null) {
			switch (SiaFileManager.OPERATING_SYSTEM) {
				case "Linux":
					configDir = System.getProperty("user.home") + "/.config/SiaFileManager/";
					break;
				case "Windows":
					configDir = System.getProperty("user.home") + "\\AppData\\Roaming\\SiaFileManager\\";
					break;
				default:
					configDir = System.getProperty("user.home") + "/.SiaFileManager/";
					break;
			}

			File dir = new File(configDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
	}

	public String getConfigDir() {
		return configDir;
	}
}
