package org.assist.components.common;

import java.awt.Color;

import javax.swing.InputMap;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.text.PlainDocument;

import org.assist.tools.IReleaseable;

public class TextField extends JFormattedTextField implements IReleaseable/*,IUndoable*/ {

//	private UndoManager undoManager = new UndoManager();

	public TextField() {
		this(null);
	}

	public TextField(boolean editable) {
		this(null, editable);
	}

	public TextField(String text) {
		this(text, true);
	}

	public TextField(String text, boolean editable) {
		super(text);
		init(editable);
	}

	private void init(boolean editable) {
		setEditable(editable);
		setSelectedTextColor(Color.BLACK);
	//	setBackground(Color.WHITE);
		InputMap inputMap = getInputMap();
		inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), new Object());
		setFocusLostBehavior(JFormattedTextField.PERSIST);
//
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), "undo");
//		getActionMap().put("undo", new AbstractAction() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				undo();
//			}
//		});
//
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), "redo");
//		getActionMap().put("redo", new AbstractAction() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				redo();
//			}
//		});

	}

	public void clear() {
		setText(null);
	}

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		setOpaque(editable);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setOpaque(enabled);
	}

	@Override
	public void release() {
		setBorder(null);
		setUI(null);
		setDocument(new PlainDocument());
	}

//	@Override
//	public void setDocument(Document doc) {
//		super.setDocument(doc);
//		doc.addUndoableEditListener(undoManager);
//	}

//	@Override
//	public void redo() {
//		try {
//			undoManager.redo();
//		}
//		catch (CannotRedoException e) {
//		}
//	}
//
//	@Override
//	public void undo() {
//		try {
//			undoManager.undo();
//		}
//		catch (CannotUndoException e) {
//		}
//	}
}