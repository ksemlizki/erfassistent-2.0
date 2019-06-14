package org.assist.constants;

import javax.swing.ImageIcon;

public class ImageConstants {
	public static final ImageIcon UNDO = createIcon("Undo.png");
	public static final ImageIcon REDO = createIcon("Redo.png");
	public static final ImageIcon COPY = createIcon("Copy.png");
	public static final ImageIcon PASTE = createIcon("Paste.png");
	public static final ImageIcon ERASE = createIcon("Erase.png");
	public static final ImageIcon SELECT_ALL_TEXT = createIcon("SelectAllText.png");
	public static final ImageIcon WARNING = createIcon("Warning.png");
	public static final ImageIcon SAVED = createIcon("Saved.png");
	public static final ImageIcon UNSAVED = createIcon("Unsaved.png");
	public static final ImageIcon CLOSE = createIcon("CloseNoFocus.png");
	public static final ImageIcon CLOSE_FOCUS = createIcon("CloseFocus.png");
	public static final ImageIcon CROSS = createIcon("Cross.png");
	public static final ImageIcon CHECKMARK = createIcon("Checkmark.png");
	public static final ImageIcon EDIT = createIcon("Pencil.png");
	public static final ImageIcon DELETE = createIcon("Minus.png");

	public static final ImageIcon FILTER = createIcon("Filter.png");
	public static final ImageIcon FILTER_DISABLED = createIcon("FilterDisabled.png");
	public static final ImageIcon FRAME = createIcon("Frame.png");

	public static final ImageIcon MOVE_TOP = createIcon("MoveTop.png");
	public static final ImageIcon MOVE_UP = createIcon("MoveUp.png");
	public static final ImageIcon MOVE_DOWN = createIcon("MoveDown.png");
	public static final ImageIcon MOVE_BOTTOM = createIcon("MoveBottom.png");

	public static final ImageIcon CALCULATOR = createIcon("Calculator.png");

	public static final ImageIcon PAGE = createIcon("page.png");
	public static final ImageIcon SQRT = createIcon("sqrt.gif");
	public static final ImageIcon PLUSMINUS = createIcon("PlusMinus.png");
	public static final ImageIcon X1 = createIcon("1x.png");

	public static final ImageIcon CASHBOOK = createIcon("CashBook.png");

	public static final ImageIcon SELECT_ALL = createIcon("SelectAllChB.png");
	public static final ImageIcon DESELECT_ALL = createIcon("DeselectAllChB.png");
	public static final ImageIcon PRINT = createIcon("Print.png");


	private static ImageIcon createIcon(String name) {
		return new ImageIcon(ImageConstants.class.getClassLoader().getResource("images/" + name));
	}



}