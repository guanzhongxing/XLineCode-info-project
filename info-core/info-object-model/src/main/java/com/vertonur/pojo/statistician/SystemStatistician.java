package com.vertonur.pojo.statistician;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_SYS_STS")
public class SystemStatistician {

	private int id;
	private int guestId;
	private int defaultGuestGroupId;
	private int defaultUserGroupId;
	private int mostUsersEverOnlineNum;
	private Date mostUsersEverOnlineDate;

	@Id
	@GeneratedValue
	@Column(name = "SYS_STS_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "SYS_STS_MOST_USER_ONLINE_NUM")
	public int getMostUsersEverOnlineNum() {
		return mostUsersEverOnlineNum;
	}

	public void setMostUsersEverOnlineNum(int mostUsersEverOnlineNum) {
		this.mostUsersEverOnlineNum = mostUsersEverOnlineNum;
	}

	public void addMostUsersEverOnlineNum() {
		mostUsersEverOnlineNum++;
	}

	@Column(name = "SYS_STS_MOST_USER_ONLINE_DATE")
	public Date getMostUsersEverOnlineDate() {
		return mostUsersEverOnlineDate;
	}

	public void setMostUsersEverOnlineDate(Date mostUsersEverOnlineDate) {
		this.mostUsersEverOnlineDate = mostUsersEverOnlineDate;
	}

	@Column(name = "SYS_STS_GST_ID")
	public int getGuestId() {
		return guestId;
	}

	public void setGuestId(int guestId) {
		this.guestId = guestId;
	}

	@Column(name = "SYS_STS_DFT_GST_GRP_ID")
	public int getDefaultGuestGroupId() {
		return defaultGuestGroupId;
	}

	public void setDefaultGuestGroupId(int defaultGuestGroupId) {
		this.defaultGuestGroupId = defaultGuestGroupId;
	}

	@Column(name = "SYS_STS_DFT_USR_GRP_ID")
	public int getDefaultUserGroupId() {
		return defaultUserGroupId;
	}

	public void setDefaultUserGroupId(int defaultUserGroupId) {
		this.defaultUserGroupId = defaultUserGroupId;
	}
}
