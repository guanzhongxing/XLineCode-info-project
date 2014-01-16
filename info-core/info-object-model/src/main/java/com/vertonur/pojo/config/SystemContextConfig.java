package com.vertonur.pojo.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_SYS_CXT_CNG")
public class SystemContextConfig implements Config {

	private int id;
	private int checkAcctActivationSessionDelay = 60;// in seconds
	private int checkUserSessionDelay = 60;// in seconds
	private int checkGuestSessionDelay = 60;// in seconds
	private int threadPoolSize;
	private int sessionTiming = 600;// in seconds
	private long loginSessionTiming = 1800;// in seconds
	private long acctActivationSessionTiming = 86400;// in seconds

	@Id
	@GeneratedValue
	@Column(name = "SYS_CXT_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "SYS_CXT_CNG_ATV_SSN_DLY")
	public int getCheckAcctActivationSessionDelay() {
		return checkAcctActivationSessionDelay;
	}

	public void setCheckAcctActivationSessionDelay(
			int checkAcctActivationSessionDelay) {
		this.checkAcctActivationSessionDelay = checkAcctActivationSessionDelay;
	}

	@Column(name = "SYS_CXT_CNG_USR_SSN_DLY")
	public int getCheckUserSessionDelay() {
		return checkUserSessionDelay;
	}

	public void setCheckUserSessionDelay(int checkUserSessionDelay) {
		this.checkUserSessionDelay = checkUserSessionDelay;
	}

	@Column(name = "SYS_CXT_CNG_TRD_PL_SIZE")
	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	@Column(name = "SYS_CXT_CNG_SSN_TIMING")
	public int getSessionTiming() {
		return sessionTiming;
	}

	/**
	 * 
	 * @param sessionTiming
	 *            ,the sessionTiming to set, in seconds
	 */
	public void setSessionTiming(int sessionTiming) {
		this.sessionTiming = sessionTiming;
	}

	/**
	 * @return the loginSessionTiming
	 */
	@Column(name = "SYS_CXT_CNG_LG_SSN_TIMING")
	public long getLoginSessionTiming() {
		return loginSessionTiming;
	}

	/**
	 * @param loginSessionTiming
	 *            the loginSessionTiming to set, in seconds
	 */
	public void setLoginSessionTiming(long loginSessionTiming) {
		this.loginSessionTiming = loginSessionTiming;
	}

	/**
	 * @return the acctActivationSessionTiming
	 */
	@Column(name = "SYS_CXT_CNG_AA_SSN_TIMING")
	public long getAcctActivationSessionTiming() {
		return acctActivationSessionTiming;
	}

	/**
	 * @param acctActivationSessionTiming
	 *            the acctActivationSessionTiming to set, in seconds
	 */
	public void setAcctActivationSessionTiming(long acctActivationSessionTiming) {
		this.acctActivationSessionTiming = acctActivationSessionTiming;
	}

	/**
	 * @return the checkGuestSessionDelay
	 */
	@Column(name = "SYS_CXT_CNG_GST_SSN_DLY")
	public int getCheckGuestSessionDelay() {
		return checkGuestSessionDelay;
	}

	/**
	 * @param checkGuestSessionDelay
	 *            the checkGuestSessionDelay to set
	 */
	public void setCheckGuestSessionDelay(int checkGuestSessionDelay) {
		this.checkGuestSessionDelay = checkGuestSessionDelay;
	}
}
