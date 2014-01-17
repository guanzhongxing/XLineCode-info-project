package com.vertonur.pojo.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_ATTM_CNG")
public class AttachmentConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6700407284621032978L;

	public static enum AttmCngType {
		SYS, USR
	}

	private int id;
	private int maxAttmtNum;
	private int maxSize;
	private boolean attmtEnabled;
	private boolean downloadEnabled;
	private int thumbWidth;
	private int thumbHeight;
	private boolean thumbEnabled;
	private String BcsAccessKey;
	private String BcsSecretKey;
	private String BcsBucket;
	private String BcsHost;

	private AttmCngType configTyp;

	public AttachmentConfig() {
	}

	@Id
	@GeneratedValue
	@Column(name = "ATTM_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "ATTM_CNG_MAX_ATTMT_NUM")
	public int getMaxAttmtNum() {
		return maxAttmtNum;
	}

	public void setMaxAttmtNum(int maxAttmtNum) {
		this.maxAttmtNum = maxAttmtNum;
	}

	@Column(name = "ATTM_CNG_MAX_SIZE")
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	@Column(name = "ATTM_CNG_ATTMT_ENB")
	public boolean isAttmtEnabled() {
		return attmtEnabled;
	}

	public void setAttmtEnabled(boolean attmtEnabled) {
		this.attmtEnabled = attmtEnabled;
	}

	@Column(name = "ATTM_CNG_DWNLD_ENB")
	public boolean isDownloadEnabled() {
		return downloadEnabled;
	}

	public void setDownloadEnabled(boolean downloadEnabled) {
		this.downloadEnabled = downloadEnabled;
	}

	@Column(name = "ATTM_TMB_WID")
	public int getThumbWidth() {
		return thumbWidth;
	}

	public void setThumbWidth(int thumbWidth) {
		this.thumbWidth = thumbWidth;
	}

	@Column(name = "ATTM_TMB_HGH")
	public int getThumbHeight() {
		return thumbHeight;
	}

	public void setThumbHeight(int thumbHeight) {
		this.thumbHeight = thumbHeight;
	}

	@Column(name = "ATTM_TMB_ENB")
	public boolean getThumbEnabled() {
		return thumbEnabled;
	}

	public void setThumbEnabled(boolean thumbEnabled) {
		this.thumbEnabled = thumbEnabled;
	}

	@Column(name = "ATTM_CNG_CNG_TYP")
	public AttmCngType getConfigTyp() {
		return configTyp;
	}

	public void setConfigTyp(AttmCngType configTyp) {
		this.configTyp = configTyp;
	}

	public String getBcsAccessKey() {
		return BcsAccessKey;
	}

	public void setBcsAccessKey(String bcsAccessKey) {
		BcsAccessKey = bcsAccessKey;
	}

	public String getBcsSecretKey() {
		return BcsSecretKey;
	}

	public void setBcsSecretKey(String bcsSecretKey) {
		BcsSecretKey = bcsSecretKey;
	}

	public String getBcsBucket() {
		return BcsBucket;
	}

	public void setBcsBucket(String bcsBucket) {
		BcsBucket = bcsBucket;
	}

	public String getBcsHost() {
		return BcsHost;
	}

	public void setBcsHost(String bcsHost) {
		BcsHost = bcsHost;
	}
}
