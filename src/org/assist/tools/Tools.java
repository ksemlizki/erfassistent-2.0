package org.assist.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.JLabel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.assist.components.common.ComboBox;
import org.assist.components.table.TablePanel;
import org.assist.constants.GeneralConstants;
import org.assist.domain.Data;
import org.assist.domain.TaxKey;

public class Tools {

	private static Logger logger = null;

	public static final String DEFAULT_PATTERN = "dd.MM.yyyy";
	public static final NumberFormat NUMBER_FORMAT = new DecimalFormat("##0.00");
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,##0.00;-###,##0.00");


//	public static final String MAIL_SMTP_HOST = "mail.smtp.host";
//	public static final String MAIL_SMTP_USERNAME = "mail.smtp.username";
//	public static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
//	public static final String MAIL_FROM = "mail.from";
//	public static final String MAIL_SMTP_TRANSPORT = "smtp";
//	public static final String MAIL_POP3_TRANSPORT = "pop3";
//	public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
//	public static final String MAIL_POP3_TIMEOUT = "mail.pop3.timeout";
//	public static final int SMTP_PORT = 25;

	static {
		try {
			if (logger == null) {
				logger = Logger.getLogger("ErfAssistent");
				FileHandler handler = new FileHandler("log\\kasse%u.log");
				handler.setFormatter(new SimpleFormatter());
				logger.addHandler(new StreamHandler());
				logger.addHandler(handler);
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	public static <T> String join(T[] objects) {
		return join(new ArrayList<T>(Arrays.asList(objects)));
	}

	public static String join(ArrayList<?> objects) {
		return join(objects, ", ");
	}

	public static <T> String join(T[] objects, String separator) {
		return join(new ArrayList<T>(Arrays.asList(objects)), separator);
	}

	public static String join(ArrayList<?> objects, String separator) {
		StringBuilder joinedStr = new StringBuilder();
		for (int i = 0; i < objects.size(); i++) {
			if (i > 0) {
				joinedStr.append(separator);
			}
			joinedStr.append(objects.get(i));
		}

		return joinedStr.toString();
	}

	public static boolean existValueInArray(int value, int[] array) {
		for (int arrayValue : array) {
			if (arrayValue == value) {
				return true;
			}
		}

		return false;
	}

	public static boolean existObjectInArray(Object searchObject, Object[] objectArr) {
		for (Object object : objectArr) {
			if (searchObject.equals(object)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Prüft ob string == null oder leer
	 **/
	public static boolean isStringEmpty(String string) {
		return (string == null) || string.isEmpty();
	}

	/**
	 * Prüft ob list == null oder leer
	 **/
	public static boolean isCollectionEmpty(Collection<?> list) {
		return (list == null) || list.isEmpty();
	}

	/**
	 * Prüft ob array == null oder leer
	 **/
	public static <T> boolean isArrayEmpty(T[] array) {
		return (array == null) || (array.length == 0);
	}

	/**
	 * Prüft ob array == null oder leer
	 **/
	public static boolean isArrayEmpty(int[] array) {
		return (array == null) || (array.length == 0);
	}

	public static boolean isOneOfObjectsNull(Object... objects) {
		for (Object object : objects) {
			if (object == null) {
				return true;
			}
		}

		return false;
	}

	public static boolean compareByteArrays(byte[] array1, byte[] array2) {
		boolean array1Empty = (array1 == null) || (array1.length == 0);
		boolean array2Empty = (array2 == null) || (array2.length == 0);

		if (array1Empty != array2Empty) {
			return false;
		}

		if ((array1 != null) && (array2 != null)) {
			if (array1.length != array2.length) {
				return false;
			}

			for (int i = 0; i < array1.length; i++) {
				if (array1[i] != array2[i]) {
					return false;
				}
			}
		}

		return true;
	}

	public static String replaceSpecialChars(String text) {
		return text.replaceAll("Ä", "Ae").replaceAll("ä", "ae").replaceAll("Ö", "Oe").replaceAll("ö", "oe").replaceAll("Ü", "Ue").replaceAll("ü", "ue").replaceAll("ß", "ss");
	}

	public static <T> ArrayList<T> createOneElementList(T t) {
		return (t == null) ? new ArrayList<T>() : new ArrayList<T>(Collections.singleton(t));
	}

	public static <K, V> Map<K, V> createOneElementMap(K k, V v) {
		return (k == null) ? new HashMap<K, V>() : new HashMap<K, V>(Collections.singletonMap(k, v));
	}

	public static String getFirstNotEmptyString(String... strings) {
		for (String string : strings) {
			if (!isStringEmpty(string)) {
				return string;
			}
		}

		return "";
	}

	@SafeVarargs
	public static <T> T getFirstNotNullElement(T... elements) {
		for (T element : elements) {
			if (element != null) {
				return element;
			}
		}

		return null;
	}

	public static <T> T getFirstNotNullElement(Collection<T> elements) {
		for (T element : elements) {
			if (element != null) {
				return element;
			}
		}

		return null;
	}

	/**
	 * Kopiert src nach dest und führt bei BEIDEN close() aus.
	 **/
	public static void copyStream(InputStream src, OutputStream dest) throws Exception {
		try {
			byte[] buffer = new byte[16384];
			int lenght;
			while ((lenght = src.read(buffer)) > 0) {
				dest.write(buffer, 0, lenght);
			}
			dest.flush();
		} finally {
			src.close();
			dest.close();
		}
	}

	public static String capitalizeString(String text) {
		if (text.length() == 0) {
			return text;
		}

		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	public static String generateRandomString(int length) {
		Random random = new Random();
		String result = "";
		// unverwechselbare Zeichen
		String validChars = "23456789ABCDEFGHJKLMNPRSTUVWXYZabcdefghjkpqstuvwxyz";

		for (int i = 0; i < length; i++) {
			result += validChars.charAt(random.nextInt(validChars.length()));
		}

		return result;
	}

	public static int[] listToIntArray(ArrayList<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i).intValue();
		}

		return array;
	}

	public static ArrayList<Integer> intArrayToList(int... array) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int value : array) {
			list.add(new Integer(value));
		}

		return list;
	}

	public static <T extends Comparable<?>> ArrayList<T> distinctResults(Collection<T> source) {
		return new ArrayList<T>(new TreeSet<T>(source));
	}

	public static String completeString(String text, String... args) {
		String completedString = text;

		for (int i = 0; i < args.length; i++) {
			completedString = completedString.replaceAll("%\\{" + (i + 1) + "\\}", args[i]);
		}

		return completedString;
	}

	public static <T> boolean contains(T object, T[] objects) {
		for (T obj : objects) {
			if (obj.equals(object)) {
				return true;
			}
		}

		return false;
	}

	public static boolean contains(int value, int[] array) {
		for (int i : array) {
			if (i == value) {
				return true;
			}
		}

		return false;
	}

	public static <T> int indexOf(T object, T[] objects) {
		for (int i = 0; i < objects.length; i++) {
			if ((objects[i] != null) && objects[i].equals(object)) {
				return i;
			}
		}

		return -1;
	}

	public static void addToDate(Date date, int field, int value) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(field, value);
		date.setTime(calendar.getTimeInMillis());
	}

	public static GregorianCalendar parseDate(String value) {
		return parseDate(value, new SimpleDateFormat(DEFAULT_PATTERN));
	}

	public static GregorianCalendar parseDate(String value, SimpleDateFormat dateFormat) {
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(dateFormat.parse(value));

			return calendar;
		} catch (ParseException exception) {
			return null;
		}
	}

	public static void log(Level level, String msg) {
		logger.log(level, msg);

	}

	public static void log(Throwable throwable) {
		logger.log(Level.SEVERE, "Fehler", throwable);
	}

	public static String format(Double value) {
		return NUMBER_FORMAT.format(value == null ? 0 : value.doubleValue());
	}

	public static String decimalFormat(Double value) {
		return DECIMAL_FORMAT.format(value == null ? 0 : value.doubleValue());
	}

	public static String format(GregorianCalendar value) {
		return value == null ? "" : new SimpleDateFormat(DEFAULT_PATTERN).format(value.getTime());
	}

	public static String format(Date value) {
		return value == null ? "" : new SimpleDateFormat(DEFAULT_PATTERN).format(value);
	}

	public static String removeQuote(String string) {
		return (string == null) ? "" : string.replace('\"', ' ').trim();
	}

	public static Double parseDouble(String string) {
		try {
			if ((string != null) && (string.trim().length() > 0)) {
				NumberFormat format = string.contains(".") ? DECIMAL_FORMAT : NUMBER_FORMAT;
				return Double.valueOf(format.parse(string).doubleValue());
			}
		}
		catch (ParseException exception) {
			Tools.log(exception);
		}

		return Double.valueOf(0);
	}

	public static String getBalanceString(String account, TablePanel tablePanel) {
		double balance = 0;
		for (int row = 0; row < tablePanel.getTable().getRowCount(); row++) {
			Data data = tablePanel.getData(row);

			if (data.getAccount().equals(account)) {
				if (data.isDebit().booleanValue()) {
					balance +=data.getAmount().doubleValue();
				}
				else {
					balance -=data.getAmount().doubleValue();
				}
			}
			else if (data.getContraAccount().equals(account)){
				if (data.isDebit().booleanValue()) {
					balance -=data.getAmount().doubleValue();
				}
				else {
					balance +=data.getAmount().doubleValue();
				}
			}
		}

		return DECIMAL_FORMAT.format(balance);
	}

	public static String removeDangerousChars(String text) {
		String newText = text.replace(';', ',');
		newText = newText.replace('\n', '\t');
		return newText;
	}

	public static Object getCellValue(HSSFCell cell) {
		return Tools.getCellValue(cell, false);
	}

	public static Object getCellValue(HSSFCell cell, boolean isDate) {
		if (cell != null) {
			if (isDate) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(cell.getDateCellValue());
				return calendar;
			}
			int cellType = cell.getCellType();
			if (cellType == Cell.CELL_TYPE_FORMULA){
				cellType = cell.getCachedFormulaResultType();
			}
			switch (cellType) {
				case Cell.CELL_TYPE_BOOLEAN:
					return String.valueOf(cell.getBooleanCellValue());
				case Cell.CELL_TYPE_NUMERIC:
					return new Double(cell.getNumericCellValue());
//				case Cell.CELL_TYPE_FORMULA:
//					String strValue = cell.getStringCellValue();
//					int
//					switch (cellType) {
//						case Cell.CELL_TYPE_NUMERIC:
//							double value = cell.getNumericCellValue();
//							if (!Double.isNaN(value) && !Double.isInfinite(value)) {
//								return new Double(value);
//							}
//							break;
//						case Cell.CELL_TYPE_STRING:
//							return cell.getStringCellValue();
//
//						default:
//							break;
//					}
//					/*if (strValue.equals("")) {
//						double value = cell.getNumericCellValue();
//						if (!Double.isNaN(value) && !Double.isInfinite(value)) {
//							return new Double(value);
//						}
//
//					}*/
//					return strValue;
				case Cell.CELL_TYPE_STRING:
					return cell.getStringCellValue();

				default:
					return "";

			}
		}
		return "";
	}


	public static String truncDecimal(Object value) {
		if (value instanceof String) {
			String string = (String)value;
			int index = string.indexOf(".");
			if (index != -1) {
				return string.substring(0, index);
			}
			return string;
		}
		else if (value instanceof Number) {
			Number number = (Number)value;

			return String.valueOf(Math.round(number.doubleValue()));
		}

		return value == null ? "" : value.toString();
	}

	public static ComboBox<String> createMonthComboBox() {
		String[] month = { "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" };
		return new ComboBox<String>(month);
	}

	public static ComboBox<Integer> createYearComboBox() {
		Integer[] years = new Integer[15];
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < years.length; i++) {
			years[i] = new Integer(((curYear + i) - years.length) + 1);
		}

		return new ComboBox<Integer>(years);
	}

	public static JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(GeneralConstants.FORM_FONT);

		return label;
	}

	public static TaxKey getAutoTaxKey(String contraAccount, String account) {

		if (account.equals("8400") || contraAccount.equals("8400")) {
			return (GeneralConstants.TAX_KEYS.get("3"));
		}
		else if (account.equals("8300") || contraAccount.equals("8300")) {
			return (GeneralConstants.TAX_KEYS.get("2"));
		}
		else if (account.equals("8921") || contraAccount.equals("8921")) {
			return (GeneralConstants.TAX_KEYS.get("3"));
		}
		else if (account.equals("8922") || contraAccount.equals("8922")) {
			return (GeneralConstants.TAX_KEYS.get("3"));
		}
		else if (account.equals("8910") || contraAccount.equals("8910")) {
			return (GeneralConstants.TAX_KEYS.get("3"));
		}
		else if (account.equals("8915") || contraAccount.equals("8915")) {
			return (GeneralConstants.TAX_KEYS.get("3"));
		}
		else if (account.equals("8508") || contraAccount.equals("8508")) {
			return (GeneralConstants.TAX_KEYS.get("5"));
		}
		else if (account.equals("8519") || contraAccount.equals("8519")) {
			return (GeneralConstants.TAX_KEYS.get("3"));
		}
		else if (account.equals("8150") || contraAccount.equals("8150")) {
			return (GeneralConstants.TAX_KEYS.get("40"));
		}
		else if (account.equals("8120") || contraAccount.equals("8120")) {
			return (GeneralConstants.TAX_KEYS.get("40"));
		}
		else if (account.equals("8125") || contraAccount.equals("8125")) {
			return (GeneralConstants.TAX_KEYS.get("40"));
		}
		else if (account.equals("8505") || contraAccount.equals("8505")) {
			return (GeneralConstants.TAX_KEYS.get("40"));
		}
		else if (account.equals("3300") || contraAccount.equals("3300")) {
			return (GeneralConstants.TAX_KEYS.get("8"));
		}
		else if (account.equals("3400") || contraAccount.equals("3400")) {
			return (GeneralConstants.TAX_KEYS.get("9"));
		}
		else if (account.equals("3400") || contraAccount.equals("3400")) {
			return (GeneralConstants.TAX_KEYS.get("9"));
		}
		else if (account.equals("8924") || contraAccount.equals("8924")) {
			return (GeneralConstants.TAX_KEYS.get("0"));
		}
		else if (account.equals("8905") || contraAccount.equals("8905")) {
			return (GeneralConstants.TAX_KEYS.get("0"));
		}
		else if ((account.length() == 4) && account.startsWith("18")
				&& (contraAccount.equals("1000") || contraAccount.equals("1200") || contraAccount.equals("1390"))) {
			return (GeneralConstants.TAX_KEYS.get("0"));
		}
		else if ((contraAccount.length() == 4) && contraAccount.startsWith("18")
				&& (account.equals("1000") || account.equals("1200") || account.equals("1390"))) {
			return (GeneralConstants.TAX_KEYS.get("0"));
		}
		else if (account.equals("8340") || contraAccount.equals("8340")) {
			return (GeneralConstants.TAX_KEYS.get("5"));
		}
		else if (account.equals("3340") || contraAccount.equals("3340")) {
			return (GeneralConstants.TAX_KEYS.get("7"));
		}

		return null;
	}





}
