package com.vertonur.ext.ranking.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_EXT_PNT_CFG")
public class PointConfig {

	private int id;
	private int infoPoints;
	private int cmtPoints;
	private int uploadAttmPoints;

	@Id
	@GeneratedValue
	@Column(name = "PNT_CFG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "PNT_CFG_INF_PNTS")
	public int getInfoPoints() {
		return infoPoints;
	}

	public void setInfoPoints(int infoPoints) {
		this.infoPoints = infoPoints;
	}

	@Column(name = "PNT_CFG_CMT_PNTS")
	public int getCmtPoints() {
		return cmtPoints;
	}

	public void setCmtPoints(int cmtPoints) {
		this.cmtPoints = cmtPoints;
	}

	@Column(name = "PNT_CFG_UPD_ATTM_PNTS")
	public int getUploadAttmPoints() {
		return uploadAttmPoints;
	}

	public void setUploadAttmPoints(int uploadAttmPoints) {
		this.uploadAttmPoints = uploadAttmPoints;
	}
}
