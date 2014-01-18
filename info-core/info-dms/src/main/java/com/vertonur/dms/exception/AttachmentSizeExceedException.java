package com.vertonur.dms.exception;


public class AttachmentSizeExceedException extends Exception {

	private static final long serialVersionUID = -2305309245986580825L;

	public AttachmentSizeExceedException(int fileSize, int configuredSize) {
		super(
				"Size of the uploaded attachment exceeds the configured size, attachment size:"
						+ fileSize + ",configured size:" + configuredSize);
	}
}
