package org.assist.components.common;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class TextArea extends JTextArea implements IUndoable{
	private UndoManager undoManager = new UndoManager();

	public TextArea() {
		this(null);
	}

	public TextArea(boolean editable) {
		this(null, editable);
	}

	public TextArea(String text) {
		this(text, false);
	}

	public TextArea(String text, boolean editable) {
		super(text);
		setEditable(editable);
		init();
	}

	public void clear() {
		setText(null);
	}

	private void init() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setBorder(new EmptyBorder(3, 3, 2, 3));
		setSelectedTextColor(Color.BLACK);
		setBackground(Color.WHITE);
		getDocument().addUndoableEditListener(undoManager);

		InputMap inputMap = getInputMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), "undo");
		getActionMap().put("undo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), "redo");
		getActionMap().put("redo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		});

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
	public void setDocument(Document doc) {
		super.setDocument(doc);
		doc.addUndoableEditListener(undoManager);
	}

	@Override
	public void redo() {
		try {
			undoManager.redo();
		}
		catch (CannotRedoException e) {
		}
	}

	@Override
	public void undo() {
		try {
			undoManager.undo();
		}
		catch (CannotUndoException e) {
		}
	}

}
