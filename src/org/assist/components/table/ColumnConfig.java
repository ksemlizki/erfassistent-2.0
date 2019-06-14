package org.assist.components.table;

import javax.swing.SwingConstants;

public class ColumnConfig {

	private String name;
	private int width;
	private Class<?> clazz;
	private int align;

	public ColumnConfig(String name, int width, Class<?> clazz) {
		this(name, width, clazz, SwingConstants.CENTER);
	}

	public ColumnConfig(String name, int width, Class<?> clazz, int align) {
		super();
		this.name = name;
		this.width = width;
		this.clazz = clazz;
		this.align = align;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public Class<?> getColumnClass() {
		return clazz;
	}

	public int getAlign() {
		return align;
	}

}
