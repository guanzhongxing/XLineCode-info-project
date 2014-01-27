package com.vertonur.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity(name = "INFO_COR_ATTM_INFO")
public class AttachmentInfo {

	public static enum AttachmentType {
		LOCAL, BCS
	}

	public static enum FileType {
		EMBEDDED_IMAGE, FILE
	}

	private int id;
	private int downloadCount = 0;
	private String fileName;
	private String filePath;
	private String downloadUrl;
	private String comment;
	private String mimeType;
	private long uploadTimeInMillis;
	private long filesize;
	private Date uploadTime;
	private boolean hasThumb = false;
	private boolean uploadConfirmed = false;
	private AttachmentType attachmentType;
	private FileType fileType = FileType.FILE;

	private Attachment attm;

	@Id
	@GeneratedValue
	@Column(name = "ATTM_INFO_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "ATTM_INFO_DWN_COUNT")
	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public void increaseDownloadCountByOne() {
		++downloadCount;
	}

	@Column(name = "ATTM_INFO_FILE_NME")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "ATTM_INFO_FILE_PTH")
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Column(name = "ATTM_INFO_CMT")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "ATTM_INFO_MIME_TYP")
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Column(name = "ATTM_INFO_TIME_IN_MILLS")
	public long getUploadTimeInMillis() {
		return uploadTimeInMillis;
	}

	public void setUploadTimeInMillis(long uploadTimeInMillis) {
		this.uploadTimeInMillis = uploadTimeInMillis;
	}

	@Column(name = "ATTM_INFO_FILE_SIZE")
	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	@Column(name = "ATTM_INFO_UPLOAD_TIME")
	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	@Column(name = "ATTM_INFO_HAS_THUMB")
	public boolean isHasThumb() {
		return hasThumb;
	}

	public void setHasThumb(boolean hasThumb) {
		this.hasThumb = hasThumb;
	}

	@OneToOne(mappedBy = "attmInfo")
	public Attachment getAttm() {
		return attm;
	}

	public void setAttm(Attachment attm) {
		this.attm = attm;
	}

	@Column(name = "ATTM_INFO_DOWNLOAD_URL")
	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	@Column(name = "ATTM_INFO_ATTM_TYPE")
	public AttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	@Column(name = "ATTM_INFO_UPLOAD_CONFIRMED")
	public boolean isUploadConfirmed() {
		return uploadConfirmed;
	}

	public void setUploadConfirmed(boolean uploadConfirmed) {
		this.uploadConfirmed = uploadConfirmed;
	}

	@Column(name = "ATTM_INFO_FILE_TYP")
	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}
}
