package com.vertonur.pojo.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_INFO_CNG")
public class InfoConfig implements Config{
	
	private int id;
	private int infoPgnOffset;
	private long newInfoInterval;
	
	private int newInfoInHours;
	
	private int recentInfoPgnOffset;
	private int recentInfoInDays;

	private int hottestInfoPgnOffset;
	private int hottestInfoGeCmts;

	private int usrSpfdInfoPgnOffset;

	private int categoryInfoCacheNum;
	
	@Id
	@GeneratedValue
	@Column(name = "INFO_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "INFO_CNG_PGN_OFT")
	public int getInfoPgnOffset() {
		return infoPgnOffset;
	}

	public void setInfoPgnOffset(int infoPgnOffset) {
		this.infoPgnOffset = infoPgnOffset;
	}

	@Column(name = "INFO_CNG_NEW_INFO_IVL")
	public long getNewInfoInterval() {
		return newInfoInterval;
	}

	public void setNewInfoInterval(long newInfoInterval) {
		this.newInfoInterval = newInfoInterval;
	}

	@Column(name = "INFO_CNG_NEW_INFO_IN_HRS")
	public int getNewInfoInHours() {
		return newInfoInHours;
	}

	public void setNewInfoInHours(int newInfoInHours) {
		this.newInfoInHours = newInfoInHours;
	}

	@Column(name = "INFO_CNG_RCT_INFO_PGN_OFT")
	public int getRecentInfoPgnOffset() {
		return recentInfoPgnOffset;
	}

	public void setRecentInfoPgnOffset(int recentInfoPgnOffset) {
		this.recentInfoPgnOffset = recentInfoPgnOffset;
	}

	@Column(name = "INFO_CNG_RCT_IN_DAYS")
	public int getRecentInfoInDays() {
		return recentInfoInDays;
	}

	public void setRecentInfoInDays(int recentInfoInDays) {
		this.recentInfoInDays = recentInfoInDays;
	}

	@Column(name = "INFO_CNG_HOT_PGN_OFT")
	public int getHottestInfoPgnOffset() {
		return hottestInfoPgnOffset;
	}

	public void setHottestInfoPgnOffset(int hottestInfoPgnOffset) {
		this.hottestInfoPgnOffset = hottestInfoPgnOffset;
	}

	@Column(name = "INFO_CNG_HOT_GE_CMTS")
	public int getHottestInfoGeCmts() {
		return hottestInfoGeCmts;
	}

	public void setHottestInfoGeCmts(int hottestInfoGeCmts) {
		this.hottestInfoGeCmts = hottestInfoGeCmts;
	}

	@Column(name = "INFO_CNG_USR_SPD_PGN_OFT")
	public int getUsrSpfdInfoPgnOffset() {
		return usrSpfdInfoPgnOffset;
	}

	public void setUsrSpfdInfoPgnOffset(int usrSpfdInfoPgnOffset) {
		this.usrSpfdInfoPgnOffset = usrSpfdInfoPgnOffset;
	}
	
	@Column(name = "INFO_CNG_CAT_INFO_CHE_NUM")
	public int getCategoryInfoCacheNum() {
		return categoryInfoCacheNum;
	}
	public void setCategoryInfoCacheNum(int categoryInfoCacheNum) {
		this.categoryInfoCacheNum = categoryInfoCacheNum;
	}
}
