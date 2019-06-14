package org.assist.components.entry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.assist.constants.GeneralConstants;
import org.assist.domain.TaxKey;

public class TaxKeyListCellRenderer extends DefaultListCellRenderer {

	private int index;
	private boolean selected;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int pIndex, boolean pSelected, boolean cellHasFocus) {
		index = pIndex;
		selected = pSelected;
		JLabel rendererComponent = (JLabel)super.getListCellRendererComponent(list, value, pIndex, pSelected, cellHasFocus);
		rendererComponent.setFont(GeneralConstants.FORM_FONT.deriveFont(Font.PLAIN));
		TaxKey taxKey = (TaxKey)value;
		if (taxKey != null) {
			rendererComponent.setText("<html><b>" + taxKey.getId() + "</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>" + taxKey.getDescription() + "</i><html>");
		}
		return rendererComponent;
	}

	@Override
	public void paint(Graphics graphics) {
		((Graphics2D)graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(graphics);

		if (!selected && ((index % 2) == 0)) {
			Dimension size = getSize();
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.drawLine(0, size.height - 1, size.width, size.height - 1);
		}
	}

}
