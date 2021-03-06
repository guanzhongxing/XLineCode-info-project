package com.vertonur.security;

/* Java imports */
import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * <p>
 * PassiveCallbackHandler has constructor that takes a username and password so
 * its handle() method does not have to prompt the user for input. Useful for
 * server-side applications.
 * 
 * @author Paul Feuer and John Musser
 * @version 1.0
 */

public class PassiveCallbackHandler implements CallbackHandler {

	private int userId;
	char[] password;

	/**
	 * <p>
	 * Creates a callback handler with the give username and password.
	 */
	public PassiveCallbackHandler(int userId, String pass) {
		this.userId = userId;
		this.password = pass.toCharArray();
	}

	/**
	 * Handles the specified set of Callbacks. Uses the username and password
	 * that were supplied to our constructor to popluate the Callbacks.
	 * 
	 * This class supports NameCallback and PasswordCallback.
	 * 
	 * @param callbacks
	 *            the callbacks to handle
	 * @throws IOException
	 *             if an input or output error occurs.
	 * @throws UnsupportedCallbackException
	 *             if the callback is not an instance of NameCallback or
	 *             PasswordCallback
	 */
	public void handle(Callback[] callbacks) throws java.io.IOException,
			UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof IdCallback) {
				((IdCallback) callbacks[i]).setInputId(userId);
			} else if (callbacks[i] instanceof PasswordCallback) {
				((PasswordCallback) callbacks[i]).setPassword(password);
			} else {
				System.out
						.println("com.you.user.login.action.PassiveCallbackHandler.handle()");
				throw new UnsupportedCallbackException(callbacks[i],
						callbacks[i] + "Callback class not supported");
			}
		}
	}

	/**
	 * Clears out password state.
	 */
	public void clearPassword() {
		if (password != null) {
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;
		}
	}
}
