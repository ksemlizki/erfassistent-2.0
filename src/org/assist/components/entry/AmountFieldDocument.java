package org.assist.components.entry;

import java.util.ArrayList;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.assist.components.common.IndicatorTF;
import org.assist.components.common.RegExpDocument;

public class AmountFieldDocument extends RegExpDocument {

	private final boolean allowMinus;
	protected boolean isDelete;

	public AmountFieldDocument(IndicatorTF indicatorField, boolean allowMinus, String additionRegExp) {
		super(indicatorField, additionRegExp);
		this.allowMinus = allowMinus;
	}

	@Override
	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if (!text.equals("*")) {
			super.replace(offset, length, text, attrs);
		}
	}



	@Override
	public void remove(int offs, int len) throws BadLocationException {
		if (getText(offs, len).equals(".")) {
			if (isDelete) {
				super.remove(offs, len+1);
				isDelete = false;
			}
			else {
				super.remove(offs-1, len+1);
			}
		}
		else {
			super.remove(offs, len);
		}
		setText(indicatorField.getText());
	}

//	@Override
//    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
//		if ((text != null) && !text.equals("*")) {
//	        super.replace(offset, length, text, attrs);
//        }
//	}

	@Override
	public void insertString(int offset, String string, AttributeSet attributeSet) throws BadLocationException {
		String text = string;
		String oldText = getText(0, getLength());
		if (text.equals("-")) {
			if (allowMinus) {
				if (oldText.startsWith("-")) {
					remove(0, oldText.length());
					super.insertString(0, oldText.substring(1), attributeSet);
				}
				else {
					remove(0, oldText.length());
					super.insertString(0, "-" + oldText, attributeSet);
				}
			}
		}
		else if ((text.length() > 1) || (oldText.length() == 0)) {
			if (text.equals(".") || text.equals(",")) {
				text = "0" + text;
			}
			char[] chars = text.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if (!Character.isDigit(chars[i]) && (chars[i] != '.') && (chars[i] != ',') && (chars[i] != '-')) {
					return;
				}
			}
			super.insertString(0, setDots(removeDots(text)), attributeSet);
		}
		else {
			char firstChar = text.charAt(0);
			if (Character.isDigit(firstChar) || (firstChar == ',')) {
				String newText = getText(0, offset) + text + getText(offset, getLength() - offset);
				setText(newText);
				if (getText(0, getLength()).length() == 0) {
					setText(oldText);
				}
			}
		}
	}

	private void setText(String text) {
		int pos = indicatorField.getText().length() - indicatorField.getCaretPosition();
		indicatorField.setText(setDots(removeDots(text)));
		int len = indicatorField.getText().length();
		int newPos = len - pos;
		indicatorField.setCaretPosition((newPos < 0) ? 0 : newPos);
	}

	protected String removeDots(String text) {
		String newText = "";
		String[] parts = text.split("\\.");
		for (int i = 0; i < parts.length; i++) {
			newText += parts[i];
		}

		return newText;
	}

	private String setDots(String text) {
		String newText = text;
		int decimalPlace = text.indexOf(',');
		String decimal = "";
		if (decimalPlace != -1) {
			decimal = text.substring(decimalPlace + 1);
			newText = text.substring(0, decimalPlace);
		}

		if (newText.length() > 3) {
			ArrayList<String> parts = new ArrayList<String>();
			for (int i = newText.length() - 3; i > 0; i -= 3) {
				parts.add(0, newText.substring(i));
				newText = newText.substring(0, i);
			}
			parts.add(0, newText);
			newText = "";
			for (String string : parts) {
				newText += string + ".";
			}
			newText = newText.substring(0, newText.length() - 1);
		}

		if ((decimalPlace != -1)) {
			newText += ",";
		}
		newText += decimal;
		return newText;
	}
}
