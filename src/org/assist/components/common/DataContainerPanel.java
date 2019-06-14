package org.assist.components.common;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class DataContainerPanel extends JPanel {

	protected JList<String> dataList;

	protected Collection<String> data;

	protected IDblClickHandler dblClickHandler;

	public DataContainerPanel(Collection<String> data) {
		super(new BorderLayout());
		this.data = data;
		getPreferredSize().width = 295;
		dataList = new JList<String>() {
			@Override
			public String getToolTipText(MouseEvent event) {
				int index = locationToIndex(event.getPoint());
				Object item = getModel().getElementAt(index);
				return item.toString();
			}
		};

		dataList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				if ((event.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(event)) {
					if (dblClickHandler != null) {
						dblClickHandler.handle(dataList.getSelectedValue());
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(dataList);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(scrollPane, BorderLayout.CENTER);
	}

	public int filter(String text) {
		ArrayList<String> filteredData = new ArrayList<String>();
		for (String string : data) {
			if (string.toLowerCase().contains(text.toLowerCase())) {
				filteredData.add(string);
			}
		}
		dataList.setListData(new Vector<String>(filteredData));
		revalidate();

		return filteredData.size();
	}

	public void setDblClickHandler(IDblClickHandler dblClickHandler) {
		this.dblClickHandler = dblClickHandler;
	}

	public void setData(Collection<String> data) {
		this.data = data;
		dataList.setListData(new Vector<String>(data));
	}

	public int getDataSize() {
		return data == null ? 0 : data.size();
	}

	public interface IDblClickHandler {
		void handle(String text);
	}

}
