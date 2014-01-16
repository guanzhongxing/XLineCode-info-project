package com.vertonur.pojo.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_MDR_CNG")
public class ModerationConfig implements Config {

	private int id;
	private int mdrLgPgnOffset;
	private int digestionNum;
	private int assignPendingLogInterval;
	private int threadPoolSize;

	@Id
	@GeneratedValue
	@Column(name = "MDR_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "MDR_CNG_LG_PGN_OFT")
	public int getMdrLgPgnOffset() {
		return mdrLgPgnOffset;
	}

	public void setMdrLgPgnOffset(int mdrLgPgnOffset) {
		this.mdrLgPgnOffset = mdrLgPgnOffset;
	}

	/**
	 * Get the configed digest num that a moderator can handle,with a default
	 * value of 30
	 * 
	 * @return
	 */
	@Column(name = "MDR_CNG_DST_NUM")
	public int getDigestionNum() {
		return digestionNum;
	}

	public void setDigestionNum(int digestionNum) {
		this.digestionNum = digestionNum;
	}

	@Column(name = "MDR_CNG_ASSGN_PDG_LG_IVL")
	public int getAssignPendingLogInterval() {
		return assignPendingLogInterval;
	}

	public void setAssignPendingLogInterval(int assignPendingLogInterval) {
		this.assignPendingLogInterval = assignPendingLogInterval;
	}

	@Column(name = "MDR_CNG_TRD_PL_SIZE")
	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}
}
