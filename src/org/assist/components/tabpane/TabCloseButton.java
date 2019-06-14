package org.assist.components.tabpane;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import org.assist.constants.ImageConstants;

public class TabCloseButton extends JButton {

	public TabCloseButton() {
		super(ImageConstants.CLOSE);
		setBorder(null);
		setRolloverIcon(ImageConstants.CLOSE_FOCUS);
		setPressedIcon(ImageConstants.CLOSE_FOCUS);
		int size = 17;
		setPreferredSize(new Dimension(size, size));
		setContentAreaFilled(false);
		setFocusable(false);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				MouseListener[] mouseListeners = getParent().getMouseListeners();
				for (MouseListener mouseListener : mouseListeners) {
					mouseListener.mouseEntered(e);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				MouseListener[] mouseListeners = getParent().getMouseListeners();
				for (MouseListener mouseListener : mouseListeners) {
					mouseListener.mouseExited(e);
				}
			}
		});
	}
}
