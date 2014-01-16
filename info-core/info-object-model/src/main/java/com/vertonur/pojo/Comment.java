package com.vertonur.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "INFO_COR_CMT")
@DiscriminatorValue("C")
public class Comment extends AbstractInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean isLatestOne;

	private Info info;

	public Comment() {
		super();
	}

	@Column(name = "CMT_IS_LST_ONE", nullable = false)
	public boolean isLatestOne() {
		return isLatestOne;
	}

	public void setLatestOne(boolean isLatestOne) {
		this.isLatestOne = isLatestOne;
	}

	@ManyToOne(targetEntity = Info.class)
	@JoinColumn(name = "INFO_ID")
	/**
	 * @return the Info
	 */
	public Info getInfo() {
		return info;
	}

	/**
	 * @param Info
	 *            the Info to set
	 */
	public void setInfo(Info info) {
		this.info = info;
	}
}