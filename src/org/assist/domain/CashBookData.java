package org.assist.domain;

import java.util.Date;

public class CashBookData {
	private ReportMonth month;
	private Date date;
	private String accountNr;
	private String text;
	private Double income;
	private Double expence;
	private Double balance;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		month = new ReportMonth(date);
	}

	public String getAccountNr() {
		return accountNr;
	}

	public void setAccountNr(String accountNr) {
		this.accountNr = accountNr;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Double getIncome() {
		return income;
	}

	public void setIncome(Double income) {
		this.income = income;
	}

	public Double getExpence() {
		return expence;
	}

	public void setExpence(Double expence) {
		this.expence = expence;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public ReportMonth getMonth() {
		return month;
	}

	public void setMonth(ReportMonth month) {
		this.month = month;
	}

}
