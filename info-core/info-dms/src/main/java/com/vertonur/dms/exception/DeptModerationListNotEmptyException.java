package com.vertonur.dms.exception;

/**
 * This class indicates that there are pending messages to moderate under the
 * department being updated, which is going to disable its moderation function
 * 
 * @author Vertonur
 * 
 */
public class DeptModerationListNotEmptyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4387511476799931082L;

	public DeptModerationListNotEmptyException(String msg) {
		super(msg);
	}
}
