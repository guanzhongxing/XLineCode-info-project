package com.vertonur.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity(name = "INFO_COR_USR_PRES")
public class UserPreferences implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private byte showEmailAddr = 0;
	private byte hideOnlineStatus = 0;
	private byte attachSignature = 0;

	private String locale;
	private User user;

	public UserPreferences() {
	}

	@Id
	@GeneratedValue
	@Column(name = "USR_PRES_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "USR_PRES_SHOW_EML")
	public byte getShowEmailAddr() {
		return showEmailAddr;
	}

	public void setShowEmailAddr(byte showEmailAddr) {
		this.showEmailAddr = showEmailAddr;
	}

	@Column(name = "USR_PRES_HIDE_ONLINE_STUS")
	public byte getHideOnlineStatus() {
		return hideOnlineStatus;
	}

	public void setHideOnlineStatus(byte hideOnlineStatus) {
		this.hideOnlineStatus = hideOnlineStatus;
	}

	@Column(name = "USR_PRES_ATTACH_SIGNATURE")
	public byte getAttachSignature() {
		return attachSignature;
	}

	public void setAttachSignature(byte attachSignature) {
		this.attachSignature = attachSignature;
	}

	@Column(name = "USR_PRES_LOCALE")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@OneToOne(mappedBy = "userPres")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}