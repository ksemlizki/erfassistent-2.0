package org.assist.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ReportMonth implements Comparable<ReportMonth> {
	private final SimpleDateFormat monthFormat =  new SimpleDateFormat("MMMM yyyy");
	private Integer month;
	private Integer year;
	private Double startAmount;
	private GregorianCalendar internalValue;

	public ReportMonth(Date date) {
		internalValue = new GregorianCalendar();
		internalValue.setTime(date);
		month = new Integer(internalValue.get(Calendar.MONTH));
		year = new Integer(internalValue.get(Calendar.YEAR));
	}


	@Override
	public boolean equals(Object object) {
		if (object instanceof ReportMonth) {
			ReportMonth reportMonth = (ReportMonth) object;
			return reportMonth.month.equals(month) && reportMonth.year.equals(year);
		}
		return false;
	}

	@Override
	public int compareTo(ReportMonth reportMonth) {
		int result = year.compareTo(reportMonth.year);
		return result == 0 ? month.compareTo(reportMonth.month) : result;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Double getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(Double startAmount) {
		this.startAmount = startAmount;
	}

	@Override
	public String  toString() {
		return monthFormat.format(internalValue.getTime());
	}

}
