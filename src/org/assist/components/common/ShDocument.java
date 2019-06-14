package org.assist.components.common;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;


public class ShDocument extends RegExpDocument {

	public ShDocument(IndicatorTF indicatorField, char[] tokens, String additionRegExp) {
		super(indicatorField, tokens, additionRegExp);
	}

	public ShDocument(IndicatorTF indicatorField, char[] tokens) {
		super(indicatorField, tokens);
	}

	public ShDocument(IndicatorTF indicatorField, int maxChars) {
		super(indicatorField, maxChars);
	}

	public ShDocument(IndicatorTF indicatorField, String regExp, boolean allChars) {
		super(indicatorField, regExp, allChars);
	}

	public ShDocument(IndicatorTF indicatorTF, String regExp) {
		super(indicatorTF, regExp);
	}



	@Override
	public void insertString(int offset, String text, AttributeSet attributeSet) throws BadLocationException {
		super.insertString(offset, text, attributeSet);
	}

}
