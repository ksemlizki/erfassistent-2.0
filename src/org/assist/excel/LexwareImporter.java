package org.assist.excel;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.assist.constants.GeneralConstants;
import org.assist.domain.Data;
import org.assist.tools.Tools;


public class LexwareImporter {
	private File excelFile;

	public LexwareImporter(File excelFile){
		this.excelFile = excelFile;
	}

	public ArrayList<Data> getData()  throws Exception  {
		HSSFWorkbook workbook;
		FileInputStream inputStream;
		inputStream = new FileInputStream(excelFile);
		workbook = new HSSFWorkbook(inputStream);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
		ArrayList<Data> datas = new ArrayList<Data>();
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row;
		Data data;
		String col8;
		String col9;
		for (int rowNr = sheet.getFirstRowNum() + 1; rowNr <= sheet.getLastRowNum(); rowNr++) {
			data = new Data();
			row = sheet.getRow(rowNr);
			GregorianCalendar date = Tools.parseDate((String)getCellValue(row, 0), dateFormat);

			if (date == null) {
				continue;
			}

			data.setDate(date);
			data.setText(((String)getCellValue(row, 2)).replaceAll("Geb\\x3Fren", "Gebühren").replaceAll("Unterst\\x3Fzung", "Unterstützung"));
			data.setAmount((Double)getCellValue(row, 3));
			data.setAccount(Tools.truncDecimal(getCellValue(row, 4)));
			data.setContraAccount(Tools.truncDecimal(getCellValue(row, 6)));
			data.setDebit(Boolean.TRUE);

			col8 = Tools.truncDecimal(getCellValue(row, 8));
			col9 = Tools.truncDecimal(getCellValue(row, 10));

			if (col8.equals("1571")|| col9.equals("1571")){
				data.setTaxKey(GeneralConstants.TAX_KEYS.get("8"));
			}
			else if (col8.equals("1576")|| col9.equals("1576")){
				data.setTaxKey(GeneralConstants.TAX_KEYS.get("9"));
			}
			else if (col8.equals("1771")|| col9.equals("1771")){
				data.setTaxKey(GeneralConstants.TAX_KEYS.get("2"));
			}
			else if (col8.equals("1776")|| col9.equals("1776")){
				data.setTaxKey(GeneralConstants.TAX_KEYS.get("3"));
			}
			else {
				data.setTaxKey(GeneralConstants.TAX_KEYS.get("0"));
			}

			datas.add(data);
		}
		inputStream.close();
		return datas;
	}

	protected Object getCellValue(HSSFRow row, int column) {
		return Tools.getCellValue(row.getCell(row.getFirstCellNum()+ column), false);
	}
}
