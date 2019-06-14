package org.assist.tools;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	private static final String NODE_NAME = "ErfAssistent";

	static {
		 Preferences node = Preferences.userRoot().node(NODE_NAME);
		 try {
			node.flush();
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static String getLastDir() {
		return Preferences.userRoot().node(NODE_NAME).get("LastDir", System.getProperty("user.dir"));
	}

	public static void setLastDir(String path) {
		Preferences node = Preferences.userRoot().node(NODE_NAME);
		node.put("LastDir", path);
		try {
			node.flush();
		}
		catch (BackingStoreException e) {
			Tools.log(e);

		}
	}

	public static int getAutosaveInterval() {
		return Preferences.userRoot().node(NODE_NAME).getInt("AutoSave", 15);
	}
}
