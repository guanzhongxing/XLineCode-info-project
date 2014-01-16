package com.vertonur.pojo.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_CMT_CNG")
public class CommentConfig implements Config{

	private int id;
	private int cmtPgnOffset;
	private long newCmtInterval;
	
	private int usrSpfdCmtPgnOffset;
	
	@Id
	@GeneratedValue
	@Column(name = "CMT_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "CMT_CNG_PGN_OFT")
	public int getCmtPgnOffset() {
		return cmtPgnOffset;
	}

	public void setCmtPgnOffset(int cmtPgnOffset) {
		this.cmtPgnOffset = cmtPgnOffset;
	}

	@Column(name = "CMT_CNG_NW_IVL")
	public long getNewCmtInterval() {
		return newCmtInterval;
	}

	public void setNewCmtInterval(long newCmtInterval) {
		this.newCmtInterval = newCmtInterval;
	}

	@Column(name = "CMT_CNG_USR_SPD_PGN_OFT")
	public int getUsrSpfdCmtPgnOffset() {
		return usrSpfdCmtPgnOffset;
	}

	public void setUsrSpfdCmtPgnOffset(int usrSpfdCmtPgnOffset) {
		this.usrSpfdCmtPgnOffset = usrSpfdCmtPgnOffset;
	}
}
