package com.vertonur.pojo;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "INFO_COR_ATTM")
public class Attachment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6700407284621032978L;

	private int id;
	private User uploader;
	private AbstractInfo attmHolder;
	private AttachmentInfo attmInfo;

	public Attachment() {
	}

	@Id
	@GeneratedValue
	@Column(name = "ATTM_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "ATTM_USR_ID")
	public User getUploader() {
		return uploader;
	}

	public void setUploader(User uploader) {
		this.uploader = uploader;
	}

	@ManyToOne
	@JoinColumn(name = "MSG_INFO_ID")
	public AbstractInfo getAttmHolder() {
		return attmHolder;
	}

	public void setAttmHolder(AbstractInfo attmHolder) {
		this.attmHolder = attmHolder;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ATTM_INFO_ID", nullable = false)
	public AttachmentInfo getAttmInfo() {
		return attmInfo;
	}

	public void setAttmInfo(AttachmentInfo attmInfo) {
		this.attmInfo = attmInfo;
	}
}
