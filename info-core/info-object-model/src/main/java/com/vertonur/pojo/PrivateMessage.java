package com.vertonur.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name = "INFO_COR_PM")
@DiscriminatorValue("P")
public class PrivateMessage extends AbstractInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean attachSig = false;
	private boolean newToReceiver = true;
	private boolean readToReceiver = false;
	private User receiver;

	@Column(name = "PM_ATTACH_SIG")
	public boolean isAttachSig() {
		return attachSig;
	}

	public void setAttachSig(boolean attachSig) {
		this.attachSig = attachSig;
	}

	@Transient
	public boolean isNewToReceiver() {
		return newToReceiver;
	}

	public void setNewToReceiver(boolean newToReceiver) {
		this.newToReceiver = newToReceiver;
	}

	@Transient
	public boolean isReadToReceiver() {
		return readToReceiver;
	}

	public void setReadToReceiver(boolean readToReceiver) {
		this.readToReceiver = readToReceiver;
	}

	@OneToOne
	@JoinColumn(name = "PM_RECEIVER_ID", nullable = false, updatable = false)
	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
}
