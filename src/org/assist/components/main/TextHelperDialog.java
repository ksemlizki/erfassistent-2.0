package org.assist.components.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.assist.tools.DocumentAdapter;

public class TextHelperDialog extends JDialog {

	protected final Color ODD_ROW_COLOR = new Color(0xf2, 0xf2,0xf2);
	protected JTextField searchField;
	protected JList<String> dataList;

	protected Collection<String> data;

	public TextHelperDialog(Collection<String> data, String text) {
		super(MainFrame.getInstance(),false);
		this.data = data;

		init(text);
	}

	private void init(String text) {
		dataList = new JList<String>(new Vector<String>(data));
		dataList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component rendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if ((index %2) == 1) {
					rendererComponent.setBackground(ODD_ROW_COLOR);
				}
				return rendererComponent;
			}
		});
		setLayout(new BorderLayout());
		searchField = new JTextField();
		add(searchField, BorderLayout.NORTH);
		searchField.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void update() {
				super.update();
				filter();
			}
		});
		searchField.setText(text);
		filter();
		add(new JScrollPane(dataList), BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
	}

	protected void filter() {
		ArrayList<String> filteredData = new ArrayList<String>();
		for (String string : data) {
			if (string.contains(searchField.getText())) {
				filteredData.add(string);
			}
		}
		dataList.setListData(new Vector<String>(filteredData));
	}

}
