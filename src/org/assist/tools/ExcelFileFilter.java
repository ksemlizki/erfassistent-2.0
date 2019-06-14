package org.assist.tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ExcelFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		if (file.getName().toLowerCase().endsWith(".xls") || file.getName().toLowerCase().endsWith(".xlsx")) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "MS Excel (*.xls, *.xlsx)";
	}
}