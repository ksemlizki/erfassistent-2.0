package org.assist.components.table;

import javax.swing.AbstractAction;
import javax.swing.JTable;

public abstract class TableAction extends AbstractAction {
	protected JTable table;

	public TableAction(JTable table) {
		super();
		this.table = table;
	}

}
