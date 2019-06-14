package org.assist.components.calculator;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.assist.components.common.IndicatorTF;
import org.assist.components.common.TextArea;
import org.assist.components.entry.AmountField;
import org.assist.components.entry.AmountFieldDocument;
import org.assist.constants.GeneralConstants;
import org.assist.constants.ImageConstants;
import org.assist.tools.Tools;

public class Calculator extends JDialog {

	private final Double startValue;
	private JButton buttons[][];
	private JButton clearButton;
	private JButton clearFieldButton;
	private JToggleButton historyButton;

	protected IAcceptListener acceptListener;
	protected IndicatorTF amountField;
	protected IndicatorTF resultTF;
	protected ArrayList<Operation> operations;
	protected JTextArea historyArea;
	protected JPanel historyPanel;
	protected JPanel mainPanel;

	private JButton acceptButton;


	public Calculator(Window parent, Double startValue) {
		super(parent, "Rechner");
		this.startValue = startValue;
		init();
		initListeners();
		pack();
		setMinimumSize(getPreferredSize());
		setLocationRelativeTo(parent);
		setResizable(false);
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		operations = new ArrayList<Operation>();
		setModal(true);

		int columCount = 5;
		int rowCount = 10;
		TableLayout layout = createLayout(columCount, rowCount);
		mainPanel = new JPanel(layout);
		amountField = new AmountField(false);
		amountField.setDocument(new CalculatorFieldDocument(amountField));
		mainPanel.add(amountField, "0,0,3,0");
		resultTF = new IndicatorTF();
		resultTF.setEditable(false);
		resultTF.setFocusable(false);
		resultTF.setEnabled(false);

		mainPanel.add(amountField, "0,0,3,0");
		mainPanel.add(resultTF, "0,1,3,1");

		historyButton = new JToggleButton(ImageConstants.PAGE);
		historyButton.setFocusable(false);
		historyButton.setToolTipText("Verlauf");
		mainPanel.add(historyButton, "4,0");
		clearFieldButton = createButton("CE");
		mainPanel.add(clearFieldButton, "3,3");
		clearButton = createButton("C");
		clearButton.setForeground(Color.RED);
		mainPanel.add(clearButton, "4,3");
		createDigitsPanel(columCount, rowCount);
		JPanel panel = new JPanel();
		acceptButton = createButton("Übernehmen", true);
		panel.add(acceptButton);
		historyPanel = new JPanel(new BorderLayout());
		historyPanel.setBorder(BorderFactory.createEtchedBorder());
		historyPanel.setPreferredSize(new Dimension(0, 0));
		mainPanel.add(panel, "0, 9, 4,9");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(columCount, columCount, columCount, columCount));
		add(mainPanel);
		add(historyPanel, BorderLayout.EAST);
		historyArea = new TextArea(false);
		historyPanel.add(new JScrollPane(historyArea));

		if (startValue != null) {
			amountField.setText(Tools.DECIMAL_FORMAT.format(startValue.doubleValue()));
			amountField.setCaretPosition(amountField.getText().length());
		}
	}

	private TableLayout createLayout(int columCount, int rowCount) {
		double layoutColumns[] = new double[columCount];
		double layoutRows[] = new double[rowCount];
		Arrays.fill(layoutColumns, 40);
		Arrays.fill(layoutRows, 30);
		layoutRows[2] = columCount;
		layoutRows[4] = columCount;
		layoutRows[layoutRows.length - 1] = TableLayoutConstants.PREFERRED;
		TableLayout layout = new TableLayout(new double[][] { layoutColumns, layoutRows });
		return layout;
	}

	private void initListeners() {
		acceptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accept();
			}
		});

		amountField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					if (event.getModifiers() == 0) {
						recalc();
						amountField.setText(null);
					}
					else {
						accept();
					}
				}
			}
		});

		clearFieldButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				amountField.setText("");
			}
		});

		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				operations = new ArrayList<Operation>();
				historyArea.setText("");
				amountField.setText("");
				resultTF.setText(null);
			}
		});

		historyButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean show = e.getStateChange() == ItemEvent.SELECTED;
				historyPanel.setPreferredSize(new Dimension(show ? 150 : 0, 0));
				mainPanel.setBorder(new EmptyBorder(0, 0, 0, show ? 5 : 0));
				Calculator.this.pack();
			}
		});

		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	private void createDigitsPanel(int columnCount, int rowCount) {
		String buttonTexts[][][] = { { { "7", "7" }, { "8", "8" }, { "9", "9" }, { "/", "divide" }, { "", "sqrt" } }, { { "4", "4" }, { "5", "5" }, { "6", "6" }, { "*", "mul" }, { "%", "prozent" } },
				{ { "1", "1" }, { "2", "2" }, { "3", "3" }, { "-", "minus" }, { "", "1/x" } }, { { "0", "0" }, { "", "+-" }, { ",", "decimal" }, { "+", "plus" }, { "=", "result" } } };

		buttons = new JButton[4][5];
		CalcActionListener actionListener = new CalcActionListener();
		for (int i = 0; i < columnCount; i++) {
			for (int j = 0; j < (rowCount - 6); j++) {
				mainPanel.add(buttons[j][i] = createButton(buttonTexts[j][i][0]), i + "," + (j + 5));
				buttons[j][i].setActionCommand(buttonTexts[j][i][1]);
				buttons[j][i].addActionListener(actionListener);
			}
		}
		buttons[0][4].setIcon(ImageConstants.SQRT);
		buttons[2][4].setIcon(ImageConstants.X1);
		buttons[3][1].setIcon(ImageConstants.PLUSMINUS);
	}

	private JButton createButton(String text) {
		return createButton(text, false);
	}

	private JButton createButton(String text, boolean setMargin) {
		JButton button = new JButton(text);
		button.setFont(GeneralConstants.FORM_FONT);
		button.setFocusable(false);
		button.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				amountField.requestFocus();
			}
		});

		if (!setMargin) {
			UIDefaults buttonDefaults = new UIDefaults();
			buttonDefaults.put("Button.contentMargins", new Insets(0, 0, 0, 0));
			button.putClientProperty("Nimbus.Overrides", buttonDefaults);
			button.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
		}

		return button;
	}

	public Double getValue() {
		try {
			return new Double(Tools.DECIMAL_FORMAT.parse(amountField.getText()).doubleValue());
		}
		catch (ParseException exception) {
		}

		return null;
	}

	public double recalc() {
		addLastValue();

		double value = setResult();

		return value;
	}

	private double setResult() {
		double value = 0;
		if (!operations.isEmpty()) {
			value = operations.get(0).getValue().doubleValue();
			for (int i = 1; i < operations.size(); i++) {
				Operation operation = operations.get(i);
				Operator operator = operation.getOperator();
				if ((operator.isUnary())) {
					value = operation.execute(value, 0);
				}
				else {
					i++;
					value = operation.execute(value, operations.get(i).getValue().doubleValue());
				}

			}
			resultTF.setText(Tools.DECIMAL_FORMAT.format(value));
		}
		return value;
	}

	protected void addValue(Operator operator) {
		Operation newOperation = new Operation(operator);
		if (!Tools.isStringEmpty(amountField.getText())) {
			addLastValue();
			setResult();
		}

		if (!operations.isEmpty()) {
			Operation lastOperation = operations.get(operations.size() - 1);
			switch (lastOperation.getType()) {
				case DOUBLE:
					addOperation(newOperation);
					break;
				case OPERATOR:
					Operator op = lastOperation.getOperator();
					if (op.isUnary()) {
						addOperation(newOperation);
					}
					else {
						operations.set(operations.size() - 1, newOperation);
						resetHistory();
					}

					break;
			}
		}
		amountField.setText(null);

	}

	private void resetHistory() {
		historyArea.setText(null);
		for (Operation operation : operations) {
			historyArea.append(operation.toString() + "\n");
		}
	}

	private void addLastValue() {
		Double value = getValue();
		if (value != null) {
			addOperation(new Operation(value));
		}
	}

	protected void addOperation(Operation operation) {
		operations.add(operation);
		historyArea.append(operation.toString() + "\n");
	}

	public void setAcceptListener(IAcceptListener acceptListener) {
		this.acceptListener = acceptListener;
	}

	protected void accept() {
		if (acceptListener != null) {
			acceptListener.accept(operations.isEmpty() ? Double.NaN : recalc());
			dispose();
		}
	}

	class CalcActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				String command = event.getActionCommand();
				int caretPosition = amountField.getCaretPosition();
				String text = amountField.getText();
				if ((command.length() == 1) &&  Character.isDigit(command.charAt(0))) {
					amountField.getDocument().insertString(caretPosition, command, null);
				}
				else if (command.equals("decimal")) {
					amountField.getDocument().insertString(caretPosition, ",", null);
				}
				else if (command.equals("+-")) {
					amountField.setText(text.startsWith("-") ? text.substring(1) : "-" + text);
				}
				else if (command.equals("plus")) {
					amountField.getDocument().insertString(caretPosition, "+", null);
				}
				else if (command.equals("minus")) {
					amountField.getDocument().insertString(caretPosition, "-", null);
				}
				else if (command.equals("divide")) {
					amountField.getDocument().insertString(caretPosition, "/", null);
				}
				else if (command.equals("mul")) {
					amountField.getDocument().insertString(caretPosition, "*", null);
				}
				else if (command.equals("prozent")) {
					amountField.getDocument().insertString(caretPosition, "%", null);
				}
				else if (command.equals("sqrt")) {
					addValue(Operator.SQRT);
				}
				else if (command.equals("1/x")) {
					addValue(Operator.X1);
				}
				else if (command.equals("result")) {
					recalc();
					amountField.setText(null);
				}
			}
			catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	class CalculatorFieldDocument extends AmountFieldDocument {

		public CalculatorFieldDocument(IndicatorTF indicatorField) {
			super(indicatorField, false, AmountField.DOCUMENT_REGEX);
		}

		@Override
		public void insertString(int offset, String str, AttributeSet attributeSet) throws BadLocationException {
			if (str.equals("+")) {
				addValue(Operator.PLUS);
			}
			else if (str.equals("-") ) {
				addValue(Operator.MINUS);
			}
			else if (str.equals("*")) {
				addValue(Operator.MUL);
			}
			else if (str.equals("/")) {
				addValue(Operator.DIV);
			}
			else if (str.equals("%")) {
				addValue(Operator.PROZENT);
			}
			else {
				super.insertString(offset, str, attributeSet);
			}
		}


	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			Calculator calculator = new Calculator(null, Double.valueOf(5.78));
			calculator.setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}