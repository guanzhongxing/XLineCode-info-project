package com.vertonur.pojo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class State {
	private boolean deprecated;
	private boolean pending;
	private boolean modified;

	@Column(name = "MSG_DEPRECATED")
	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	@Column(name = "MSG_PENDING")
	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	/**
	 * @return the modified
	 */
	@Column(name = "MSG_MODIFIED")
	public boolean isModified() {
		return modified;
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}
}
