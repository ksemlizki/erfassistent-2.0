package org.assist.components.table;

import java.util.Vector;

import org.assist.domain.Data;

public class TableRow extends Vector<Object> {

	private Data data;

	public TableRow(Data userObject) {
		this.data = userObject;

		add(data.getAmount());
		add(data.isDebit());
		add(data.getContraAccount());
		add(data.getTaxKey());
		add(data.getDocumentNr());
		add(data.getDate());
		add(data.getAccount());
		add(data.getText());
		if(data.getBalance() != null) {
			add(data.getBalance());
		}
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

}
