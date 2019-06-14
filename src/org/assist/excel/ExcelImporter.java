package org.assist.excel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.assist.components.main.MainFrame;
import org.assist.constants.GeneralConstants;
import org.assist.domain.Data;
import org.assist.domain.TaxKey;
import org.assist.tools.CurrencyDownload;
import org.assist.tools.IDGenerator;
import org.assist.tools.Tools;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class ExcelImporter extends JDialog {

	private HSSFWorkbook workbook;

	private int dateFieldIndex = -1;
	private int numberField1Index = -1;
	private int numberField2Index = -1;
	private int textFieldIndex = -1;
	private int amountFieldIndex = -1;
	private int accountFieldIndex = -1;
	private int contraAccountFieldIndex = -1;
	private int debitFieldIndex = -1;
	private int taxKeyFieldIndex = -1;
	private int currencyFieldIndex = -1;

	protected JComboBox<String> tableCB;
	private JComboBox<?> comboBoxes[] = new JComboBox[10];

	ArrayList<Data> datas;

	private String fileName;

	private final IDGenerator generator;

	public ExcelImporter(File file, IDGenerator generator) throws Exception {
		super(MainFrame.getInstance(), "Spaltenzuordnung", true);
		this.generator = generator;

		init(file);
	}

	private void init(File file) throws Exception {
		FileInputStream inputStream = new FileInputStream(file);
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
		workbook = new HSSFWorkbook(poifsFileSystem);
		fileName = file.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));

		datas = new ArrayList<Data>();
		initContentPane();
		inputStream.close();
	}

	private void initContentPane() {
		JButton cancelButton = new JButton("Abbrechen");
		JButton okButton = new JButton("OK");

		for (int i = 0; i < comboBoxes.length; i++) {
			comboBoxes[i] = new JComboBox<String>();
		}

		double lSize[][] = { { 10, 100, 10, TableLayoutConstants.FILL }, { 10, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 } };

		JPanel cbPanel = new JPanel(new TableLayout(lSize));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		add(cbPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		tableCB = new JComboBox<String>(getSheetNames());

		tableCB.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					initComboBoxes(tableCB.getSelectedIndex());
					// cbPanel.updateUI();
				}
			}
		});

		cbPanel.add(new JLabel("Tabelle"), "1,1");
		JLabel dateLabel = new JLabel("Datum");
		dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD));
		cbPanel.add(dateLabel, "1,2");
		JLabel textLabel = new JLabel("Buchungstext");
		textLabel.setFont(textLabel.getFont().deriveFont(Font.BOLD));
		cbPanel.add(textLabel, "1,3");
		JLabel amountLabel = new JLabel("Umsatz");
		amountLabel.setFont(amountLabel.getFont().deriveFont(Font.BOLD));
		cbPanel.add(amountLabel, "1,4");
		cbPanel.add(new JLabel("Konto"), "1,5");
		cbPanel.add(new JLabel("Gegenkonto"), "1,6");
		cbPanel.add(new JLabel("Soll/Haben"), "1,7");
		cbPanel.add(new JLabel("Steuerschlüssel"), "1,8");
		cbPanel.add(new JLabel("Belegnummer 1"), "1,9");
		cbPanel.add(new JLabel("Belegnummer 2"), "1,10");
		cbPanel.add(new JLabel("Währung"), "1,11");

		cbPanel.add(tableCB, "3,1");

		initComboBoxes(0);
		for (int i = 0; i < comboBoxes.length; i++) {
			cbPanel.add(comboBoxes[i], "3," + (i + 2));
		}

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (check()) {
					processImport();
					dispose();
				}
				else {
					JOptionPane.showMessageDialog(getOwner(), "\"Datum\", \"Umsatz\" und \"Text\" " + "müssen auf jeden Fall gesetzt werden", "Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		setSize(new Dimension(300, 365));
		setLocationRelativeTo(getOwner());
	}

	protected boolean check() {
		dateFieldIndex = getSelectedColumn(0);
		textFieldIndex = getSelectedColumn(1);
		amountFieldIndex = getSelectedColumn(2);
		accountFieldIndex = getSelectedColumn(3);
		contraAccountFieldIndex = getSelectedColumn(4);
		debitFieldIndex = getSelectedColumn(5);
		taxKeyFieldIndex = getSelectedColumn(6);
		numberField1Index = getSelectedColumn(7);
		numberField2Index = getSelectedColumn(8);
		currencyFieldIndex = getSelectedColumn(9);

		return (dateFieldIndex >= 0) & (textFieldIndex >= 0) & (amountFieldIndex >= 0);

	}

	private int getSelectedColumn(int index) {
		return (comboBoxes[index].getSelectedIndex() - 1);
	}

	@SuppressWarnings("unchecked")
	protected void initComboBoxes(int index) {
		String[] names = getColumnNames(index);
		String columnNames[] = new String[0];
		if (names == null) {
			for (int i = 0; i < comboBoxes.length; i++) {
				initCombobox((JComboBox<String>) comboBoxes[i], columnNames);
				comboBoxes[i].setEnabled(false);
			}
		}
		else {
			columnNames = new String[names.length + 1];
			for (int i = 0; i < names.length; i++) {
				columnNames[i + 1] = names[i];
			}

			columnNames[0] = "";

			for (int i = 0; i < comboBoxes.length; i++) {
				initCombobox((JComboBox<String>) comboBoxes[i], columnNames);
				comboBoxes[i].setEnabled(true);
			}
		}

	}

	private void initCombobox(JComboBox<String> box, String[] columnNames) {
		DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>)box.getModel();
		model.removeAllElements();
		for (int i = 0; i < columnNames.length; i++) {
			model.addElement(columnNames[i]);
		}
	}

	private String[] getSheetNames() {
		String[] sheetNames = new String[workbook.getNumberOfSheets()];
		for (int i = 0; i < sheetNames.length; i++) {
			sheetNames[i] = workbook.getSheetName(i);
		}

		return sheetNames;
	}

	private String[] getColumnNames(int index) {
		HSSFSheet sheet = workbook.getSheetAt(index);
		HSSFRow firstRow = sheet.getRow(sheet.getFirstRowNum());
		String[] columnNames = null;

		if (firstRow != null) {
			short firstCellNr = firstRow.getFirstCellNum();
			short lastCellNr = firstRow.getLastCellNum();

			columnNames = new String[lastCellNr - firstCellNr];
			HSSFCell cell;
			int i = 0;
			for (int cellNr = firstCellNr; cellNr < lastCellNr; cellNr++) {
				cell = firstRow.getCell(cellNr);
				columnNames[i++] = (String)Tools.getCellValue(cell);
			}
		}
		return columnNames;
	}

	protected void processImport() {
		HSSFSheet sheet = workbook.getSheetAt(tableCB.getSelectedIndex());
		HSSFRow firstRow = sheet.getRow(sheet.getFirstRowNum());
		short firstCellNr = firstRow.getFirstCellNum();

		ArrayList<String> failedCells = new ArrayList<String>();
		for (int rowNr = sheet.getFirstRowNum(); rowNr <= sheet.getLastRowNum(); rowNr++) {
			HSSFRow row = sheet.getRow(rowNr + 1);
			if (row != null) {
				Data data = new Data();
				try {
					Object amountValue = Tools.getCellValue(row.getCell((firstCellNr + amountFieldIndex)));
					if ((amountValue == null) || !(amountValue instanceof Double)) {
						continue;
					}
					Double amount = (Double)amountValue;
					amount = new Double(Math.abs(amount.doubleValue()));
					data.setAmount(amount);


					data.setDate((GregorianCalendar)Tools.getCellValue(row.getCell((firstCellNr + dateFieldIndex)), true));
					data.setText(Tools.removeDangerousChars(Tools.getCellValue(row.getCell(firstCellNr + textFieldIndex)).toString()));

					String taxkey = Tools.truncDecimal(Tools.getCellValue(row.getCell(firstCellNr + taxKeyFieldIndex)));
					String account = Tools.truncDecimal(Tools.getCellValue(row.getCell(firstCellNr + accountFieldIndex)));
					String contraAccount = Tools.truncDecimal(Tools.getCellValue(row.getCell(firstCellNr + contraAccountFieldIndex)));
					String sh = (String)Tools.getCellValue(row.getCell((firstCellNr + debitFieldIndex)));

					String num1 = Tools.truncDecimal(Tools.getCellValue(row.getCell(firstCellNr + numberField1Index)));
					String num2 = Tools.truncDecimal(Tools.getCellValue(row.getCell(firstCellNr + numberField2Index)));

					String num = (num1 == null) ? num2 : num1;

					if (num != null) {
						int ind = num.lastIndexOf(".");
						num = num.substring(0, (ind == -1) ? num.length() : ind);
					}
					else {
						num = "";
					}
					data.setDocumentNr(num);

					sh = (sh != null) ? sh.trim() : sh;
					data.setAccount(account);
					data.setContraAccount(contraAccount);
					if ((sh == null) || (sh.length() == 0) || sh.equalsIgnoreCase("S")) {
						data.setDebit(Boolean.TRUE);
					}

					if (currencyFieldIndex != -1) {
						String selectedCurrency =  (String)Tools.getCellValue(row.getCell((firstCellNr + currencyFieldIndex)));;
						if (!Tools.isStringEmpty(selectedCurrency) &&  !selectedCurrency.equals(GeneralConstants.DEFAULT_CURRENCY)) {
							double currencyRate = CurrencyDownload.getCurrencyRate(selectedCurrency,data.getDate().getTime()).doubleValue();
							BigDecimal amountBD = new BigDecimal(amount.doubleValue() / currencyRate);
							amountBD =  amountBD.setScale(2, RoundingMode.HALF_UP);
							amount = new Double(amountBD.doubleValue());
							data.setAmount(amount);
						}
					}


					if(data.isDebit().booleanValue()) {
						data.setAccount(contraAccount);
						data.setContraAccount(account);
						data.setDebit(Boolean.FALSE);
					}

					if (taxkey.trim().length() == 0) {
						taxkey = "0";
					}

					if (!taxkey.equals("0")) {
						data.setTaxKey(GeneralConstants.TAX_KEYS.get(taxkey));
					}

					if (data.getTaxKey() == null){

						TaxKey autoTaxKey = Tools.getAutoTaxKey(contraAccount, account);
						if (autoTaxKey != null) {
							data.setTaxKey(autoTaxKey);
						}
						else {
							//data.setTaxKey(GeneralConstants.TAX_KEYS.get("0"));

							throw new Exception("Steuerschlüssel fehlerhaft");
						}
					}

					Data linkedData = data.getLinkedData();
					if (data.getAccount().equals("4650") || data.getContraAccount().equals("4650")) {
						if (linkedData == null) {
							linkedData = new Data(false);
							linkedData.setId(generator.getNextId());
							linkedData.setDate(data.getDate());
							data.setLinkedData(linkedData);
						}
						if (data.getAccount().equals("4650")) {
							linkedData.setAccount("4654");
							linkedData.setContraAccount("4650");
						}
						else {
							linkedData.setContraAccount("4654");
							linkedData.setAccount("4650");
						}
						linkedData.setText("30% nicht abzugsfähig");
						double value = data.getAmount().doubleValue();
						if (data.getTaxKey().getId() == 0) {
							linkedData.setAmount(0.3 * value);
						}
						else if (data.getTaxKey().getId() == 9) {
							linkedData.setAmount(0.3 *(value - ((value*0.19) /1.19)));
						}
					}

					data.setId(generator.getNextId());
					datas.add(data);

					if(linkedData != null) {
						datas.add(linkedData);
					}
				}
				catch (Exception e) {
					failedCells.add("Zeile : " + (rowNr + 2) + "\t" + e.getMessage() + "\n");
				}
			}
		}


		if (!failedCells.isEmpty()) {
			JTextArea area = new JTextArea();
			area.setEditable(false);

			for (String string : failedCells) {
				area.append(string + "\n");
			}

			JScrollPane scrollPane = new JScrollPane(area);
			JOptionPane.showMessageDialog(MainFrame.getInstance(), scrollPane);
		}
	}

	public ArrayList<Data> getDatas() {
		return datas;
	}
}
