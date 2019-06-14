package org.assist.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.assist.constants.GeneralConstants;
import org.assist.domain.AccountSheetData;
import org.assist.domain.Data;
import org.assist.tools.Tools;

public class RepTest {

	public static void main(String[] args) throws Exception {

//		String regExp = "s|S|H|h";
//		String input = "1";
//		System.out.println(Pattern.compile(regExp, Pattern.CASE_INSENSITIVE).matcher(input).matches());



		System.setProperty("http.proxyHost","172.16.0.200");
		System.setProperty("http.proxyPort","8080");
		System.setProperty("http.proxyUser","k.semlizki");
		System.setProperty("http.proxyPassword","Hvost123456");
		System.setProperty("https.proxyHost","172.16.0.200");
		System.setProperty("https.proxyPort","8080");
		System.setProperty("https.proxyUser","k.semlizki");
		System.setProperty("https.proxyPassword","Hvost123456");

		Authenticator.setDefault(new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("k.semlizki", "Hvost123456".toCharArray());
			}
		});


		//Tools.sendEmail("ksemlizki@gmail.com", "k.semlizki@optadata-gruppe.de", "test", "test", null, "text/plain", false);

	}
	public static void main1(String[] args) throws Exception {

		for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				javax.swing.UIManager.setLookAndFeel(info.getClassName());
				break;
			}
		}

		BufferedReader reader = new BufferedReader(new FileReader("E:\\NeuerOrdner\\04.07.txt"));

		ArrayList<Data> datas = new ArrayList<Data>();

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

		String account = "1200";
		boolean isActive = true;

		ArrayList<AccountSheetData> datas2 = new ArrayList<AccountSheetData>();
		double balance = 0;

		for (Data data : datas) {
			AccountSheetData data2 = new AccountSheetData();
			data2.setDocDate(data.getDate().getTime());
			data2.setDocNumber(data.getDocumentNr());
			data2.setDocText(data.getText());
			data2.setTaxKey(data.getTaxKey().getId().toString());

			if (data.getAccount().equals(account)) {
				if ((isActive && data.isDebit().booleanValue()) || (!isActive && !data.isDebit().booleanValue())) {
					data2.setDebit(data.getAmount());
					data2.setCredit(null);
					data2.setAccount(data.getContraAccount());
				}
				else {
					data2.setDebit(null);
					data2.setCredit(data.getAmount());
					data2.setAccount(data.getContraAccount());
				}
			}
			else if (data.getContraAccount().equals(account)){
				if ((isActive && data.isDebit().booleanValue()) || (!isActive && !data.isDebit().booleanValue())) {
					data2.setDebit(null);
					data2.setCredit(data.getAmount());
					data2.setAccount(data.getAccount());
				}
				else {
					data2.setDebit(data.getAmount());
					data2.setCredit(null);
					data2.setAccount(data.getAccount());
				}
			}
			else {
				continue;
			}
			balance += (data2.getDebit() == null ? 0 : data2.getDebit().doubleValue()) - (data2.getCredit() == null ? 0 : data2.getCredit().doubleValue());
			data2.setBalance(Double.valueOf(balance));

			datas2.add(data2);

		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("REPORT_FILE_RESOLVER", new FileResolver() {
		//
		// @Override
		// public File resolveFile(String fileName) {
		// return new File(getServletContext().getRealPath(fileName));
		// }
		// });
		parameters.put("ReportTitle", "firma X Konto "+account);
		parameters.put("StartAmount", datas2.get(0).getBalance());

		String path = System.getProperty("user.dir") + "\\reports\\AccountSheet.jrxml";
		System.out.println(path);
		//JasperReport report = JasperCompileManager.compileReport(path);
		//JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(datas2, false));


	}
}
