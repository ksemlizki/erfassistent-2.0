package org.assist.components.common;

public class AutoCompleteComboBox<T> extends ComboBox<T> /*implements KeyListener*/ {



	public AutoCompleteComboBox() {
		new AutoCompleteCBDocument<T>(this);
	}

//	private JTextField searchField;
//	private boolean doSearch = true;
//
//	public AutoCompleteComboBox() {
//		setEditable(true);
//		searchField = (JTextField) getEditor().getEditorComponent();
//		searchField.setDocument(new SearchFieldDocument());
//		searchField.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(final KeyEvent event) {
//				if (!isPopupVisible()) {
//					setPopupVisible(true);
//				}
//			}
//		});
//	}
//
//	@Override
//	public void keyReleased(final KeyEvent event) {
//
//	}
//
//	@Override
//	public void keyPressed(final KeyEvent event) {
//		if (!isPopupVisible()) {
//			setPopupVisible(true);
//		}
//	}
//
//	@Override
//	public void keyTyped(final KeyEvent event) {
//		char keyChar = event.getKeyChar();
//		int keyCode = event.getKeyCode();
//		String search = null;
//		String selectedText = searchField.getSelectedText();
//		if (keyCode == KeyEvent.VK_ENTER) {
//			ComboBoxModel<T> cbm = getModel();
//			if (!searchField.getText().equalsIgnoreCase("")) {
//				search = searchField.getText();
//				int index = findString(search);
//				if (index != -1) {
//					searchField.setText(cbm.getElementAt(index).toString());
//				}
//			}
//		}
//		if ((keyCode == KeyEvent.VK_BACK_SPACE) || (keyCode == KeyEvent.VK_DELETE)) {
//			if ((searchField.getText().length() > 0) || !searchField.getText().equalsIgnoreCase("")) {
//				search = searchField.getText();
//				findAndSelelect(search, event);
//			}
//			else {
//				resetInput();
//				event.consume();
//				return;
//			}
//		}
//		else {
//			if (keyCode != KeyEvent.VK_ENTER) {
//				search = searchField.getText();
//				search += keyChar;
//				findAndSelelect(search, event);
//			}
//		}
//	}
//
//	@Override
//	public void setSelectedIndex(int index) {
//		super.setSelectedIndex(index);
//	}
//
//	private void findAndSelelect(String search, KeyEvent event) {
//		int index = findString(search);
//		setSelectedIndex(index);
//		searchField.setText(search);
//		searchField.setSelectionEnd(search.length());
//		searchField.setSelectionStart(search.charAt(0));
//		event.consume();
//	}
//
//	public void findAndSelelect(String search) {
//		int index = findString(search);
//		setSelectedIndex(index);
//		if (search != null) {
//			searchField.setSelectionEnd(search.length());
//			searchField.setSelectionStart(search.charAt(0));
//		}
//	}
//
//	private int findString(String search) {
//		ComboBoxModel<T> model = getModel();
//		for (int i = 0; i < model.getSize(); i++) {
//			String source = model.getElementAt(i).toString();
//			if (StringUtils.containsIgnoreCase(source, search)) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
//		private void resetInput() {
//			searchField.setText("");
//			searchField.setCaretPosition(0);
//			setSelectedIndex(-1);
//		}
//
//	class SearchFieldDocument extends PlainDocument {
//
//		@Override
//		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
//			super.insertString(offs, str, a);
//
//			if (doSearch) {
//				doSearch = false;
//				String search = searchField.getText();
//				if (search.length() > 0) {
//					findAndSelelect(search);
//				}
//				doSearch = true;
//			}
//		}
//
//	}
}
