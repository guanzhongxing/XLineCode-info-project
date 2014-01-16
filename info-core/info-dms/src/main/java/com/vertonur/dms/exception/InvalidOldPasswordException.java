package com.vertonur.dms.exception;

public class InvalidOldPasswordException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 993704114794259665L;

	public InvalidOldPasswordException() {
		super(
				"The password inputted is invalid, please check if you have inputted your old password correctly.");
	}
}
