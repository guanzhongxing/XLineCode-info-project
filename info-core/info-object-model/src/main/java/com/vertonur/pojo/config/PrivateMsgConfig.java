package com.vertonur.pojo.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_PM_CNG")
public class PrivateMsgConfig implements Config {

	private int id;
	private int privateMsgPgnOffset;
	private int newPrivateMsgNum;
	private int newPrivateMsgInDays;

	@Id
	@GeneratedValue
	@Column(name = "PM_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "PM_CNG_PNG_OFT")
	public int getPrivateMsgPgnOffset() {
		return privateMsgPgnOffset;
	}

	public void setPrivateMsgPgnOffset(int privateMsgPgnOffset) {
		this.privateMsgPgnOffset = privateMsgPgnOffset;
	}

	@Column(name = "PM_CNG_NW_NUM")
	public int getNewPrivateMsgNum() {
		return newPrivateMsgNum;
	}

	public void setNewPrivateMsgNum(int newPrivateMsgNum) {
		this.newPrivateMsgNum = newPrivateMsgNum;
	}

	@Column(name = "PM_CNG_NW_IN_DAYS")
	public int getNewPrivateMsgInDays() {
		return newPrivateMsgInDays;
	}

	public void setNewPrivateMsgInDays(int newPrivateMsgInDays) {
		this.newPrivateMsgInDays = newPrivateMsgInDays;
	}

}
