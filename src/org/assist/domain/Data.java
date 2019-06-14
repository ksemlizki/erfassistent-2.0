package org.assist.domain;

import java.util.GregorianCalendar;

import org.assist.constants.GeneralConstants;

public class Data extends Domain {

	private Double amount;
	private Boolean debit;
	private String contraAccount;
	private String account;
	private TaxKey taxKey;
	private String documentNr;
	private GregorianCalendar date;
	private String text;
	private Double balance;

	private boolean marked;
	private boolean editable;
	private Integer order;
	private Data linkedData;

	public Data() {
		this(true);
	}

	public Data(boolean editable) {
		date = new GregorianCalendar();
		text = "";
		taxKey = GeneralConstants.TAX_KEYS.get("0");
		debit = Boolean.FALSE;
		marked = false;
		this.editable = editable;
	}



	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Boolean isDebit() {
		return debit;
	}

	public void setDebit(Boolean debit) {
		this.debit = debit;
	}

	public String getContraAccount() {
		return contraAccount;
	}

	public void setContraAccount(String contraAccount) {
		this.contraAccount = contraAccount;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public TaxKey getTaxKey() {
		return taxKey;
	}

	public void setTaxKey(TaxKey taxKey) {
		this.taxKey = taxKey;
	}

	public String getDocumentNr() {
		return documentNr;
	}

	public void setDocumentNr(String documentNr) {
		this.documentNr = documentNr;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Object[] toObjectArray() {
		return new Object[] { amount, debit, contraAccount, account, taxKey, documentNr, date, text };
	}


	public Data getLinkedData() {
		return linkedData;
	}


	public void setLinkedData(Data linkedData) {
		this.linkedData = linkedData;
	}


	public boolean isEditable() {
		return editable;
	}


	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
