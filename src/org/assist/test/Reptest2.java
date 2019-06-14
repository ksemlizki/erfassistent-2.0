package org.assist.test;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import org.assist.constants.GeneralConstants;
import org.assist.constants.ImageConstants;
import org.assist.domain.CashBookData;
import org.assist.domain.Data;
import org.assist.domain.ReportMonth;
import org.assist.tools.Tools;

public class Reptest2 {

	public static void main(String[] args) throws Exception {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				UIManager.setLookAndFeel(info.getClassName());
				break;
			}
		}

		BufferedReader reader = new BufferedReader(new FileReader("D:/Apps/2010-9.txt"));

		ArrayList<Data> datas = new ArrayList<Data>();
		Double startAmount = new Double(1000);
		String str;
		String strings[];
		ArrayList<String> errors = new ArrayList<String>();
		for (int i = 0; reader.ready(); i++) {
			str = reader.readLine();
			if (str.startsWith("\"")) {
				continue;
			}

			strings = str.split(";");
			Data data = new Data();
			try {
				data.setDate(Tools.parseDate(strings[0]));
				System.out.println(data.getDate().getTime());
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


		TreeMap<ReportMonth, ArrayList< CashBookData>> cashBookDatas = new TreeMap<ReportMonth, ArrayList<CashBookData>>();
		String account = "1000";
		double balance = startAmount.doubleValue();
		for (Data data : datas) {
			CashBookData data2 = new CashBookData();
			data2.setDate(data.getDate().getTime());
			data2.setAccountNr(data.getDocumentNr());
			data2.setText(data.getText());

			if (data.getAccount().equals(account)) {
				if (data.isDebit().booleanValue()) {
					data2.setIncome(data.getAmount());
					data2.setExpence(null);
				}
				else {
					data2.setIncome(null);
					data2.setExpence(data.getAmount());
				}
			}
			else if (data.getContraAccount().equals(account)) {
				if (data.isDebit().booleanValue()) {
					data2.setIncome(null);
					data2.setExpence(data.getAmount());
				}
				else {
					data2.setIncome(data.getAmount());
					data2.setExpence(null);
				}
			}
			else {
				continue;
			}
			balance += (data2.getIncome() == null ? 0 : data2.getIncome().doubleValue()) - (data2.getExpence() == null ? 0 : data2.getExpence().doubleValue());
			data2.setBalance(Double.valueOf(balance));

			ReportMonth reportMonth = new ReportMonth(data2.getDate());
			ArrayList<CashBookData> list = cashBookDatas.get(reportMonth);
			if (list == null) {
				list = new ArrayList<CashBookData>();
				cashBookDatas.put(reportMonth, list);
			}
			list.add(data2);
		}

		ArrayList<CashBookData> completeList = new ArrayList<CashBookData>();

		Set<ReportMonth> monthSet = cashBookDatas.keySet();
		for (Iterator<ReportMonth> iterator = monthSet.iterator(); iterator.hasNext();) {
			ReportMonth reportMonth = iterator.next();
			reportMonth.setStartAmount(startAmount);
			ArrayList<CashBookData> arrayList = cashBookDatas.get(reportMonth);
			for (CashBookData cashBookData : arrayList) {
				cashBookData.getMonth().setStartAmount(startAmount);
				completeList.add(cashBookData);
			}

			startAmount = arrayList.get(arrayList.size() - 1).getBalance();
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("firmName", "J&S");

		String path = System.getProperty("user.dir") + "\\reports\\CashBookSubReport.jrxml";
		JasperReport report = JasperCompileManager.compileReport(path);
		JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(completeList, false));


		ArrayList<ReportMonth>  vector = new ArrayList<ReportMonth>(monthSet);



		JasperViewer viewer = new JasperViewer(print);
		viewer.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
		viewer.setTitle("Kassenbericht J&S " +vector.get(0) +" - "+ vector.get(vector.size() - 1));
		viewer.setIconImage(ImageConstants.CASHBOOK.getImage());
		viewer.setVisible(true);
		viewer.setFitPageZoomRatio();

	}
}
