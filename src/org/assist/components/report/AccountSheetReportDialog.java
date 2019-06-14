package org.assist.components.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.assist.components.common.AutoCompleteComboBox;
import org.assist.components.main.EntryTab;
import org.assist.domain.AccountSheetData;
import org.assist.domain.Data;
import org.assist.tools.Tools;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class AccountSheetReportDialog extends AbstractReportDialog {
	private AutoCompleteComboBox<String> accountCB;
	private JCheckBox activeAccountChB;

	public AccountSheetReportDialog(EntryTab tab) {
		super("Kontoblatt", tab);

		init();
		initListeners();
		fill();
	}

	private void init() {
		accountCB = new AutoCompleteComboBox<String>();
		activeAccountChB = new JCheckBox("Aktives Konto");



		double preferred = TableLayoutConstants.PREFERRED;
		double[][] layoutSizes = { { preferred, preferred, preferred, preferred }, { 25, preferred, preferred, preferred, preferred, 20, preferred } };

		TableLayout layout = new TableLayout(layoutSizes);
		layout.setHGap(5);
		layout.setVGap(2);

		setLayout(layout);

		add(Tools.createLabel("Firma"), "0,0");
		add(firmNameTF, "1,0,3,0");
		add(Tools.createLabel("Konto"), "0,1");
		add(accountCB, "1,1,3,1");
		add(activeAccountChB, "1,2,3,2");
		add(Tools.createLabel("Startdatum"), "0,3");
		add(startMonthCB, "1,3");
		add(startYearCB, "2,3");
		add(Tools.createLabel("Enddatum"), "0,4");
		add(endMonthCB, "1,4");
		add(endYearCB, "2,4");
		add(useEndDateChB, "3,4");
		add(createButtonPanel(), "0,6,3,6");
		pack();
		setLocationRelativeTo(getOwner());
	}



	private void fill() {
		endMonthCB.setEnabled(false);
		endYearCB.setEnabled(false);

		TreeSet<String> accounts = new TreeSet<String>();
		TreeSet<GregorianCalendar> dates = new TreeSet<GregorianCalendar>();
		for (Data data : tab.getAllData()) {
			accounts.add(data.getAccount());
			accounts.add(data.getContraAccount());
			dates.add(data.getDate());
		}
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(accounts.toArray(new String[0]));
		accountCB.setModel(model);

		startMonthCB.setSelectedIndex(dates.first().get(Calendar.MONTH));
		startYearCB.setSelectedItem(new Integer(dates.first().get(Calendar.YEAR)));
		endMonthCB.setSelectedIndex(dates.last().get(Calendar.MONTH));
		endYearCB.setSelectedItem(new Integer(dates.last().get(Calendar.YEAR)));
		firmNameTF.setText(tab.getReportFirmName());
		activeAccountChB.setSelected(tab.isReportAccountActive());
	//	accountCB.findAndSelelect(tab.getReportAccount());
		accountCB.setSelectedItem(tab.getReportAccount());
	}

	@Override
	protected void createReport() throws JRException {
		String account = (String)accountCB.getSelectedItem();
		boolean isActive = activeAccountChB.isSelected();
		boolean useEndDate = useEndDateChB.isSelected();

		GregorianCalendar startDate = new GregorianCalendar(((Integer)startYearCB.getSelectedItem()).intValue(), startMonthCB.getSelectedIndex(), 1);
		GregorianCalendar endDate = new GregorianCalendar(((Integer)endYearCB.getSelectedItem()).intValue(), endMonthCB.getSelectedIndex() + 1, 1);

		ArrayList<AccountSheetData> datas = new ArrayList<AccountSheetData>();
		double balance = 0;

		for (Data data : tab.getAllData()) {
			GregorianCalendar date = data.getDate();

			if ((startDate.compareTo(date) <= 0) && (!useEndDate || (useEndDate && (endDate.compareTo(date) >= 0)))) {
				AccountSheetData reportData = new AccountSheetData();
				reportData.setDocDate(date.getTime());
				reportData.setDocNumber(data.getDocumentNr());
				reportData.setDocText(data.getText());
				reportData.setTaxKey(data.getTaxKey().getId().toString());

				if (data.getAccount().equals(account)) {
					if (data.isDebit().booleanValue()) {
						reportData.setDebit(data.getAmount());
						reportData.setCredit(null);
						reportData.setAccount(data.getContraAccount());
					}

//					else if ((isActive && !data.isDebit().booleanValue()) ) {
//						reportData.setDebit(null);
//						reportData.setCredit(data.getAmount());
//						reportData.setAccount(data.getContraAccount());
//					}
					else {
						reportData.setDebit(null);
						reportData.setCredit(data.getAmount());
						reportData.setAccount(data.getContraAccount());
					}
				}
				else if (data.getContraAccount().equals(account)) {
					if ((data.isDebit().booleanValue())) {
						reportData.setDebit(null);
						reportData.setCredit(data.getAmount());
						reportData.setAccount(data.getAccount());
					}
					else {
						reportData.setDebit(data.getAmount());
						reportData.setCredit(null);
						reportData.setAccount(data.getAccount());
					}
				}
				else {
					continue;
				}
				Double debit = reportData.getDebit();
				Double credit = reportData.getCredit();
				balance += (debit == null ? 0 : debit.doubleValue()) - (credit == null ? 0 : credit.doubleValue());

				if(isActive) {
					balance *= -1;
				}
				reportData.setBalance(Double.valueOf( balance));

				datas.add(reportData);
			}
		}

		if (datas.isEmpty()) {
			JOptionPane.showMessageDialog(getOwner(), "Keine Daten zur Berichterstellung", "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}


		String firmName = firmNameTF.getText();
		tab.setReportAccount(account);
		tab.setReportAccountActive(isActive);
		tab.setReportFirmName(firmName);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ReportTitle", "Firma " + firmName + " Konto " + account);
		parameters.put("StartAmount", datas.get(0).getBalance());
		JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/AccountSheet.jasper"), parameters, new JRBeanCollectionDataSource(datas, false));
		String title = "Kontoblatt " + account + "  " + firmName;

		viewReport(title, print);
	}


}
