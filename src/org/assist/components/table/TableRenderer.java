package org.assist.components.table;

import java.awt.Color;
import java.awt.Component;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.assist.tools.Tools;

public class TableRenderer extends DefaultTableCellRenderer {

	private static final Color MARKED_BG_COLOR = new Color(0x80, 0xFF, 0xff);
	private static final Color BG_COLOR = new Color(0xE3, 0xE3, 0xE3);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel rendererComponent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		TableModel model = (TableModel) table.getModel();

		rendererComponent.setFont(table.getTableHeader().getFont());
		rendererComponent.setBorder(new EmptyBorder(0, 5, 0, 5));
		rendererComponent.setHorizontalAlignment(model.getColumnAlign(column));

		if (isSelected) {
			rendererComponent.setBackground(table.getSelectionBackground());
		}
		else {
			if (model.getData(table, row).isMarked()) {
				rendererComponent.setBackground(MARKED_BG_COLOR);
			}
			else {
				rendererComponent.setBackground(((row % 2) == 0) ? Color.WHITE : BG_COLOR);
			}
		}

		if (value instanceof Boolean) {
			rendererComponent.setText(((Boolean) value).booleanValue() ? "S" : "H");
		}
		else if (value instanceof Double) {
			rendererComponent.setText(Tools.decimalFormat((Double) value));
		}
		else if (value instanceof GregorianCalendar) {
			rendererComponent.setText(Tools.format((GregorianCalendar) value));
		}

		if (column == 7) {//Buchungstext
			rendererComponent.setToolTipText(rendererComponent.getText());
		}
		else {
			rendererComponent.setToolTipText(null);
		}


		return rendererComponent;
	}

//	@Override
//	public void paint(Graphics g) {
//		super.paint(g);
//
//		Dimension size = getSize();
//		g.setColor(Color.LIGHT_GRAY);
//		g.drawLine(size.width - 1, 0, size.width - 1, size.height - 1);
//	}

}
