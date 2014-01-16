package com.vertonur.session;


public class AcctActivationSession extends ExpireSession{

	private int userId;
	
	public AcctActivationSession(long validPeriod) {
		super(validPeriod);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
