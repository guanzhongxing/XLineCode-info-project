package com.vertonur.pojo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "INFO_COR_USR_READ_INFO")
public class UserReadInfo extends AbstractUserReadInfo {

	private Info readInfo;

	public UserReadInfo() {
	}

	public UserReadInfo(User reader, Info readInfo, Date readDate) {
		super(reader, readDate);
		this.readInfo = readInfo;
	}

	@ManyToOne
	@JoinColumn(name = "INFO_ID", nullable = false, updatable = false)
	public Info getReadInfo() {
		return readInfo;
	}

	public void setReadInfo(Info readInfo) {
		this.readInfo = readInfo;
	}
}
