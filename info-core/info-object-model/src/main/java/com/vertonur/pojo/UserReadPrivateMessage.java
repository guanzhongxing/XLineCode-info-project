package com.vertonur.pojo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "INFO_COR_USR_READ_PM")
public class UserReadPrivateMessage extends AbstractUserReadInfo {

	private PrivateMessage readPm;

	public UserReadPrivateMessage() {
	}

	public UserReadPrivateMessage(User reader, PrivateMessage readPm,
			Date readDate) {
		super(reader, readDate);
		this.readPm = readPm;
	}

	@ManyToOne
	@JoinColumn(name = "PM_ID", nullable = false, updatable = false)
	public PrivateMessage getReadPm() {
		return readPm;
	}

	public void setReadPm(PrivateMessage readPm) {
		this.readPm = readPm;
	}
}
