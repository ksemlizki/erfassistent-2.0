package org.assist.components.common;

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.assist.constants.GeneralConstants;
import org.assist.tools.IReleaseable;

public class RegExpDocument extends PlainDocument implements IReleaseable {

	private String regExp;
	private boolean allChars;
	private boolean valid;
	protected IndicatorTF indicatorField;

	public RegExpDocument(IndicatorTF indicatorField, char[] tokens) {
		this(indicatorField, tokens, "");
	}

	public RegExpDocument(IndicatorTF indicatorField, char[] tokens, String additionRegExp) {
		this.indicatorField = indicatorField;
		regExp = createRegExp(tokens, additionRegExp);
		setValid("");
	}

	public RegExpDocument(IndicatorTF indicatorField, int maxChars) {
		this(indicatorField, ".{0," + maxChars + "}", true);
	}

	public RegExpDocument(IndicatorTF indicatorTF, String regExp) {
		this(indicatorTF, regExp, true);
	}

	public RegExpDocument(IndicatorTF indicatorField, String regExp, boolean allChars) {
		this.indicatorField = indicatorField;
		this.regExp = regExp;
		this.allChars = allChars;
		setValid("");
	}

	private String createRegExp(char[] tokens, String additionRegExp) {
		StringBuilder lRegExp = new StringBuilder("(");

		for (int i = 0; i < tokens.length; i++) {
			if (i > 0) {
				lRegExp.append("|");
			}
			lRegExp.append("\\");
			lRegExp.append(tokens[i]);
		}

		lRegExp.append("|");
		lRegExp.append(additionRegExp);
		lRegExp.append(")*");

		return lRegExp.toString();
	}

	public boolean isValid() {
		return valid;
	}

	private void setValid(CharSequence input) {
		valid = checkValid(input) ;
	}

	protected boolean checkValid(CharSequence input) {
		return Pattern.compile(regExp, Pattern.CASE_INSENSITIVE).matcher(input).matches();
    }

	public void setRegExp(String regExp) {
		this.regExp = regExp;
		setValid("");
	}

	@Override
	public void release() {
		indicatorField = null;
	}

	@Override
	public void insertString(int offset, String text, AttributeSet attributeSet) throws BadLocationException {
		String input;

		if (allChars) {
			StringBuilder builder = new StringBuilder(getText(0, getLength()));
			builder.insert(offset, text);
			input = builder.toString();
		}
		else {
			input = text;
		}

		setValid(input);
		if (valid) {
			super.insertString(offset, text, attributeSet);
		}
		else {
			if ((indicatorField != null) && indicatorField.isValueValid()) {
				indicatorField.setStateBGColor(GeneralConstants.INDICATOR_TF_FAILS_VALUE_BG_COLOR);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException exception) {
							exception.printStackTrace();
						}
						indicatorField.setStateBGColor(GeneralConstants.BG_COLOR);
					}
				}).start();
			}
		}
	}

}
