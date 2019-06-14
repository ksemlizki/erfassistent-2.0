package org.assist.domain;

import java.io.Serializable;

public abstract class Domain implements Serializable, Comparable<Domain> {

	private Integer id;

	public Domain() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isNew() {
		return id == null;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (getClass().isInstance(obj)) {
			Domain domain = (Domain)obj;
			result = ((isNew() && domain.isNew()) ? false : (compareTo(domain)) == 0);
		}

		return result;
	}

	@Override
	public int compareTo(Domain domain) {
		if ((domain != null) && getClass().isInstance(domain) && !isNew() && !domain.isNew()) {
			return id.compareTo(domain.getId());
		}

		return -1;
	}

}
