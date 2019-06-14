/*
 *	Copyright 2009 by opta data Abrechnungs GmbH
 */
package org.assist.tools;

import java.awt.Image;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class ImageLoader {

	private static final HashMap<String, ImageIcon> ICONS = new HashMap<String, ImageIcon>();

	private static ClassLoader classLoader = ImageLoader.class.getClassLoader();

	public static final ImageIcon getIcon(String name) {
		ImageIcon icon = ICONS.get(name);
		if (icon == null) {
			icon = new ImageIcon(classLoader.getResource("images/" + name));
			ICONS.put(name, icon);
		}
		return icon;
	}

	public static final Image getImage(String name) {
		return getIcon(name).getImage();
	}

}
