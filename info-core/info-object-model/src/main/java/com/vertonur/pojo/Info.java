package com.vertonur.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.vertonur.pojo.statistician.InfoStatistician;

@Entity(name = "INFO_COR_INFO")
@DiscriminatorValue("I")
public class Info extends AbstractInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6807654760878769357L;
	public static final String SET_LOCKED_METHOD_NAME = "setLocked";

	public static enum InfoType {
		NORMAL, STICKY, CATEGORY_ANNOUNCEMENT, DEPARTMENT_ANNOUNCEMENT, SYSTEM_ANNOUNCEMENT
	}

	private boolean newToUser = true;
	private boolean readToUser = false;
	private boolean hot = false;
	private boolean moderated;
	private boolean locked;
	private InfoType infoType = InfoType.NORMAL;
	private Category category;

	@Embedded
	private InfoStatistician statistician;

	// private InfoAttachment tAttachment;

	public Info() {
		super();
	}

	@Transient
	public boolean isNewToUser() {
		return newToUser;
	}

	public void setNewToUser(boolean newToUser) {
		this.newToUser = newToUser;
	}

	@Transient
	public boolean isReadToUser() {
		return readToUser;
	}

	public void setReadToUser(boolean readToUser) {
		this.readToUser = readToUser;
	}

	@ManyToOne(targetEntity = Category.class)
	@JoinColumn(name = "CAT_ID", nullable = false)
	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	public InfoStatistician getStatistician() {
		return statistician;
	}

	public void setStatistician(InfoStatistician statistician) {
		this.statistician = statistician;
	}

	@Column(name = "INFO_MODERATED")
	public boolean isModerated() {
		return moderated;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	@Column(name = "INFO_LOCKED")
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Reserved for orm usage and for setting this value from service
	 * 
	 */
	@SuppressWarnings("unused")
	private void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Column(name = "INFO_TYP")
	public InfoType getInfoType() {
		return infoType;
	}

	public void setInfoType(InfoType infoType) {
		this.infoType = infoType;
	}

	@Transient
	public boolean isHot() {
		return hot;
	}

	public void setHot(boolean hot) {
		this.hot = hot;
	}

	// @OneToOne
	// @JoinColumn(name="Info_ATTM_ID",nullable=true,updatable=false)
	// public InfoAttachment getTAttachment() {
	// return tAttachment;
	// }
	// public void setTAttachment(InfoAttachment tAttachment) {
	// this.tAttachment = tAttachment;
	// }
}