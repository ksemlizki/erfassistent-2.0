package org.assist.components.entry;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import org.assist.components.common.IndicatorTF;
import org.assist.tools.Tools;

public class AmountField extends IndicatorTF {

	public static final String INPUT_REGEX = "(\\-?\\d{1,3},\\d{0,2})|(\\-?\\d{1,3}(\\.\\d{3})*,\\d{0,2})|(\\-?\\d{1,3}(\\.\\d{3})*)|\\-?\\d{1,3}";
	public static final String DOCUMENT_REGEX = "(\\-)|(\\-?\\d{1,3},\\d{0,2})|(\\-?\\d{1,3}(\\.\\d{3})*,\\d{0,2})|(\\-?\\d{1,3}(\\.\\d{3})*)|\\-?\\d{1,3}|(\\-?\\d{1,3}(\\.\\d{3})*)|\\-?\\d{1,3}";

	public AmountField(boolean allowMinus) {
		super(INPUT_REGEX, false, DOCUMENT_REGEX);
		init(allowMinus);
	}

	private void init(boolean allowMinus) {

		setDocument(new AmountFieldDocument(this, allowMinus, DOCUMENT_REGEX));
		setErrorIndicatorRegExp(INPUT_REGEX, false);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_DELETE) {
					((AmountFieldDocument)getDocument()).isDelete = true;
				}
			}
		});
	}

	@Override
	public void recheck() {
		super.recheck();
	}

	public Double getDoubleValue() {
		try {
			return new Double(Tools.DECIMAL_FORMAT.parse(getText()).doubleValue());
		}
		catch (ParseException exception) {
		}

		return Double.valueOf(0);
	}

}
