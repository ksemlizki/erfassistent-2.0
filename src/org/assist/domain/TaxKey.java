package org.assist.domain;

public class TaxKey extends Domain {

	private String description;

	public TaxKey(int key, String description) {
		setId(Integer.valueOf(key));

		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return getId().toString();
	}

}
