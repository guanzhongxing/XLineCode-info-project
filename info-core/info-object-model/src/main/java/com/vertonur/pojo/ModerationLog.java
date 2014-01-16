package com.vertonur.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "INFO_COR_MDR_LG")
public class ModerationLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7686326533887440070L;

	/**
	 * DELETED: related content has been deleted by a moderator. <br>
	 * MODIFIED: related content has been modified by a moderator. <br>
	 * APPROVED: related content has been audited and approved by a moderator. <br>
	 * REJECTED: related content has been audited and rejected by a
	 * moderator,which means the content will never come public. <br>
	 * DEFERRED: related content has been assigned to a moderator and waiting
	 * for him/her to audit. <br>
	 * PENDING: there is no moderator available to audit the content ,the
	 * content will be assigned to a moderator when available. <br>
	 * LOCKED: there is an info being locked. <br>
	 * UNLOCKED: there is an info being unlocked.
	 * 
	 * @author Vertonur
	 * 
	 */
	public static enum ModerationStatus {
		DELETED, MODIFIED, APPROVED, REJECTED, DEFERRED, PENDING, MOVED, LOCKED, UNLOCKED
	}

	public static enum LogType {
		INFO, CMT
	}

	private int id;
	private Date moderatedDate;
	private User moderator;
	private User infoAuthor;
	private String reason;
	private String contentSummary;
	private String archiveContent;
	private AbstractInfo modifiedInfo;
	private Category category;
	private ModerationStatus status;
	private LogType logType;

	@Id
	@GeneratedValue
	@Column(name = "MDR_LG_ID")
	public int getId() {
		return id;
	}

	/**
	 * Reserved for orm usage
	 * 
	 * @param id
	 */
	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}

	@Column(name = "MDR_LG_DAT")
	public Date getModeratedDate() {
		return moderatedDate;
	}

	public void setModeratedDate(Date moderatedDate) {
		this.moderatedDate = moderatedDate;
	}

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "MDR_LG_MDROR_ID")
	public User getModerator() {
		return moderator;
	}

	public void setModerator(User moderator) {
		this.moderator = moderator;
	}

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "MDR_LG_INFO_ATR_ID", nullable = false)
	public User getInfoAuthor() {
		return infoAuthor;
	}

	public void setInfoAuthor(User infoAuthor) {
		this.infoAuthor = infoAuthor;
	}

	@Column(name = "MDR_LG_RSN")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "MDR_LG_CNT_SMY")
	public String getContentSummary() {
		return contentSummary;
	}

	public void setContentSummary(String contentSummary) {
		this.contentSummary = contentSummary;
	}

	@Column(name = "MDR_LG_ARC_CNT", columnDefinition = "TEXT")
	public String getArchiveContent() {
		return archiveContent;
	}

	public void setArchiveContent(String archiveContent) {
		this.archiveContent = archiveContent;
	}

	@ManyToOne(targetEntity = AbstractInfo.class)
	@JoinColumn(name = "MDR_LG_MDY_INFO_ID", nullable = false)
	public AbstractInfo getModifiedInfo() {
		return modifiedInfo;
	}

	public void setModifiedInfo(AbstractInfo modifiedInfo) {
		this.modifiedInfo = modifiedInfo;
	}

	@ManyToOne(targetEntity = Category.class)
	@JoinColumn(name = "MDR_LG_CAT_ID", nullable = false)
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Column(name = "MDR_LG_STS")
	public ModerationStatus getStatus() {
		return status;
	}

	public void setStatus(ModerationStatus status) {
		this.status = status;
	}

	@Column(name = "MDR_LG_TYP")
	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
	}
}