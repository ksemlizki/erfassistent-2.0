package org.assist.components.common;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.assist.tools.IContentValidator;
import org.assist.tools.Tools;

public class DateField extends IndicatorTF {

	private Robot robot;
	private GregorianCalendar value;

	public DateField() {
		super("\\d{2}\\.\\d{2}\\.\\d{4}", false, "\\d{0,2}\\.?\\d{0,2}\\.?\\d{0,4}");

		addContentValidator(new IContentValidator() {

			@Override
			public String validate() {
				return processValidate();
			}
		});
		try {
			MaskFormatter dateFormatter = new MaskFormatter("##.##.20##");
			dateFormatter.setPlaceholderCharacter('0');
			setFormatterFactory(new DefaultFormatterFactory(dateFormatter));

		}
		catch (ParseException exception) {
			exception.printStackTrace();
		}

		internalSetValue();
		setCaretPosition(0);
		initListeners();
	}

	protected String processValidate() {
		String text = getText();
		GregorianCalendar newValue = Tools.parseDate(text);
		if (newValue != null) {
			String[] dateParts = text.split("\\.");
			if (Integer.parseInt(dateParts[0]) != newValue.get(Calendar.DAY_OF_MONTH)) {
				return "Tag beim eingegebenen Datum ist ungültig!";
			}
			else if (Integer.parseInt(dateParts[1]) != (newValue.get(Calendar.MONTH) + 1)) {
				return "Monat beim eingegebenen Datum ist ungültig!";
			}
			else if (Integer.parseInt(dateParts[2]) != newValue.get(Calendar.YEAR)) {
				return "Jahr beim eingegebenen Datum ist ungültig!";
			}
		}

		return null;
	}

	private void initListeners() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				handleKeyPressed(event.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent event) {
				handleKeyReleased();
			}
		});

		addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent event) {

			}
		});
	}

	protected void handleKeyPressed(int keyCode) {
		try {
			if (keyCode == KeyEvent.VK_DECIMAL) {
				if (robot == null) {
					robot = new Robot();
				}
				robot.keyPress(KeyEvent.VK_PERIOD);
			}
		}
		catch (AWTException exception) {
			exception.printStackTrace();
		}
	}

	protected void handleKeyReleased() {
		if (isValueValid()) {
			GregorianCalendar newValue = Tools.parseDate(getText());
			if ((value == null) || !value.equals(newValue)) {
				value = newValue;
				internalSetValue();
			}
		}
	}

	@Override
	public GregorianCalendar getValue() {
		return value;
	}

	public void setValue(GregorianCalendar value) {
		this.value = value;
		internalSetValue();

	}

	private void internalSetValue() {
		String defaultValue = "00.00." + new GregorianCalendar().get(Calendar.YEAR);
		int caretPosition = getCaretPosition();
		setText(value == null ? defaultValue : Tools.format(value));
		setCaretPosition(caretPosition);
	}
}
