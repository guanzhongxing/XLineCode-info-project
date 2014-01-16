package com.vertonur.session;

import java.util.Date;
import java.util.UUID;

/**
 * Session that will expire
 * 
 * @author Administrator
 * 
 */
public abstract class ExpireSession {
	private String sessionId;
	private Date startDate;
	private long validPeriod;

	/**
	 * @param validPeriod
	 *            valid period of a session ,in second
	 */
	public ExpireSession(long validPeriod) {
		sessionId = UUID.randomUUID().toString();
		this.validPeriod = validPeriod * 1000;
		startDate = new Date();
	}

	public boolean isExpired() {
		long startTime = startDate.getTime();
		long currentTime = new Date().getTime();
		if (currentTime > startTime + validPeriod)
			return true;

		return false;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @param valid
	 *            period of a session ,in second
	 */
	public void setValidPeriod(long validPeriod) {
		this.validPeriod = validPeriod * 1000;
	}

	public long getValidPeriod() {
		return validPeriod / 1000;
	}
}
