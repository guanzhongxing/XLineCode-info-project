package com.vertonur.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractUserReadInfo {

	private int id;
	private User reader;
	private Date readDate;

	protected AbstractUserReadInfo() {
	}

	protected AbstractUserReadInfo(User reader, Date readDate) {
		this.reader = reader;
		this.readDate = readDate;
	}

	@Id
	@GeneratedValue
	@Column(name = "USER_READ_INFO_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false, updatable = false)
	public User getReader() {
		return reader;
	}

	public void setReader(User reader) {
		this.reader = reader;
	}

	@Column(name = "USER_READ_DATE", nullable = false, updatable = false)
	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
}
