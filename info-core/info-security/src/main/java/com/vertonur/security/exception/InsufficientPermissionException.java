package com.vertonur.security.exception;


public class InsufficientPermissionException extends RuntimeException {

	private static final long serialVersionUID = -4459538340637750450L;

	public InsufficientPermissionException(String message){
		super(message);
	}
}
