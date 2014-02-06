package com.vertonur.pojo.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.vertonur.pojo.AttachmentInfo.AttachmentType;

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
	private String thumbSuffix;
	private int thumbWidth;
	private int thumbHeight;
	private boolean thumbEnabled;
	private String bcsAccessKey;
	private String bcsSecretKey;
	private String bcsBucket;
	private String bcsHost;
	private String bcsDefaultAvatarUrl;

	private AttachmentType uploadFileSystem;
	private String uploadRoot;
	private String avatarRoot;
	private String defaultAvatarURI;

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

	@Column(name = "ATTM_CNG_BCS_ACS_KY")
	public String getBcsAccessKey() {
		return bcsAccessKey;
	}

	public void setBcsAccessKey(String bcsAccessKey) {
		this.bcsAccessKey = bcsAccessKey;
	}

	@Column(name = "ATTM_CNG_BCS_SCRT_KY")
	public String getBcsSecretKey() {
		return bcsSecretKey;
	}

	public void setBcsSecretKey(String bcsSecretKey) {
		this.bcsSecretKey = bcsSecretKey;
	}

	@Column(name = "ATTM_CNG_BCS_BCKT")
	public String getBcsBucket() {
		return bcsBucket;
	}

	public void setBcsBucket(String bcsBucket) {
		this.bcsBucket = bcsBucket;
	}

	@Column(name = "ATTM_CNG_BCS_HST")
	public String getBcsHost() {
		return bcsHost;
	}

	public void setBcsHost(String bcsHost) {
		this.bcsHost = bcsHost;
	}

	@Column(name = "ATTM_CNG_BCS_DFT_AVTR_URL", length = 600)
	public String getBcsDefaultAvatarUrl() {
		return bcsDefaultAvatarUrl;
	}

	public void setBcsDefaultAvatarUrl(String bcsDefaultAvatarUrl) {
		this.bcsDefaultAvatarUrl = bcsDefaultAvatarUrl;
	}

	@Column(name = "ATTM_CNG_THMB_SFFX")
	public String getThumbSuffix() {
		return thumbSuffix;
	}

	public void setThumbSuffix(String thumbSuffix) {
		this.thumbSuffix = thumbSuffix;
	}

	@Column(name = "ATTM_CNG_UPLD_FL_SYS")
	public AttachmentType getUploadFileSystem() {
		return uploadFileSystem;
	}

	public void setUploadFileSystem(AttachmentType uploadFileSystem) {
		this.uploadFileSystem = uploadFileSystem;
	}

	@Column(name = "ATTM_CNG_AVTR_RT")
	public String getAvatarRoot() {
		return avatarRoot;
	}

	public void setAvatarRoot(String avatarRoot) {
		this.avatarRoot = avatarRoot;
	}

	@Column(name = "ATTM_CNG_DFT_AVTR_URI")
	public String getDefaultAvatarURI() {
		return defaultAvatarURI;
	}

	public void setDefaultAvatarURI(String defaultAvatarURI) {
		this.defaultAvatarURI = defaultAvatarURI;
	}

	@Column(name = "ATTM_CNG_UPLD_RT")
	public String getUploadRoot() {
		return uploadRoot;
	}

	public void setUploadRoot(String uploadRoot) {
		this.uploadRoot = uploadRoot;
	}
}
