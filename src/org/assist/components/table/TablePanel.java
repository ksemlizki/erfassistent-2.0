package org.assist.components.table;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;

import org.assist.components.common.TextField;
import org.assist.constants.ImageConstants;
import org.assist.domain.Data;
import org.assist.domain.Domain;
import org.assist.tools.DocumentAdapter;
import org.assist.tools.Tools;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TablePanel extends JPanel {

	protected JTable table;
	protected TableModel model;
	private ITableHandler selectionHandler;
	private ITableHandler doubleClickHandler;
	private ChangeListener changeListener;

	protected ArrayList<Data> searchResult;
	protected int searchResultIndex;
	protected JButton prevButton;
	protected JButton nextButton;
	protected TextField filterField;
	protected JPopupMenu popupMenu;

	public TablePanel() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		searchResultIndex = -1;
		searchResult = new ArrayList<Data>();

		model = new TableModel();
		table = new JTable(model);
		table.setRowSorter(null);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		DefaultTableColumnModel columnModel = (DefaultTableColumnModel)table.getColumnModel();

		TableRenderer renderer = new TableRenderer();
		table.setDefaultRenderer(Object.class, renderer);
		table.setDefaultRenderer(Double.class, renderer);
		table.setDefaultRenderer(Boolean.class, renderer);
		table.setDefaultRenderer(String.class, renderer);


		for (int i = 0; i < model.getColumnCount(); i++) {
			columnModel.getColumn(i).setPreferredWidth(model.getColumnWidth(i));
		}

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font(null, Font.BOLD, 12));
		header.setReorderingAllowed(false);
		header.setResizingAllowed(false);

		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.setRowSelectionAllowed(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (table.getSelectedRowCount() == 1) {
						int row = table.getSelectedRow();
						if (row != -1) {
							handleSelection(row);
						}
					}
					else {
						handleSelection(-1);
					}

				}
			}
		});

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				if ((event.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(event)) {
					handleDoubleClick(table.rowAtPoint(event.getPoint()));
				}
			}
			@Override
			public void mousePressed(MouseEvent event) {
				showPopup(event);
			}

			@Override
			public void mouseReleased(MouseEvent event) {
				showPopup(event);
			}

			private void showPopup(MouseEvent event) {
				if (event.isPopupTrigger()) {
					int row = table.rowAtPoint(event.getPoint());
					if ((row != table.getSelectedRow()) && (table.getSelectedRowCount() != 1)) {
						table.getSelectionModel().setSelectionInterval(row, row);
					}
					popupMenu.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});

		double[][] layoutSize = { { TableLayoutConstants.FILL, 30, 30, 30, 50, 30, 30, 30, 30 }, { 30 } };

		JPanel toolbar = new JPanel(new TableLayout(layoutSize));
		filterField = new TextField() {
			@Override
			public void paint(Graphics graphics) {
				super.paint(graphics);
				paintSearchResult(graphics);
			}
		};
		filterField.setLayout(new BorderLayout());
		filterField.setMargin(new Insets(0, 0, 0, 50));


		toolbar.add(filterField, "0, 0");

		prevButton = createToolbarButton(ImageConstants.MOVE_UP, "Aufwärts");
		nextButton = createToolbarButton(ImageConstants.MOVE_DOWN, "Abwärts");
		nextButton.setEnabled(false);
		prevButton.setEnabled(false);
		JButton clearFilterButton = new JButton(ImageConstants.CROSS);
		toolbar.add(clearFilterButton, "1, 0");
		toolbar.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 5));

		JButton topButton = createToolbarButton(ImageConstants.MOVE_TOP, "als erste");
		JButton upButton = createToolbarButton(ImageConstants.MOVE_UP, "nach oben");
		JButton downButton = createToolbarButton(ImageConstants.MOVE_DOWN, "nach unten");
		JButton bottomButton = createToolbarButton(ImageConstants.MOVE_BOTTOM, "als letzte");

		toolbar.add(prevButton, "2, 0");
		toolbar.add(nextButton, "3, 0");
		toolbar.add(topButton, "5, 0");
		toolbar.add(upButton, "6, 0");
		toolbar.add(downButton, "7, 0");
		toolbar.add(bottomButton, "8, 0");
		add(toolbar, BorderLayout.NORTH);
		add(new JScrollPane(table));

		prevButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				searchResultIndex--;
				if (searchResultIndex < 0) {
					searchResultIndex = searchResult.size() - 1;
				}

				select(searchResult.get(searchResultIndex));
				filterField.repaint();
			}
		});
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				searchResultIndex++;
				if (searchResultIndex > (searchResult.size() - 1)) {
					searchResultIndex = 0;
				}

				select(searchResult.get(searchResultIndex));
				filterField.repaint();
			}
		});
		topButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				moveTop();
			}
		});
		upButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				moveUp();
			}
		});
		downButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				moveDown();
			}
		});
		bottomButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				moveBottom();
			}
		});

		clearFilterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				filterField.setText(null);
				nextButton.setEnabled(false);
				prevButton.setEnabled(false);
				searchResultIndex = -1;
			}
		});

		filterField.getDocument().addDocumentListener(new DocumentAdapter() {

			@Override
			public void update() {
				search(filterField.getText());
			}
		});

	}

	public void setTablePopupMenu(JPopupMenu menu) {
		this.popupMenu = menu;
	}

	protected void search(String text) {
		searchResult.clear();
		if (!Tools.isStringEmpty(text)) {
			TableRenderer renderer = new TableRenderer();
			for (Data data : model.getAllData()) {
				boolean found = Tools.isStringEmpty(text);
				if (!found) {
					for (int column = 0; column < table.getColumnCount(); column++) {
						String cellContent = ((JLabel)renderer.getTableCellRendererComponent(table, data.toObjectArray()[column], false, false, 0, column)).getText();
						if (cellContent.toLowerCase().contains(text.toLowerCase())) {
							found = true;
							break;
						}
					}
				}

				if (found) {
					searchResult.add(data);
				}
			}
		}
		if (searchResult.isEmpty()) {
			nextButton.setEnabled(false);
			prevButton.setEnabled(false);
			searchResultIndex = -1;
		}
		else {
			nextButton.setEnabled(true);
			prevButton.setEnabled(true);
			searchResultIndex = 0;
			select(searchResult.get(searchResultIndex));
		}

	}

	public int findRow(Data data) {int rowCount = model.getRowCount();
		Integer dataId = data.getId();
		for (int row = 0; row < rowCount; row++) {
			Domain userObject = getData(row);
			if ((userObject).getId().compareTo(dataId) == 0) {
				return row;
			}
		}

		return -1;
	}

	private JButton createToolbarButton(ImageIcon icon, String toolip) {
		JButton button = new JButton(icon);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		button.setToolTipText(toolip);
		return button;
	}

	protected void handleSelection(int row) {
		if (selectionHandler != null) {
			selectionHandler.handle(row);
		}
	}

	protected void handleDoubleClick(int row) {
		if (doubleClickHandler != null) {
			doubleClickHandler.handle(row);
		}
	}

	public JTable getTable() {
		return table;
	}

	public void addRow(Data data) {
		model.addData(data);
	}

	public void setData(int selectedRow, Data data) {
		model.setData(table.convertRowIndexToModel(selectedRow), data);
		model.fireTableRowsUpdated(selectedRow, selectedRow);
	}

	public void removeRow(int selectedRow) {
		model.removeRow(table.convertRowIndexToModel(selectedRow));
	}

	public Data getData(int row) {
		return model.getData(table, row);
	}

	public ArrayList<Data> getAllData() {
		return model.getAllData();
	}

	public void setDataVector(Vector<TableRow> rows) {
		model.setDataVector(rows);
		model.fireTableRowsInserted(0, rows.size());
	}

	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	public void select(Data data) {

		int row = findRow(data);
		if (row != -1) {
			table.getSelectionModel().setSelectionInterval(row, row);
			scrollToRow(row);
			handleSelection(row);

		}
	}

	private void scrollToRow(int row) {
		table.scrollRectToVisible(table.getCellRect(row, 0, true));
	}

	public void setDoubleClickHandler(ITableHandler doubleClickHandler) {
		this.doubleClickHandler = doubleClickHandler;
	}

	public void setSelectionHandler(ITableHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}

	public void moveTop() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length > 0) {
			int firstSelectedRow = selectedRows[0];
			if (firstSelectedRow > 0) {

				TableRow row1 = model.getRow(table, firstSelectedRow);
				int firstRow = firstSelectedRow - 1;
				for (; firstRow >= 0; firstRow--) {
					TableRow row2 = model.getRow(table, firstRow);
					if (row2.getData().getDate().compareTo(row1.getData().getDate()) < 0) {
						break;
					}
				}
				for (int i = 0; i < selectedRows.length; i++) {
					model.insertRow(firstRow + i + 1, model.getRow(table, selectedRows[i]));
					model.removeRow(selectedRows[i] + 1);

				}
				table.clearSelection();
				table.addRowSelectionInterval(firstRow + 1, firstRow + selectedRows.length);
				scrollToRow(firstRow + 1);
				changeListener.stateChanged(null);
			}
		}
	}

	public void moveUp() {
		int[] selectedRows = table.getSelectedRows();

		if (selectedRows.length > 0) {
			int firstSelectedRow = selectedRows[0];
			if (firstSelectedRow > 0) {
				TableRow row1 = model.getRow(table, firstSelectedRow);
				TableRow row2 = model.getRow(table, firstSelectedRow - 1);
				if (row2.getData().getDate().compareTo(row1.getData().getDate()) == 0) {
					for (int i = 0; i < selectedRows.length; i++) {
						model.insertRow(selectedRows[i] - 1, model.getRow(table, selectedRows[i]));
						model.removeRow(selectedRows[i] + 1);
					}
					table.clearSelection();
					for (int i = 0; i < selectedRows.length; i++) {
						table.addRowSelectionInterval(selectedRows[i] - 1, selectedRows[i] - 1);
					}
					scrollToRow(firstSelectedRow - 1);
					changeListener.stateChanged(null);
				}
			}
		}
	}

	public void moveDown() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length > 0) {
			int lastSelectedRow = selectedRows[selectedRows.length - 1];
			if (lastSelectedRow < (table.getRowCount() - 1)) {
				TableRow row1 = model.getRow(table, lastSelectedRow);
				TableRow row2 = model.getRow(table, lastSelectedRow + 1);
				if (row2.getData().getDate().compareTo(row1.getData().getDate()) == 0) {
					for (int i = selectedRows.length - 1; i >= 0; i--) {
						model.insertRow(selectedRows[i] + 2, model.getRow(table, selectedRows[i]));
						model.removeRow(selectedRows[i]);
					}
					table.clearSelection();
					for (int i = 0; i < selectedRows.length; i++) {
						table.addRowSelectionInterval(selectedRows[i] + 1, selectedRows[i] + 1);
					}
					scrollToRow(lastSelectedRow + 1);
					changeListener.stateChanged(null);
				}
			}
		}
	}

	public void moveBottom() {
		int[] selectedRows = table.getSelectedRows();
		int selectedRowCount = selectedRows.length;
		if (selectedRowCount > 0) {
			int lastSelectedRow = selectedRows[selectedRowCount - 1];
			TableRow row1 = model.getRow(table, lastSelectedRow);
			int lastRow = lastSelectedRow + 1;
			for (; lastRow < table.getRowCount(); lastRow++) {
				TableRow row2 = model.getRow(table, lastRow);

				if (row2.getData().getDate().compareTo(row1.getData().getDate()) > 0) {
					break;
				}
			}

			for (int i = 0; i < selectedRowCount; i++) {
				TableRow row = model.getRow(table, selectedRows[selectedRowCount - i - 1]);
				model.insertRow(lastRow - i, row);
				model.removeRow(selectedRows[selectedRowCount - i - 1]);
			}

			table.clearSelection();
			table.addRowSelectionInterval(lastRow - 1, lastRow - selectedRowCount);
			scrollToRow(lastRow - 1);
			changeListener.stateChanged(null);
		}
	}

	public ChangeListener getChangeListener() {
		return changeListener;
	}

	public void setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public void paintSearchResult(Graphics g) {
		if (!searchResult.isEmpty()) {
			String text = (searchResultIndex + 1) + " von " + searchResult.size();
			int charsWidth = filterField.getFontMetrics(filterField.getFont()).charsWidth(text.toCharArray(), 0, text.length());
			g.drawChars(text.toCharArray(), 0, text.length(), filterField.getSize().width - charsWidth - 10, 20);
		}
	}



}
