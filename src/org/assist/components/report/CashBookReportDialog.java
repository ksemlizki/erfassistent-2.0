package org.assist.components.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.assist.components.entry.AmountField;
import org.assist.components.main.EntryTab;
import org.assist.constants.ImageConstants;
import org.assist.domain.CashBookData;
import org.assist.domain.Data;
import org.assist.domain.ReportMonth;
import org.assist.tools.GUITools;
import org.assist.tools.Tools;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;

public class CashBookReportDialog extends AbstractReportDialog {
	private AmountField startAmountTF;

	public CashBookReportDialog(EntryTab tab) {
		super("Kassenbericht", tab);

		init();
		initListeners();
		fill();
	}

	private void init() {
		double preferred = TableLayoutConstants.PREFERRED;
		double[][] layoutSizes = { { preferred, preferred, preferred, preferred }, { 25, 25, preferred, preferred, 20, preferred } };

		TableLayout layout = new TableLayout(layoutSizes);
		layout.setHGap(5);
		layout.setVGap(2);

		setLayout(layout);
		startAmountTF = new AmountField(false);

		add(Tools.createLabel("Firma"), "0,0");
		add(firmNameTF, "1,0,3,0");
		add(Tools.createLabel("Anfangsbestand"), "0,1");
		add(startAmountTF, "1,1,3,1");
		add(Tools.createLabel("Startdatum"), "0,2");
		add(startMonthCB, "1,2");
		add(startYearCB, "2,2");
		add(Tools.createLabel("Enddatum"), "0,3");
		add(endMonthCB, "1,3");
		add(endYearCB, "2,3");
		add(useEndDateChB, "3,3");
			add(createButtonPanel(), "0,5,3,5");
		pack();
		setLocationRelativeTo(getOwner());
	}

	private void fill() {
		endMonthCB.setEnabled(false);
		endYearCB.setEnabled(false);

		TreeSet<GregorianCalendar> dates = new TreeSet<GregorianCalendar>();
		for (Data data : tab.getAllData()) {
			dates.add(data.getDate());
		}

		startMonthCB.setSelectedIndex(dates.first().get(Calendar.MONTH));
		startYearCB.setSelectedItem(new Integer(dates.first().get(Calendar.YEAR)));
		endMonthCB.setSelectedIndex(dates.last().get(Calendar.MONTH));
		endYearCB.setSelectedItem(new Integer(dates.last().get(Calendar.YEAR)));

	}

	@Override
	protected void createReport() throws JRException {
		String account = "1000";
		boolean useEndDate = useEndDateChB.isSelected();
		TreeMap<ReportMonth, ArrayList<CashBookData>> cashBookDatas = new TreeMap<ReportMonth, ArrayList<CashBookData>>();
		GregorianCalendar startDate = new GregorianCalendar(((Integer) startYearCB.getSelectedItem()).intValue(), startMonthCB.getSelectedIndex(), 1);
		GregorianCalendar endDate = new GregorianCalendar(((Integer) endYearCB.getSelectedItem()).intValue(), endMonthCB.getSelectedIndex() + 1, 1);

		Double startAmount = startAmountTF.getDoubleValue();
		double balance = startAmount.doubleValue();
		for (Data data : tab.getAllData()) {
			GregorianCalendar date = data.getDate();

			if ((startDate.compareTo(date) <= 0) && (!useEndDate || (useEndDate && (endDate.compareTo(date) >= 0)))) {

				CashBookData cashBookData = new CashBookData();
				cashBookData.setDate(data.getDate().getTime());
				cashBookData.setAccountNr(data.getDocumentNr());
				cashBookData.setText(data.getText());

				if (data.getAccount().equals(account)) {
					if (data.isDebit().booleanValue()) {
						cashBookData.setIncome(data.getAmount());
						cashBookData.setExpence(null);
					}
					else {
						cashBookData.setIncome(null);
						cashBookData.setExpence(data.getAmount());
					}
				}
				else if (data.getContraAccount().equals(account)) {
					if (data.isDebit().booleanValue()) {
						cashBookData.setIncome(null);
						cashBookData.setExpence(data.getAmount());
					}
					else {
						cashBookData.setIncome(data.getAmount());
						cashBookData.setExpence(null);
					}
				}
				else {
					continue;
				}

				balance += (cashBookData.getIncome() == null ? 0 : cashBookData.getIncome().doubleValue()) - (cashBookData.getExpence() == null ? 0 : cashBookData.getExpence().doubleValue());
				cashBookData.setBalance(Double.valueOf(balance));

				ReportMonth reportMonth = new ReportMonth(cashBookData.getDate());
				ArrayList<CashBookData> list = cashBookDatas.get(reportMonth);
				if (list == null) {
					list = new ArrayList<CashBookData>();
					cashBookDatas.put(reportMonth, list);
				}
				list.add(cashBookData);
			}
		}

		if (cashBookDatas.isEmpty()) {
			JOptionPane.showMessageDialog(getOwner(), "Keine Daten zur Berichterstellung", "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String firmName = firmNameTF.getText();
		tab.setReportFirmName(firmName);

		//ArrayList<CashBookData> completeList = new ArrayList<CashBookData>();

		Set<ReportMonth> monthSet = cashBookDatas.keySet();
		for (Iterator<ReportMonth> iterator = monthSet.iterator(); iterator.hasNext();) {
			ReportMonth reportMonth = iterator.next();
			reportMonth.setStartAmount(startAmount);
			ArrayList<CashBookData> arrayList = cashBookDatas.get(reportMonth);
			for (CashBookData cashBookData : arrayList) {
				cashBookData.getMonth().setStartAmount(startAmount);
				//	completeList.add(cashBookData);
			}

			startAmount = arrayList.get(arrayList.size() - 1).getBalance();
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("firmName", firmName);

		//JasperPrint print = JasperFillManager.fillReport(reportStream, parameters, new JRBeanCollectionDataSource(completeList, false));
		ArrayList<ReportMonth> vector = new ArrayList<ReportMonth>(monthSet);

		final JDialog dialog = new JDialog((Frame) getOwner(), "Kassenbericht" + firmName + " " + vector.get(0) + " - " + vector.get(vector.size() - 1), true);
		monthSet = cashBookDatas.keySet();
		JTabbedPane pane = new JTabbedPane();
		final TreeMap<ReportMonth, JasperPrint> prints = new TreeMap<ReportMonth, JasperPrint>();
		for (Iterator<ReportMonth> iterator = monthSet.iterator(); iterator.hasNext();) {
			ReportMonth reportMonth = iterator.next();
			JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/CashBook.jasper"), parameters, new JRBeanCollectionDataSource(cashBookDatas.get(reportMonth), false));

			JRViewer viewer = new JRViewer(print);
			viewer.setFitPageZoomRatio();

			prints.put(reportMonth, print);

			pane.addTab(reportMonth.toString(), viewer);
		}

		//viewReport("Kassenbericht" + firmName + " " + vector.get(0) + " - " + vector.get(vector.size() - 1), print);

		//		JRViewer viewer = new JRViewer(print);
		//		viewer.setFitPageZoomRatio();
		setVisible(false);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		JButton button = new JButton("", ImageConstants.PRINT);
		button.setToolTipText("Kassenberichte ausdrucken");
		button.setFocusable(false);
		toolBar.add(button);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new MonthReportSelectDialog(dialog, prints).setVisible(true);
			}
		});

		dialog.getContentPane().add(toolBar, BorderLayout.NORTH);
		dialog.getContentPane().add(pane, BorderLayout.CENTER);
		Dimension size = getOwner().getSize();
		dialog.setSize(size.width - 20, size.height - 20);
		dialog.setLocationRelativeTo(getOwner());
		GUITools.makeDialogCloseableByEscape(dialog);
		dialog.setVisible(true);
	}

}
