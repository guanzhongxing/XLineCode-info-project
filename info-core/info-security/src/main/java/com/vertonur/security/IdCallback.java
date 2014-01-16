package com.vertonur.security;

import java.io.Serializable;

import javax.security.auth.callback.Callback;

public class IdCallback implements Callback, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int inputId;

	public int getInputId() {
		return inputId;
	}

	public void setInputId(int inputId) {
		this.inputId = inputId;
	}
}
