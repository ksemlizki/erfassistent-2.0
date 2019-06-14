package org.assist.domain;

import java.io.Serializable;
import java.util.Date;

public class AccountSheetData implements Serializable {

	private Date docDate;
	private String docNumber;
	private String docText;
	private String taxKey;
	private String account;
	private Double debit;
	private Double credit;
	private Double balance;

	public Date getDocDate() {
		return docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getDocText() {
		return docText;
	}

	public void setDocText(String docText) {
		this.docText = docText;
	}

	public String getTaxKey() {
		return taxKey;
	}

	public void setTaxKey(String taxKey) {
		this.taxKey = taxKey;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

}
