package com.vertonur.pojo.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_USR_CNG")
public class UserConfig implements Config {

	private int id;
	private int usrPgnOffset;
	private boolean requireNewUserAuthEmail;
	private boolean notifyAuthorOnNewCmt;
	private boolean registrationEnabled;
	private int avatarWidth;
	private int avatarHeight;
	private int avatarSize;
	
	@Id
	@GeneratedValue
	@Column(name = "USR_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "USR_CNG_PGN_OFT")
	public int getUsrPgnOffset() {
		return usrPgnOffset;
	}

	public void setUsrPgnOffset(int usrPgnOffset) {
		this.usrPgnOffset = usrPgnOffset;
	}
	
	
	@Column(name = "USR_CNG_RQR_AUTH_EML")
	public boolean isRequireNewUserAuthEmail() {
		return requireNewUserAuthEmail;
	}

	public void setRequireNewUserAuthEmail(boolean requireNewUserAuthEmail) {
		this.requireNewUserAuthEmail = requireNewUserAuthEmail;
	}

	@Column(name = "USR_CNG_NTY_USR_NW_CMT")
	public boolean isNotifyAuthorOnNewCmt() {
		return notifyAuthorOnNewCmt;
	}

	public void setNotifyAuthorOnNewCmt(boolean notifyAuthorOnNewCmt) {
		this.notifyAuthorOnNewCmt = notifyAuthorOnNewCmt;
	}
	
	@Column(name = "USR_CNG_AVA_WID")
	public int getAvatarWidth() {
		return avatarWidth;
	}

	public void setAvatarWidth(int avatarWidth) {
		this.avatarWidth = avatarWidth;
	}

	@Column(name = "USR_CNG_AVA_HGT")
	public int getAvatarHeight() {
		return avatarHeight;
	}

	public void setAvatarHeight(int avatarHeight) {
		this.avatarHeight = avatarHeight;
	}

	@Column(name = "USR_CNG_AVA_SIZE")
	public int getAvatarSize() {
		return avatarSize;
	}

	public void setAvatarSize(int avatarSize) {
		this.avatarSize = avatarSize;
	}
	
	@Column(name = "USR_CNG_RGT_ENABLED")
	public boolean isRegistrationEnabled() {
		return registrationEnabled;
	}

	public void setRegistrationEnabled(boolean registrationEnabled) {
		this.registrationEnabled = registrationEnabled;
	}

}
