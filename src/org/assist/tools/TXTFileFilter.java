package org.assist.tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class TXTFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		if (f.getName().toLowerCase().endsWith(".txt")) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Text-Datei (*.txt)";
	}
}
