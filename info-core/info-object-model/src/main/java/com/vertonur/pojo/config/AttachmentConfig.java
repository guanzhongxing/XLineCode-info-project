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
	private long maxSize;
	private boolean attmtEnabled;
	private boolean downloadEnabled;
	private String thumbPrefix;
	private int thumbWidth;
	private int thumbHeight;
	private boolean thumbEnabled;
	private String bcsAccessKey;
	private String bcsSecretKey;
	private String bcsBucket;
	private String bcsHost;

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
	public long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(long maxSize) {
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
		return bcsAccessKey;
	}

	public void setBcsAccessKey(String bcsAccessKey) {
		this.bcsAccessKey = bcsAccessKey;
	}

	public String getBcsSecretKey() {
		return bcsSecretKey;
	}

	public void setBcsSecretKey(String bcsSecretKey) {
		this.bcsSecretKey = bcsSecretKey;
	}

	public String getBcsBucket() {
		return bcsBucket;
	}

	public void setBcsBucket(String bcsBucket) {
		this.bcsBucket = bcsBucket;
	}

	public String getBcsHost() {
		return bcsHost;
	}

	public void setBcsHost(String bcsHost) {
		this.bcsHost = bcsHost;
	}

	public String getThumbPrefix() {
		return thumbPrefix;
	}

	public void setThumbPrefix(String thumbPrefix) {
		this.thumbPrefix = thumbPrefix;
	}
}
