package org.assist.components.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import org.assist.constants.GeneralConstants;
import org.assist.tools.DocumentAdapter;
import org.assist.tools.IContentValidator;
import org.assist.tools.IValueValid;
import org.assist.tools.Tools;

public class IndicatorTF extends TextField implements IValueValid {

	protected boolean valueValid = true;
	protected ArrayList<ChangeListener> changeListeners;
	protected String regExp;
	protected ArrayList<IContentValidator> contentValidators;
	protected String validateWarning;

	private boolean canEmpty;

	public IndicatorTF() {
		this(null, true);
	}

	public IndicatorTF(String text, boolean editable) {
		super(text, editable);
		canEmpty = true;
		initListeners(null, null);
	}

	public IndicatorTF(String regExp) {
		this(regExp, false, null);
	}

	public IndicatorTF(String inputRegExp, boolean canEmpty, String documentRegExp) {
		this.canEmpty = canEmpty;
		initListeners(inputRegExp, (documentRegExp == null) ? inputRegExp : documentRegExp);
	}

	public void initListeners(String inputRegExp, String documentRegExp) {
		setFont(GeneralConstants.FORM_FONT);
		changeListeners = new ArrayList<ChangeListener>();
		contentValidators = new ArrayList<IContentValidator>();

		if (inputRegExp != null) {
			setDocument(new RegExpDocument(this, documentRegExp));
			setErrorIndicatorRegExp(inputRegExp, canEmpty);
		}

		Painter<Object> painter = new Painter<Object>() {
			@Override
			public void paint(Graphics2D graphics2d, Object object, int w, int h) {
				UIDefaults defaults = javax.swing.UIManager.getDefaults();
				if (valueValid) {
					graphics2d.setColor((Color)defaults.get("Panel.background"));
					graphics2d.fillRect(0, 0, w, h );
				}

				graphics2d.setColor(valueValid ? GeneralConstants.BG_COLOR : GeneralConstants.INDICATOR_TF_FAILS_VALUE_BG_COLOR);
				graphics2d.fillRect(3, 4, w - 6, h - 7);
			}
		};

		UIDefaults bknTextDefaults = new UIDefaults();
		bknTextDefaults.put("FormattedTextField[Enabled].backgroundPainter", painter);
		bknTextDefaults.put("FormattedTextField[Disabled].textForeground", Color.black);
		bknTextDefaults.put("FormattedTextField.font", Color.black);
		bknTextDefaults.put("FormattedTextField.contentMargins", new Insets(6, 6, 6, 6));
		putClientProperty("Nimbus.Overrides", bknTextDefaults);
		putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
	}

	@Override
	public void setValueValid(boolean valueValid) {
		this.valueValid = valueValid;
	}

	@Override
	public boolean isValueValid() {
		return valueValid;
	}

	@Override
	public void setStateBGColor(Color bgColor) {
		setBackground(bgColor);
	}

	public void setErrorIndicatorRegExp(String pRegExp, final boolean canEmpty) {
		this.regExp = pRegExp;

		if (Tools.isStringEmpty(getText().trim()) && !canEmpty) {
			setValueValid(false);
		}

		getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void update() {
				recheck();
			}
		});
	}

	public void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public void addContentValidator(IContentValidator contentValidator) {
		contentValidators.add(contentValidator);
	}

	public String getValidateWarning() {
		return validateWarning;
	}

	public void recheck() {
		validateText();
		for (ChangeListener changeListener : changeListeners) {
			changeListener.stateChanged(null);
		}
	}

	public void validateText() {
		String text = getText().trim();
		boolean valid;
		if (Tools.isStringEmpty(text) && canEmpty) {
			valid = true;
		}
		else {
			valid = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE).matcher(text).matches();
		}

		for (IContentValidator contentValidator : contentValidators) {
			validateWarning = contentValidator.validate();
			if (validateWarning != null) {
				valid = false;
				break;
			}
		}

		setValueValid(valid);
	}

	public boolean isCanEmpty() {
		return canEmpty;
	}

	public void setCanEmpty(boolean canEmpty) {
		this.canEmpty = canEmpty;
		if (regExp != null) {
			recheck();
		}
	}

	public ArrayList<IContentValidator> getContentValidators() {
		return contentValidators;
	}

	public ArrayList<ChangeListener> getChangeListeners() {
		return changeListeners;
	}

	@Override
	public void release() {
		changeListeners.clear();
		contentValidators.clear();
		Document document = getDocument();
		if (document instanceof RegExpDocument) {
			((RegExpDocument)document).release();
		}

		super.release();
	}

}
