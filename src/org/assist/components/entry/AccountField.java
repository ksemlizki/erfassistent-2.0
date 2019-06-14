package org.assist.components.entry;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.assist.components.common.IndicatorTF;

public class AccountField extends IndicatorTF {

	public AccountField() {
		super();
	}

	public AccountField(String inputRegExp, boolean canEmpty, String documentRegExp) {
		super(inputRegExp, canEmpty, documentRegExp);
	}

	public AccountField(String text, boolean editable) {
		super(text, editable);
	}

	public AccountField(String regExp) {
		super(regExp);
	}

	@Override
	public void replaceSelection(String content) {
		Document document = getDocument();
		if (document != null) {
			int insertPosition = getCaretPosition();
			int overwriteLength = document.getLength() - insertPosition;
			int length = content.length();

			if (overwriteLength > length) {
				overwriteLength = length;
			}

			// Remove the range being overwritten
			try {
				document.remove(insertPosition, overwriteLength);
			}
			catch (BadLocationException e) {
				// Won't happen
			}
		}

		super.replaceSelection(content);
	}
}
