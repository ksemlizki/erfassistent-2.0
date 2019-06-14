package org.assist.components.table;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.assist.domain.Data;
import org.assist.domain.TaxKey;

public class TableModel extends DefaultTableModel {

	private ArrayList<ColumnConfig> columnConfigs;

	public TableModel() {
		columnConfigs = new ArrayList<ColumnConfig>();

		columnConfigs.add(new ColumnConfig("Betrag", 50, Double.class, SwingConstants.RIGHT));
		columnConfigs.add(new ColumnConfig("S/H", 40, Boolean.class));
		columnConfigs.add(new ColumnConfig("Gegenkonto", 50, String.class));
		columnConfigs.add(new ColumnConfig("St.schl.", 50, TaxKey.class));
		columnConfigs.add(new ColumnConfig("Belegnummer", 60, String.class));
		columnConfigs.add(new ColumnConfig("Datum", 80, GregorianCalendar.class, SwingConstants.LEFT));
		columnConfigs.add(new ColumnConfig("Konto", 50, String.class));
		columnConfigs.add(new ColumnConfig("Buchungstext", 350, String.class, SwingConstants.LEFT));
	}

	@Override
	public int getColumnCount() {
		return columnConfigs.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex < getColumnCount()) {
			return columnConfigs.get(columnIndex).getName();
		}

		return "";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex < getColumnCount()) {
			return columnConfigs.get(columnIndex).getColumnClass();
		}
		return super.getColumnClass(columnIndex);
	}

	public int getColumnWidth(int columnIndex) {
		if (columnIndex < getColumnCount()) {
			return columnConfigs.get(columnIndex).getWidth();
		}

		return 0;
	}

	public int getColumnAlign(int columnIndex) {
		if (columnIndex < getColumnCount()) {
			return columnConfigs.get(columnIndex).getAlign();
		}

		return SwingConstants.CENTER;
	}

	public Data getData(JTable table, int row) {
		return getRow(table, row).getData();
	}

	public TableRow getRow(JTable table, int row) {
		return ((TableRow)getDataVector().get(table.convertRowIndexToModel(row)));
	}

	@SuppressWarnings("unchecked")
	public void setData(int row, Data data) {
		getDataVector().set(row, new TableRow(data));

	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public ArrayList<Data> getAllData() {
		ArrayList<Data> allData = new ArrayList<Data>();
		for (Object object : dataVector) {
			allData.add(((TableRow)object).getData());
		}

		return allData;
	}

	@SuppressWarnings("unchecked")
	public void setDataVector(Vector<TableRow> data) {
		dataVector.clear();
		dataVector.addAll(data);
	}

	public int addData(Data data) {
		TableRow newRow = new TableRow(data);
		if (getRowCount() == 0) {
			addRow(newRow);
		}
		else {
			boolean inserted = false;
			for (int i = 0; i < getRowCount(); i++) {
				Data rowData = ((TableRow)getDataVector().get(i)).getData();
				if (rowData.getDate().compareTo(data.getDate()) > 0) {
					insertRow(i, newRow);
					inserted = true;
					return i;
				}
			}

			if (!inserted) {
				addRow(newRow);
			}
		}

		return getRowCount();
	}

}