/**
 *  Copyright 2013 by opta data Abrechnungs GmbH
 */
package org.assist.components.report;

import javax.swing.JCheckBox;

import org.assist.constants.GeneralConstants;
import org.assist.domain.ReportMonth;

/**
 * @author k.semlizki created on: 30.08.2013
 */
public class MonthCheckBox extends JCheckBox {
	private final ReportMonth month;

	public MonthCheckBox(ReportMonth month) {
		super(month.toString(),true);
		this.month = month;
		setFont(GeneralConstants.FORM_FONT);
	}

	public ReportMonth getMonth() {
		return month;
	}
}
