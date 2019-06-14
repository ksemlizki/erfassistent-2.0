package org.assist.components.tabpane;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTabbedPane;

public class CloseTabbedPane extends JTabbedPane {

	private ArrayList<CloseListener> closeListeners;

	public CloseTabbedPane() {
		super();
		closeListeners = new ArrayList<CloseListener>();
	}

	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		TabHeader tabHeader = new TabHeader(title);
		tabHeader.addCloseListeners(closeListeners);

		setTabComponentAt(getTabCount() - 1, tabHeader);
	}
	@Override
	public void setTitleAt(int index, String title) {
		((TabHeader)getTabComponentAt(index)).setTitle(title);
	}

	public void addCloseListener(CloseListener closeListener) {
		closeListeners.add(closeListener);
	}
}
