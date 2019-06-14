package org.assist.components.main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.assist.components.common.ComboBox;
import org.assist.components.common.DataContainerPanel;
import org.assist.components.common.DateField;
import org.assist.components.common.IndicatorTF;
import org.assist.components.common.TextArea;
import org.assist.components.common.WaitWindow;
import org.assist.components.entry.AmountField;
import org.assist.components.table.ITableHandler;
import org.assist.components.table.TableAction;
import org.assist.components.table.TablePanel;
import org.assist.components.table.TableRow;
import org.assist.components.tabpane.CloseTabbedPane;
import org.assist.components.tabpane.TabHeader;
import org.assist.constants.GeneralConstants;
import org.assist.constants.ImageConstants;
import org.assist.domain.Data;
import org.assist.domain.TaxKey;
import org.assist.excel.ExcelImporter;
import org.assist.excel.LexwareImporter;
import org.assist.tools.CurrencyDownload;
import org.assist.tools.DocumentAdapter;
import org.assist.tools.ExcelFileFilter;
import org.assist.tools.IDGenerator;
import org.assist.tools.Settings;
import org.assist.tools.TXTFileFilter;
import org.assist.tools.Tools;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class EntryTab extends JPanel {
	protected TablePanel tablePanel;
	private AmountField amountField;
	private IndicatorTF shField;
	private IndicatorTF contraAccountField;
	private ComboBox<TaxKey> taxKeyCB;
	private IndicatorTF docNumberField;
	private DateField dateField;
	private IndicatorTF accountField;
	protected ComboBox<String> currencyCB;
	protected TextArea textArea;
	protected JButton acceptButton;

	protected IndicatorTF balanceContraAccountTF;
	protected IndicatorTF balanceAccountTF;

	private Data currentData;
	protected String fileName;
	protected IDGenerator generator;

	private boolean saved = true;
	private boolean newFile = true;
	protected TreeSet<String> texts;
	private String reportFirmName;
	private String reportAccount;
	private boolean reportAccountActive;

	protected Thread thread;

	protected boolean showTextHelper = true;
	protected InputPanel inputPanel;
	protected JLabel calculatorLabel;
	private JLabel cleanLabel;
	private JLabel acceptLabel;
	private JLabel editLabel;
	private JLabel duplicateLabel;

	public EntryTab() {
		init();
		initListeners();
		initTableActions();
		initTextHelper();
		initPopupMenu();
		initAutosave();

	}

	public void setFile(String fileName) {
		this.fileName = fileName;
	}

	private void init() {
		tablePanel = new TablePanel();
		double layoutSize[][] = {{TableLayoutConstants.FILL},{TableLayoutConstants.FILL,TableLayoutConstants.PREFERRED,TableLayoutConstants.PREFERRED}};
		setLayout(new TableLayout(layoutSize));
		add(tablePanel, "0,0");
		generator = new IDGenerator();


		inputPanel = new InputPanel();
		setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		add(inputPanel, "0,1");
		add(createFButtonPanel(), "0,2");
		amountField = inputPanel.getAmountField();
		shField = inputPanel.getShField();
		shField.setText("H");
		contraAccountField = inputPanel.getContraAccountField();
		balanceContraAccountTF = inputPanel.getBalanceContraAccountTF();
		taxKeyCB = inputPanel.getTaxKeyCB();
		docNumberField = inputPanel.getDocNumberField();
		dateField = inputPanel.getDateField();
		accountField = inputPanel.getAccountField();
		balanceAccountTF = inputPanel.getBalanceAccountTF();
		textArea = inputPanel.getTextArea();
		acceptButton = inputPanel.getAcceptButton();
		currencyCB = inputPanel.getCurrencyCB();
		currencyCB.setSelectedItem(GeneralConstants.DEFAULT_CURRENCY);
	}

	private JPanel createFButtonPanel() {
		JPanel panel  = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
		cleanLabel = new JLabel("<html><b>ESC:</b> Eingabefelder löschen</html>");
		acceptLabel = new JLabel("<html><b>F1:</b> Übernehmen</html>");
		editLabel = new JLabel("<html><b>F5:</b> Zeile editieren</html>");
		duplicateLabel = new JLabel("<html><b>F8:</b> Duplizieren</html>");
		calculatorLabel = new JLabel("<html><b>F4:</b> Taschenrechner</html>");
		panel.add(cleanLabel);
		panel.add(acceptLabel);
		panel.add(editLabel);
		panel.add(duplicateLabel);
		panel.add(calculatorLabel);

		return panel;
	}

	private void initListeners() {
		tablePanel.setSelectionHandler(new ITableHandler() {
			@Override
			public void handle(int row) {
				if (row != -1) {
					recalcBalance(row);
				}
				else {
					balanceContraAccountTF.setText("");
					balanceAccountTF.setText("");
				}
			}
		});

		tablePanel.setDoubleClickHandler(new ITableHandler() {
			@Override
			public void handle(int row) {
				editRow(row);
			}
		});

		tablePanel.setChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setSaved(false);
			}
		});

		acceptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setData();
			}
		});


		inputPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false), "accept");
		inputPanel.getActionMap().put("accept", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (acceptButton.isEnabled()) {
					setData();
				}
			}
		});

		amountField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				calculatorLabel.setVisible(false);
			}

			@Override
			public void focusGained(FocusEvent e) {
				calculatorLabel.setVisible(true);
			}
		});

		acceptLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if ((event.getClickCount() == 1) && acceptButton.isEnabled()) {
					setData();
				}
			}
		});
		cleanLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 1) {
					clear();
				}
			}
		});
		editLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 1) {
					editRow();
				}
			}
		});
		duplicateLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 1) {
					duplicateRow();
				}
			}
		});

		calculatorLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 1) {
					inputPanel.showCalculator();
				}
			}
		});

		setSelectOnFocusListener(amountField, shField, contraAccountField, docNumberField, accountField);
	}

	private void setSelectOnFocusListener(JTextComponent... textFields) {
		for (JTextComponent indicatorTF : textFields) {
			indicatorTF.addFocusListener(new SelectOnFocusListener(indicatorTF));
		}
	}

	private void initTableActions() {
		ActionMap actionMap = getActionMap();
		InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		TableAction upAction = new TableAction(tablePanel.getTable()) {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component focusOwner =  KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				int selectedRow = table.getSelectedRow();
				if (selectedRow <= 0) {
					selectedRow = 1;
				}
				table.clearSelection();
				table.getSelectionModel().addSelectionInterval(selectedRow - 1, selectedRow - 1);

				focusOwner.requestFocus();
			}
		};


		TableAction downAction = new TableAction(tablePanel.getTable()) {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component focusOwner =  KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				int selectedRow = table.getSelectedRow();

				table.clearSelection();
				table.getSelectionModel().addSelectionInterval(selectedRow + 1, selectedRow + 1);

				focusOwner.requestFocus();
			}
		};

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "clearFields");
		actionMap.put("clearFields", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, true), "duplicateRow");
		actionMap.put("duplicateRow", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				duplicateRow();
			}
		});

		actionMap.put("upAction", upAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK), "upAction");

		actionMap.put("downAction", downAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK), "downAction");

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, true), "editRow");
		actionMap.put("editRow", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editRow();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_DOWN_MASK, true), "moveTop");
		actionMap.put("moveTop", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner =  KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				tablePanel.moveTop();
				focusOwner.requestFocus();

			}
		});
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), "moveUp");
		actionMap.put("moveUp", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner =  KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				tablePanel.moveUp();
				focusOwner.requestFocus();
			}
		});
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK, true), "moveDown");
		actionMap.put("moveDown", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner =  KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				tablePanel.moveDown();
				focusOwner.requestFocus();
			}
		});
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_DOWN_MASK, true), "moveBottom");
		actionMap.put("moveBottom", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner =  KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				tablePanel.moveBottom();
				focusOwner.requestFocus();
			}
		});
	}

	public void editRow() {
		editRow(tablePanel.getTable().getSelectedRow());
	}

	public void markRow() {
		int row = tablePanel.getTable().getSelectedRow();
		if (row > -1) {
			Data data = tablePanel.getData(row);
			data.setMarked(true);
		}
	}

	public void demarkRow() {
		int row = tablePanel.getTable().getSelectedRow();
		if (row > -1) {
			Data data = tablePanel.getData(row);
			data.setMarked(false);
		}
	}

	public void removeRow() {
		int row = tablePanel.getTable().getSelectedRow();
		if (row > -1) {
			tablePanel.removeRow(row);
			setChanged();
		}
	}


	protected void clear() {
		amountField.setText("");
		shField.setText("H");
		contraAccountField.setText("");
		taxKeyCB.setSelectedIndex(0);
		docNumberField.setText("");
		if (currentData != null) {
			dateField.setValue(currentData.getDate());
			accountField.setText(currentData.getAccount());
		}
		showTextHelper = false;
		textArea.setText("");
		showTextHelper = true;
		amountField.requestFocus();
		currentData = null;
	}

	public void editRow(int row) {
		if (row > -1) {
			currentData = tablePanel.getData(row);
			if (!currentData.isEditable()) {
				return;
			}
			amountField.setText(Tools.DECIMAL_FORMAT.format(currentData.getAmount().doubleValue()));
			shField.setText(currentData.isDebit().booleanValue() ? "S" : "H");
			contraAccountField.setText(currentData.getContraAccount());
			taxKeyCB.setSelectedItem(currentData.getTaxKey());
			docNumberField.setText(currentData.getDocumentNr());
			dateField.setValue(currentData.getDate());
			accountField.setText(currentData.getAccount());
			showTextHelper = false;
			textArea.setText(currentData.getText());
			showTextHelper = true;
			amountField.requestFocus();
		}
	}

	private void initTextHelper() {
		texts = new TreeSet<String>();
		final DataContainerPanel contents = new DataContainerPanel(texts);
		final JPopupMenu menu = new JPopupMenu();
		menu.add(contents);
		contents.setDblClickHandler(new DataContainerPanel.IDblClickHandler() {
			@Override
			public void handle(String text) {
				textArea.setText(text);
				menu.setVisible(false);
				textArea.requestFocus();
			}
		});

		menu.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				KeyListener[] keyListeners = textArea.getKeyListeners();
				for (KeyListener keyListener : keyListeners) {
					keyListener.keyPressed(e);
				}
			}
		});

		textArea.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void update() {
				super.update();
				if (showTextHelper) {
					if (contents.getDataSize() != texts.size()) {
						contents.setData(texts);
					}
					Dimension preferredSize = menu.getPreferredSize();
					menu.setPreferredSize(new Dimension(290, preferredSize.height));
					menu.show(textArea, -1,- contents.getPreferredSize().height - 15);

					textArea.grabFocus();
					if (contents.filter(textArea.getText()) == 0) {
						menu.setVisible(false);
					}
				}
			}
		});
	}

	private void initPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem markMI = menu.add("Markieren");
		markMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				markRow();
			}
		});

		JMenuItem demarkMI = menu.add("Markierung aufheben");
		demarkMI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				demarkRow();
			}
		});
		menu.addSeparator();
		JMenuItem editMI = menu.add("Bearbeiten");
		editMI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				editRow();
			}
		});

		JMenuItem duplicateMI = menu.add("Duplizieren");
		duplicateMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				duplicateRow();
			}
		});
		menu.addSeparator();
		JMenuItem removeMI = menu.add("Entfernen");
		removeMI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				removeRow();
			}
		});

		tablePanel.setTablePopupMenu(menu);
	}

	private void initAutosave() {
		thread = new Thread() {
			@Override
			public void run() {
				try {
					int timeout = Settings.getAutosaveInterval() * 60000;
					while (true) {
						sleep(timeout);
						if (!isSaved()) {
							saveFile();

							if (MainFrame.getInstance().isShowSaveMessage()) {
								if (isSaved()) {
									JOptionPane.showMessageDialog(MainFrame.getInstance(), "<html>Datei <b>" + fileName
											+ "</b> wurde erfolgreich gespeichert.</html>");
								}
							}
							else {
								MainFrame.getInstance().getSavedFiles().add(fileName);
							}
						}
					}
				}
				catch (InterruptedException e) {
				}
			}
		};
		thread.setDaemon(true);

		thread.start();
	}

	public void duplicateRow() {
		duplicateRow(tablePanel.getTable().getSelectedRow());
	}

	public void duplicateRow(int row) {
		Data data = tablePanel.getData(row);
		currentData = null;
		if (row > -1) {
			amountField.setText(Tools.DECIMAL_FORMAT.format(data.getAmount().doubleValue()));
			shField.setText(data.isDebit().booleanValue() ? "S" : "H");
			contraAccountField.setText(data.getContraAccount());
			taxKeyCB.setSelectedItem(data.getTaxKey());
			docNumberField.setText(data.getDocumentNr());
			dateField.setValue(data.getDate());
			accountField.setText(data.getAccount());
			showTextHelper = false;
			textArea.setText(data.getText());
			showTextHelper = true;
			amountField.requestFocus();
		}

	}

	protected void recalcBalance(int row) {
		Data data = tablePanel.getData(row);

		balanceContraAccountTF.setText(Tools.getBalanceString(data.getContraAccount(), tablePanel));
		balanceAccountTF.setText(Tools.getBalanceString(data.getAccount(), tablePanel));
	}

	protected void setData() {

		boolean newData =  (currentData == null);
		if (newData) {
			currentData = new Data();
			currentData.setId(generator.getNextId());
		}
		Double amount = amountField.getDoubleValue();
		String selectedCurrency = (String)currencyCB.getSelectedItem();
		if (!selectedCurrency.equals(GeneralConstants.DEFAULT_CURRENCY)) {
			double currencyRate = CurrencyDownload.getCurrencyRate(selectedCurrency, dateField.getValue().getTime()).doubleValue();
			BigDecimal amountBD = new BigDecimal(amount.doubleValue() / currencyRate);
			amountBD =  amountBD.setScale(2, RoundingMode.HALF_UP);
			amount = new Double(amountBD.doubleValue());
		}
		boolean reverse = amount.doubleValue() < 0;
		currentData.setDebit(Boolean.valueOf(shField.getText().equalsIgnoreCase("s") || reverse));
		currentData.setAmount(Double.valueOf(Math.abs(amount.doubleValue())));
		currentData.setAccount(accountField.getText());
		currentData.setContraAccount(contraAccountField.getText());
		boolean dateChanged = dateField.getValue().compareTo(currentData.getDate()) != 0;
		currentData.setDate(dateField.getValue());
		currentData.setTaxKey((TaxKey) taxKeyCB.getSelectedItem());
		currentData.setDocumentNr(docNumberField.getText());
		currentData.setText(textArea.getText());

		Data linkedData = currentData.getLinkedData();
		if (currentData.getAccount().equals("4650") || currentData.getContraAccount().equals("4650")) {
			if (linkedData == null) {
				linkedData = new Data(false);
				linkedData.setId(generator.getNextId());
				linkedData.setDate(currentData.getDate());
				currentData.setLinkedData(linkedData);
			}
			if (currentData.getAccount().equals("4650")) {
				linkedData.setAccount("4654");
				linkedData.setContraAccount("4650");
			}
			else {
				linkedData.setContraAccount("4654");
				linkedData.setAccount("4650");
			}
			linkedData.setText("30% nicht abzugsfähig");
			double value = currentData.getAmount().doubleValue();
			if (currentData.getTaxKey().getId() == 0) {
				linkedData.setAmount(0.3 * value);
			}
			else if (currentData.getTaxKey().getId() == 9) {
				linkedData.setAmount(0.3 *(value - ((value*0.19) /1.19)));
			}
		}

		if (newData) {
			tablePanel.addRow(currentData);
			if(linkedData!= null) {
				tablePanel.addRow(linkedData);
			}
		}
		else {
			int selectedRow = tablePanel.getTable().getSelectedRow();
			if (dateChanged) {
				tablePanel.removeRow(selectedRow);
				tablePanel.addRow(currentData);
				if(linkedData!= null) {
					int row = tablePanel.findRow(linkedData);
					tablePanel.removeRow(row);
					tablePanel.addRow(linkedData);
				}
			}
			else {
				tablePanel.setData(selectedRow, currentData);
				if(linkedData!= null) {
					int row = tablePanel.findRow(linkedData);
					tablePanel.setData(row, linkedData);
				}
			}
		}
		tablePanel.select(currentData);
		setChanged();
		clear();
	}

	private void setChanged() {
		tablePanel.getChangeListener().stateChanged(null);
		extractTexts();
	}

	public void extractTexts() {
		texts = new TreeSet<String>();
		ArrayList<Data> allData = getAllData();
		for (Data data : allData) {
			texts.add(data.getText().trim());
		}
	}

	public String openExcel() {
		final MainFrame mainFrame = MainFrame.getInstance();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.resetChoosableFileFilters();
		fileChooser.setCurrentDirectory(new File(Settings.getLastDir()));
		fileChooser.setFileFilter(new ExcelFileFilter());
		int result = fileChooser.showOpenDialog(mainFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			if ((file != null) && file.exists()) {
				final WaitWindow waitWindow = new WaitWindow(mainFrame);
				new Thread() {
					@Override
					public void run() {
						try {
							Settings.setLastDir(file.getParent());

							ExcelImporter importer = new ExcelImporter(file, generator);
							importer.setVisible(true);


							setFile(file.getAbsolutePath());

							setData(importer.getDatas());
						}
						catch (Exception e) {
							Tools.log(e);
						}

						waitWindow.dispose();
					}
				}.start();
				waitWindow.setVisible(true);
				if (tablePanel.getTable().getRowCount() > 0) {
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf('.'));
					return name;
				}
			}
		}

		return null;
	}
	public String openLexwareExcel() {
		final MainFrame mainFrame = MainFrame.getInstance();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.resetChoosableFileFilters();
		fileChooser.setCurrentDirectory(new File(Settings.getLastDir()));
		fileChooser.setFileFilter(new ExcelFileFilter());
		int result = fileChooser.showOpenDialog(mainFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			if ((file != null) && file.exists()) {
				final WaitWindow waitWindow = new WaitWindow(mainFrame);
				new Thread() {
					@Override
					public void run() {
						try {
							Settings.setLastDir(file.getParent());
							LexwareImporter importer = new LexwareImporter(file);

							setFile(file.getAbsolutePath());

							setData(importer.getData());
						}
						catch (Exception e) {
							Tools.log(e);
						}

						waitWindow.dispose();
					}
				}.start();
				waitWindow.setVisible(true);
				if (tablePanel.getTable().getRowCount() > 0) {
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf('.'));
					return name;
				}
			}
		}

		return null;
	}

	public String openTextFile() {
		final MainFrame mainFrame = MainFrame.getInstance();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.resetChoosableFileFilters();
		fileChooser.setCurrentDirectory(new File(Settings.getLastDir()));
		fileChooser.setFileFilter(new TXTFileFilter());

		int result = fileChooser.showOpenDialog(mainFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			if ((file != null) && file.exists()) {
				final WaitWindow waitWindow = new WaitWindow(mainFrame);
				new Thread() {
					@Override
					public void run() {
						try {
							Settings.setLastDir(file.getParent());
							ArrayList<Data> datas = readTXT(file);

							setFile(file.getAbsolutePath());

							setData(datas);
						}
						catch (Exception exception) {
							Tools.log(exception);
						}

						waitWindow.dispose();
					}
				}.start();
				waitWindow.setVisible(true);
				if (tablePanel.getTable().getRowCount() > 0) {
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf('.'));
					return name;
				}
			}
		}

		return null;
	}

	public String insertTextFile() {
		final MainFrame mainFrame = MainFrame.getInstance();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.resetChoosableFileFilters();
		fileChooser.setCurrentDirectory(new File(Settings.getLastDir()));
		fileChooser.setFileFilter(new TXTFileFilter());

		int result = fileChooser.showOpenDialog(mainFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			if ((file != null) && file.exists()) {
				final WaitWindow waitWindow = new WaitWindow(mainFrame);
				new Thread() {
					@Override
					public void run() {
						try {
							Settings.setLastDir(file.getParent());
							ArrayList<Data> newDatas = readTXT(file);
							ArrayList<Data> datas = getAllData();
							datas.addAll(newDatas);
							setData(datas);
							setSaved(false);
						}
						catch (Exception exception) {
							Tools.log(exception);
						}

						waitWindow.dispose();
					}
				}.start();
				waitWindow.setVisible(true);
				if (tablePanel.getTable().getRowCount() > 0) {
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf('.'));
					return name;
				}
			}
		}

		return null;
	}

	protected ArrayList<Data> readTXT(File file) throws IOException {
		ArrayList<Data> datas = new ArrayList<Data>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Data data = null;
		String str;
		String strings[];
		ArrayList<String> errors = new ArrayList<String>();
		for (int i = 0; reader.ready(); i++) {
			str = reader.readLine();
			if (str.startsWith("\"")) {
				continue;
			}

			strings = str.split(";");

			data = new Data();
			try {
				data.setId(generator.getNextId());
				data.setDate(Tools.parseDate(strings[0]));
				String documentNr = strings[3];
				data.setDocumentNr(documentNr.equals("-") ? "" : documentNr);
				data.setText(Tools.removeQuote(strings[4]));
				data.setAmount(Tools.parseDouble(strings[5]));
				data.setContraAccount(strings[6]);
				data.setAccount(strings[7]);
				data.setTaxKey(GeneralConstants.TAX_KEYS.get(strings[8]));
				datas.add(data);
			}
			catch (Exception exception) {
				exception.printStackTrace();
				String message = exception.getMessage();
				errors.add("Fehler in Zeile " + i + "  " + (message == null ? "" : message));
			}

		}
		reader.close();

		if (!errors.isEmpty()) {
			JTextArea area = new JTextArea();
			area.setEditable(false);
			for (String string : errors) {
				area.append(string + "\n");
			}

			JOptionPane.showMessageDialog(MainFrame.getInstance(), new JScrollPane(area));
		}
		return datas;
	}

	protected void writeTXT(File file, ArrayList<Data> dataVector) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
		writer.write(GeneralConstants.HEADER);
		writer.newLine();
		StringBuilder row;
		for (Data data : dataVector) {
			row = new StringBuilder();
			row.append(Tools.format(data.getDate())).append(";");
			row.append(Tools.format(data.getDate())).append(";\"\";");
			row.append(Tools.isStringEmpty(data.getDocumentNr()) ? "-" : data.getDocumentNr()).append(";");
			row.append("\"").append(data.getText()).append("\";");
			row.append(Tools.format(data.getAmount())).append(";");

			if (data.isDebit().booleanValue()) {
				row.append(data.getAccount()).append(";").append(data.getContraAccount());
			}
			else {
				row.append(data.getContraAccount()).append(";").append(data.getAccount());
			}
			row.append(";");
			row.append(data.getTaxKey().getId());
			row.append(";\"\";\"\";");
			row.append(Tools.format(data.getAmount()));
			row.append(";");
			row.append(Tools.format(data.getAmount())).append(";EUR;");
			writer.write(row.toString());
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	public boolean checkSaved() {
		if (!saved) {
			int ret = JOptionPane.showConfirmDialog(this, "Möchten Sie die Änderungen speichern?", "", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (ret) {
			case JOptionPane.YES_OPTION:
				return saveFile();
			case JOptionPane.NO_OPTION:
				return true;
			case JOptionPane.CANCEL_OPTION:
				return false;
			}
		}
		return true;
	}

	public void close() {
		CloseTabbedPane tabbedPane = MainFrame.getInstance().getTabbedPane();
		int index = tabbedPane.indexOfComponent(this);
		thread.interrupt();
		tabbedPane.removeTabAt(index);
	}

	protected boolean setData(ArrayList<Data> datas) {
		if (datas.size() == 0) {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "Es konnten keine Daten importiert werden.\n ");
			return false;
		}

		tablePanel.setDataVector(sort(datas));

		setSaved(true);
		setNewFile(false);
		extractTexts();

		return true;
	}

	private Vector<TableRow> sort(ArrayList<Data> datas) {
		TreeMap<GregorianCalendar, ArrayList<Data>> dataMap = new TreeMap<GregorianCalendar, ArrayList<Data>>();
		for (Data data : datas) {
			GregorianCalendar date = data.getDate();
			ArrayList<Data> list = dataMap.get(date);
			if (list == null) {
				list = new ArrayList<Data>();
			}
			data.setOrder(Integer.valueOf(list.size()));
			list.add(data);
			dataMap.put(date, list);

		}

		Vector<TableRow> dataVector = new Vector<TableRow>();
		for (Iterator<GregorianCalendar> iterator = dataMap.keySet().iterator(); iterator.hasNext();) {
			for (Data data : dataMap.get(iterator.next())) {
				dataVector.add(new TableRow(data));
			}
		}
		return dataVector;
	}

	private String getTitle() {
		CloseTabbedPane pane = MainFrame.getInstance().getTabbedPane();
		int index = pane.indexOfComponent(this);
		return pane.getTitleAt(index);
	}

	private void setTitle() {
		CloseTabbedPane pane = MainFrame.getInstance().getTabbedPane();
		int index = pane.indexOfComponent(this);

		String title = new File(fileName).getName();
		title = title.substring(0, title.lastIndexOf('.'));
		pane.setTitleAt(index, title);
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
		CloseTabbedPane pane = MainFrame.getInstance().getTabbedPane();
		int index = pane.indexOfComponent(this);
		if (index != -1) {
			TabHeader tabHeader = (TabHeader) pane.getTabComponentAt(index);
			tabHeader.setIcon(saved ? ImageConstants.SAVED : ImageConstants.UNSAVED);
			String title = getTitle();
			boolean startsWithStar = title.startsWith("* ");
			if (saved) {
				if (startsWithStar) {
					title = title.substring(2);
				}
			}
			else if (!startsWithStar) {
				title = "* " + title;
			}

			tabHeader.setTitle(title);
		}
	}

	public boolean isNewFile() {
		return newFile;
	}

	public void setNewFile(boolean newFile) {
		this.newFile = newFile;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean saveFile() {
		File file = null;
		MainFrame mainFrame = MainFrame.getInstance();
		if (newFile) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.resetChoosableFileFilters();
			fileChooser.setCurrentDirectory(new File(Settings.getLastDir()));
			fileChooser.addChoosableFileFilter(new TXTFileFilter());
			while (true) {
				int ret = fileChooser.showSaveDialog(mainFrame);
				file = fileChooser.getSelectedFile();
				if ((ret != JFileChooser.APPROVE_OPTION) || (file == null)) {
					return false;
				}
				if (file.exists()) {
					ret = JOptionPane.showConfirmDialog(mainFrame, "Überschreiben " + file.getName(), "Datei existiert!", JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						break;
					}
				}
				else {
					break;
				}
			}
		}
		else {
			file = new File(fileName);
		}

		if (file != null) {
	        try {
		        String path = file.getAbsolutePath();
		        if (!path.toLowerCase().endsWith(".txt")) {
			        file = new File(path + ".txt");
		        }
		        writeTXT(file, getAllData());
		        setFile(file.getAbsolutePath());
		        setSaved(true);
		        setTitle();
		        Settings.setLastDir(file.getParent());
	        } catch (IOException exception) {
		        Tools.log(exception);
	        }
	        setNewFile(false);
        }
		return true;
	}

	public ArrayList<Data> getAllData() {
		return tablePanel.getAllData();
	}

	public boolean isEmpty() {
		return tablePanel.getTable().getRowCount() == 0;
	}


	public String getReportFirmName() {
		return reportFirmName;
	}


	public void setReportFirmName(String reportFirmName) {
		this.reportFirmName = reportFirmName;
	}


	public String getReportAccount() {
		return reportAccount;
	}


	public void setReportAccount(String reportAccount) {
		this.reportAccount = reportAccount;
	}


	public boolean isReportAccountActive() {
		return reportAccountActive;
	}


	public void setReportAccountActive(boolean reportAccountActive) {
		this.reportAccountActive = reportAccountActive;
	}

	private class SelectOnFocusListener extends FocusAdapter {

		private final JTextComponent textComponent;

		public SelectOnFocusListener(JTextComponent textComponent) {
			this.textComponent = textComponent;
		}

		@Override
		public void focusGained(FocusEvent e) {
			textComponent.selectAll();
		}
	}

}
