package org.assist.tools;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentAdapter implements DocumentListener {

	public void update() {
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		update();
	}

	@Override
	public void insertUpdate(DocumentEvent event) {
		update();
	}

	@Override
	public void removeUpdate(DocumentEvent event) {
		update();
	}

}
