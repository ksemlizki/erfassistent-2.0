package org.assist.constants;

import java.awt.Color;
import java.awt.Font;
import java.util.Comparator;
import java.util.TreeMap;

import org.assist.domain.TaxKey;

public class GeneralConstants {
	public static final String COLUMN_DESCRIPTION = "BEZEICHNUNG";
	public static final Font FORM_FONT = new Font(null, Font.BOLD, 14);
	public static final int DEFAULT_SIZE = 23;
	public static final Color INDICATOR_TF_FAILS_VALUE_BG_COLOR = new Color(255, 255, 102);
	public static final Color BG_COLOR = new Color(232, 238, 244);

	public static final String HEADER = "\"Belegdatum\";\"Buchungsdatum\";\"Belegnummernkreis\";\"Belegnummer\";"
			+ "\"Buchungstext\";\"Buchungsbetrag\";\"Sollkonto\";\"Habenkonto\";\"Steuerschlüssel\";\"Kostenstelle 1\";"
			+ "\"Kostenstelle 2\";\"Buchungsbetrag DM\";\"Buchungsbetrag Euro\";\"Währung\"";


	public static final String DEFAULT_CURRENCY = "EUR";

	public static final TreeMap<String, TaxKey> TAX_KEYS;

	static {
		TAX_KEYS = new TreeMap<String, TaxKey>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
			}
		});

		TAX_KEYS.put("0", new TaxKey(0, "Keine USt"));
		TAX_KEYS.put("2", new TaxKey(2, "USt 7%"));
		TAX_KEYS.put("3", new TaxKey(3, "USt 19%"));
		TAX_KEYS.put("5", new TaxKey(5, "USt 16%"));
		TAX_KEYS.put("7", new TaxKey(7, "VSt 16%"));
		TAX_KEYS.put("8", new TaxKey(8, "VSt 7%"));
		TAX_KEYS.put("9", new TaxKey(9, "VSt 19%"));
		TAX_KEYS.put("20", new TaxKey(20, "Generalumkehr"));
		TAX_KEYS.put("40", new TaxKey(40, "Aufhebung Automatik"));

	}

}
