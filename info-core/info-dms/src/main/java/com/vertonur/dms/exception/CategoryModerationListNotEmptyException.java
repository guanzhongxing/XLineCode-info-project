package com.vertonur.dms.exception;

/**
 * This class indicates that there are pending messages to moderate under the
 * category being updated, which is going to disable its moderation function
 * 
 * @author Vertonur
 * 
 */
public class CategoryModerationListNotEmptyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4387511476799931082L;

	public CategoryModerationListNotEmptyException(String msg) {
		super(msg);
	}
}
