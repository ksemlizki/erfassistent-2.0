package org.assist.components.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.assist.components.calculator.Calculator;
import org.assist.components.calculator.IAcceptListener;
import org.assist.components.common.ComboBox;
import org.assist.components.common.DateField;
import org.assist.components.common.IndicatorTF;
import org.assist.components.common.TextArea;
import org.assist.components.entry.AccountField;
import org.assist.components.entry.AmountField;
import org.assist.components.entry.TaxKeyListCellRenderer;
import org.assist.constants.GeneralConstants;
import org.assist.constants.ImageConstants;
import org.assist.domain.TaxKey;
import org.assist.tools.CurrencyDownload;
import org.assist.tools.IContentValidator;
import org.assist.tools.Tools;
import org.assist.tools.TraversalPolicy;

import info.clearthought.layout.TableLayout;

public class InputPanel extends JPanel implements ChangeListener {

	protected AmountField amountField;
	protected IndicatorTF shField;
	protected AccountField contraAccountField;
	protected IndicatorTF balanceContraAccountTF;
	protected ComboBox<TaxKey> taxKeyCB;
	protected ComboBox<String> currencyCB;
	private IndicatorTF docNumberField;
	protected DateField dateField;
	protected AccountField accountField;
	protected IndicatorTF balanceAccountTF;
	private TextArea textArea;
	private JButton acceptButton;
	protected JButton calculatorBtn;
	private JLabel warningLabel;
	private JScrollPane pane;

	public InputPanel() {
		double[][] layoutSizes = { { 100, 30, 70, 70, 90, 60, 90, 90, 90, 295, 10, 140 }, { 30, 30, 30, 30 } };

		TableLayout layout = new TableLayout(layoutSizes);
		layout.setHGap(1);
		setLayout(layout);

		warningLabel = new JLabel();
		warningLabel.setForeground(Color.RED);
		warningLabel.setFont(GeneralConstants.FORM_FONT);


		acceptButton = new JButton("Übernehmen");
		amountField = new AmountField(true);

		ImageIcon calculator = ImageConstants.CALCULATOR;
		calculatorBtn = new JButton(calculator);
		calculatorBtn.setFocusable(false);
		shField = new IndicatorTF("[sh]{1}", false, "[sh]{1}");
		contraAccountField = new AccountField("\\d{0,5}", true, "\\d{0,5}");

		balanceContraAccountTF = new IndicatorTF("", false);
		balanceContraAccountTF.setEnabled(false);
		currencyCB = new ComboBox<>(CurrencyDownload.getCurrencyTokens().toArray(new String[0]));

		taxKeyCB = new ComboBox<TaxKey>(GeneralConstants.TAX_KEYS.values().toArray(new TaxKey[0]));
		taxKeyCB.setRenderer(new TaxKeyListCellRenderer());

		docNumberField = new IndicatorTF(".{0,255}", true, null);

		dateField = new DateField();

		accountField = new AccountField("\\d{0,5}", true, "\\d{0,5}");

		balanceAccountTF = new IndicatorTF("", false);
		balanceAccountTF.setEnabled(false);

		textArea = new TextArea(true);
		pane = new JScrollPane(textArea);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


		add(warningLabel, "0, 0, 8,0");

		add(amountField, "0,2");
		add(Tools.createLabel("Betrag"), "0,1");
		add(calculatorBtn, "1,2");
		add(Tools.createLabel("Währung"), "2,1");
		add(currencyCB , "2,2");


		add(Tools.createLabel("S/H"), "3,1");
		add(shField , "3,2");
		add(Tools.createLabel("Gegenkonto"), "4,1");
		add(contraAccountField, "4,2");
		add(balanceContraAccountTF, "4,3");
		add(Tools.createLabel("St.Schl."), "5,1");
		add(taxKeyCB, "5,2");
		add(Tools.createLabel("BelegNr"), "6,1");
		add(docNumberField, "6,2");
		add(Tools.createLabel("Datum"), "7,1");
		add(dateField, "7,2");
		add(Tools.createLabel("Konto"), "8,1");
		add(accountField, "8,2");
		add(balanceAccountTF, "8,3");
		add(Tools.createLabel("Buchungstext"), "9,1");
		add(pane, "9,2, 9, 3");
		add(acceptButton, "11,2");

		initListeners();
		initFocus();
		changeBtnEnabledState();
	}

	public JScrollPane getScrollPane() {
		return pane;
	}

	private void initListeners() {
		amountField.addChangeListener(this);
		amountField.addContentValidator(new IContentValidator() {
			@Override
			public String validate() {
				if (Tools.isStringEmpty(amountField.getText())) {
					return "Kein Betrag eingegeben";
				}
				return null;
			}
		});

		shField.addContentValidator(new IContentValidator() {
			@Override
			public String validate() {
				String text = shField.getText();
				if (Tools.isStringEmpty(text) || (!text.equalsIgnoreCase("h") && !text.equalsIgnoreCase("s"))) {
					return "Soll oder Haben muss eingegeben werden";
				}

				return null;
			}
		});

		amountField.addChangeListener(this);
		shField.addChangeListener(this);
		contraAccountField.addChangeListener(this);
		accountField.addChangeListener(this);
		AutoTaxKeyListener taxKeyListener = new AutoTaxKeyListener();
		contraAccountField.addChangeListener(taxKeyListener);
		accountField.addChangeListener(taxKeyListener);
		taxKeyCB.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					changeBtnEnabledState();
				}
			}
		});
		dateField.addChangeListener(this);
		AbstractAction calculatorAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				showCalculator();
			}
		};
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, false), "calculator");
		getActionMap().put("calculator", calculatorAction);
		calculatorBtn.setAction(calculatorAction);
		calculatorBtn.setIcon(ImageConstants.CALCULATOR);

		dateField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				dateField.setCaretPosition(0);
			}
		});

		acceptButton.registerKeyboardAction(acceptButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
		acceptButton.registerKeyboardAction(acceptButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);

		amountField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.getKeyCode() == KeyEvent.VK_MULTIPLY) && (event.getModifiers() == 0)) {
	                dateField.requestFocus();
	                dateField.setCaretPosition(0);
	                event.consume();
                }
			}
		});

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		changeBtnEnabledState();
	}

	protected void changeBtnEnabledState() {
		String validateWarning = getValidateWarning();

		boolean enabled = Tools.isStringEmpty(validateWarning);
		acceptButton.setEnabled(enabled);
		acceptButton.setFocusable(enabled);
		warningLabel.setText(validateWarning);
	}

	private String getValidateWarning() {
		amountField.validateText();
		if (!amountField.isValueValid()) {
			return amountField.getValidateWarning();
		}

		shField.validateText();
		if (!shField.isValueValid()) {
			return shField.getValidateWarning();
		}

		dateField.validateText();
		if (!dateField.isValueValid()) {
			return dateField.getValidateWarning();
		}


		String contraAccount = contraAccountField.getText();
		String account = accountField.getText();
		if (account.equals(contraAccount)) {
			return "Konto gleich Gegenkonto";
		}

		if ((account.length() == 4) && (contraAccount.length() == 4)) {
			if ((account.startsWith("8") && contraAccount.startsWith("8")) || (account.startsWith("3") && contraAccount.startsWith("3"))) {
				return "Unzulässige Buchung";
			}
		}
		Object taxKeyObject = taxKeyCB.getSelectedItem();

		int taxKey = ((TaxKey)taxKeyObject).getId().intValue();
		if (((account.length() == 4) && account.startsWith("136")) || ((contraAccount.length() == 4) && contraAccount.startsWith("136"))) {
			if (taxKey != 0) {
				return "Ungültiger Steuerschlüssel";
			}
		}

		if ((account.length() == 4) && (contraAccount.length() == 4)) {
			if (account.startsWith("18") || account.startsWith("15") || account.startsWith("17") || account.startsWith("19")) {
				if (contraAccount.startsWith("10") || contraAccount.startsWith("12") || contraAccount.startsWith("13")) {
					if (taxKey != 0) {
						return "Ungültiger Steuerschlüssel";
					}
				}
			}

			if (contraAccount.startsWith("18") || contraAccount.startsWith("15") || contraAccount.startsWith("17") || contraAccount.startsWith("19")) {
				if (account.startsWith("10") || account.startsWith("12") || account.startsWith("13")) {
					if (taxKey != 0) {
						return "Ungültiger Steuerschlüssel";
					}
				}
			}
		}

		if ((account.length() == 4) && (contraAccount.length() == 5)) {
			if (account.startsWith("10") || account.startsWith("12") || account.startsWith("13")) {
				if (taxKey != 0) {
					return "Ungültiger Steuerschlüssel";
				}
			}
		}
		if ((account.length() == 5) && (contraAccount.length() == 4)) {
			if (contraAccount.startsWith("10") || contraAccount.startsWith("12") || contraAccount.startsWith("13")) {
				if (taxKey != 0) {
					return "Ungültiger Steuerschlüssel";
				}
			}
		}

		return null;
	}



	private void initFocus() {
		setFocusCycleRoot(true);
		setFocusTraversalPolicy(new TraversalPolicy(new Vector<Component>(Arrays.asList(amountField, shField, contraAccountField, taxKeyCB, docNumberField, dateField, accountField, textArea, acceptButton))));

		HashSet<KeyStroke> forwardKeyStrokes = new HashSet<KeyStroke>();
		forwardKeyStrokes.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false));
		forwardKeyStrokes.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK, false));

		HashSet<KeyStroke> backwardKeyStrokes = new HashSet<KeyStroke>();
		backwardKeyStrokes.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK, false));
		backwardKeyStrokes.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, false));

		acceptButton.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeyStrokes);
		acceptButton.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeyStrokes);

		forwardKeyStrokes.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));
		textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeyStrokes);
		textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeyStrokes);

		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeyStrokes);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeyStrokes);


		amountField.addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorRemoved(AncestorEvent event) {
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
				amountField.grabFocus();
			}
		});
	}

	public AmountField getAmountField() {
		return amountField;
	}

	public IndicatorTF getShField() {
		return shField;
	}

	public IndicatorTF getContraAccountField() {
		return contraAccountField;
	}

	public IndicatorTF getBalanceContraAccountTF() {
		return balanceContraAccountTF;
	}

	public ComboBox<TaxKey> getTaxKeyCB() {
		return taxKeyCB;
	}

	public IndicatorTF getDocNumberField() {
		return docNumberField;
	}

	public DateField getDateField() {
		return dateField;
	}

	public IndicatorTF getAccountField() {
		return accountField;
	}

	public IndicatorTF getBalanceAccountTF() {
		return balanceAccountTF;
	}

	public TextArea getTextArea() {
		return textArea;
	}

	public JButton getAcceptButton() {
		return acceptButton;
	}



	public ComboBox<String> getCurrencyCB() {
		return currencyCB;
	}


	protected void showCalculator() {
		Double startValue = null;
		try {
			startValue = new Double(Tools.DECIMAL_FORMAT.parse(amountField.getText()).doubleValue());
		}
		catch (ParseException e1) {
		}

		Calculator calculator = new Calculator(MainFrame.getInstance(), startValue);
		calculator.setAcceptListener(new IAcceptListener() {

			@Override
			public void accept(double value) {
				if (value != Double.NaN) {
					amountField.setText(Tools.NUMBER_FORMAT.format(value));
				}
			}
		});

		calculator.setVisible(true);
	}

	class AutoTaxKeyListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			taxKeyCB.setEnabled(true);
			String contraAccount = contraAccountField.getText();
			String account = accountField.getText();

			TaxKey autoTaxKey = Tools.getAutoTaxKey(contraAccount, account);
			if (autoTaxKey != null) {
				taxKeyCB.setSelectedItem(autoTaxKey);
			}

			if (account.equals("8924") || contraAccount.equals("8924")) {
				taxKeyCB.setEnabled(false);
			}
			else if (account.equals("8905") || contraAccount.equals("8905")) {
				taxKeyCB.setEnabled(false);
			}
		}
	}

}
