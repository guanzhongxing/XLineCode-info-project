package com.vertonur.context.exception;

import com.vertonur.context.SystemContextService;

public class SystemContextNotInitializedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6008853730790002001L;

	public SystemContextNotInitializedException() {
		super(
				"Failed to get system context service, to get the service, uses init() method of "
						+ SystemContextService.class.getCanonicalName()
						+ "to initialize it first");
	}
}
