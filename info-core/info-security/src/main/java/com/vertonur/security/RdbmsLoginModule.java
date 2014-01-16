package com.vertonur.security;

/* Java imports */
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.User;

/**
 * <p>
 * RdbmsLoginModule is a LoginModule that authenticates a given
 * username/password credential against a JDBC datasource.
 * 
 * <p>
 * This <code>LoginModule</code> interoperates with any conformant JDBC
 * datasource. To direct this <code>LoginModule</code> to use a specific JNDI
 * datasource, two options must be specified in the login
 * <code>Configuration</code> for this <code>LoginModule</code>.
 * 
 * <pre>
 *    url=<b>jdbc_url</b>
 *    driverb>jdbc driver class</b>
 * </pre>
 * 
 * <p>
 * For the purposed of this example, the format in which the user's information
 * must be stored in the database is in a table named "USER_AUTH" with the
 * following columns
 * 
 * <pre>
 *     USERID
 *     PASSWORD
 *     FIRST_NAME
 *     LAST_NAME
 *     DELETE_PERM
 *     UPDATE_PERM
 * </pre>
 * 
 * @see javax.security.auth.spi.LoginModule
 * @author Paul Feuer and John Musser
 * @version 1.0
 */

public class RdbmsLoginModule implements LoginModule {

	// initial state
	CallbackHandler callbackHandler;
	Subject subject;
	@SuppressWarnings("rawtypes")
	Map sharedState;
	@SuppressWarnings("rawtypes")
	Map options;

	// temporary state
	Vector<RdbmsCredential> tempCredentials;
	Vector<RdbmsPrincipal> tempPrincipals;

	// the authentication status
	boolean success;

	// configurable options
	boolean debug;

	/**
	 * <p>
	 * Creates a login module that can authenticate against a JDBC datasource.
	 */
	public RdbmsLoginModule() {
		tempCredentials = new Vector<RdbmsCredential>();
		tempPrincipals = new Vector<RdbmsPrincipal>();
		success = false;
		debug = false;
	}

	/**
	 * Initialize this <code>LoginModule</code>.
	 * 
	 * <p>
	 * 
	 * @param subject
	 *            the <code>Subject</code> to be authenticated.
	 *            <p>
	 * 
	 * @param callbackHandler
	 *            a <code>CallbackHandler</code> for communicating with the end
	 *            user (prompting for usernames and passwords, for example).
	 *            <p>
	 * 
	 * @param sharedState
	 *            shared <code>LoginModule</code> state.
	 *            <p>
	 * 
	 * @param options
	 *            options specified in the login <code>Configuration</code> for
	 *            this particular <code>LoginModule</code>.
	 */
	@SuppressWarnings("rawtypes")
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {

		// save the initial state
		this.callbackHandler = callbackHandler;
		this.subject = subject;
		this.sharedState = sharedState;
		this.options = options;

		// initialize any configured options
		if (options.containsKey("debug"))
			debug = "true".equalsIgnoreCase((String) options.get("debug"));

		if (debug) {
			System.out.println("\t\t[RdbmsLoginModule] initialize");
		}
	}

	/**
	 * <p>
	 * Verify the password against the relevant JDBC datasource.
	 * 
	 * @return true always, since this <code>LoginModule</code> should not be
	 *         ignored.
	 * 
	 * @exception FailedLoginException
	 *                if the authentication fails.
	 *                <p>
	 * 
	 * @exception LoginException
	 *                if this <code>LoginModule</code> is unable to perform the
	 *                authentication.
	 */
	public boolean login() throws LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] login");

		if (callbackHandler == null)
			throw new LoginException("Error: no CallbackHandler available "
					+ "to garner authentication information from the user");

		try {
			// Setup default callback handlers.
			Callback[] callbacks = new Callback[] { new IdCallback(),
					new PasswordCallback("Password: ", false) };

			callbackHandler.handle(callbacks);

			int userId = ((IdCallback) callbacks[0]).getInputId();
			String password = new String(
					((PasswordCallback) callbacks[1]).getPassword());

			((PasswordCallback) callbacks[1]).clearPassword();

			success = rdbmsValidate(userId, password);

			callbacks[0] = null;
			callbacks[1] = null;

			if (!success)
				throw new LoginException(
						"Authentication failed: Password does not match");

			return (true);
		} catch (LoginException ex) {
			throw ex;
		} catch (Exception ex) {
			success = false;
			throw new LoginException(ex.getMessage());
		}
	}

	/**
	 * Abstract method to commit the authentication process (phase 2).
	 * 
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * succeeded (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules succeeded).
	 * 
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> method),
	 * then this method associates a <code>RdbmsPrincipal</code> with the
	 * <code>Subject</code> located in the <code>LoginModule</code>. If this
	 * LoginModule's own authentication attempted failed, then this method
	 * removes any state that was originally saved.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the commit fails
	 * 
	 * @return true if this LoginModule's own login and commit attempts
	 *         succeeded, or false otherwise.
	 */
	@SuppressWarnings("rawtypes")
	public boolean commit() throws LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] commit");

		if (success) {

			if (subject.isReadOnly()) {
				throw new LoginException("Subject is Readonly");
			}

			try {
				Iterator it = tempPrincipals.iterator();

				if (debug) {
					while (it.hasNext())
						System.out.println("\t\t[RdbmsLoginModule] Principal: "
								+ it.next().toString());
				}

				subject.getPrincipals().addAll(tempPrincipals);
				subject.getPublicCredentials().addAll(tempCredentials);

				tempPrincipals.clear();
				tempCredentials.clear();

				if (callbackHandler instanceof PassiveCallbackHandler)
					((PassiveCallbackHandler) callbackHandler).clearPassword();

				return (true);
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				throw new LoginException(ex.getMessage());
			}
		} else {
			tempPrincipals.clear();
			tempCredentials.clear();
			return (true);
		}
	}

	/**
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * failed. (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules did not succeed).
	 * 
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> and
	 * <code>commit</code> methods), then this method cleans up any state that
	 * was originally saved.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the abort fails.
	 * 
	 * @return false if this LoginModule's own login and/or commit attempts
	 *         failed, and true otherwise.
	 */
	public boolean abort() throws javax.security.auth.login.LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] abort");

		// Clean out state
		success = false;

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof PassiveCallbackHandler)
			((PassiveCallbackHandler) callbackHandler).clearPassword();

		logout();

		return (true);
	}

	/**
	 * Logout a user.
	 * 
	 * <p>
	 * This method removes the Principals that were added by the
	 * <code>commit</code> method.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the logout fails.
	 * 
	 * @return true in all cases since this <code>LoginModule</code> should not
	 *         be ignored.
	 */
	@SuppressWarnings("rawtypes")
	public boolean logout() throws javax.security.auth.login.LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] logout");

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof PassiveCallbackHandler)
			((PassiveCallbackHandler) callbackHandler).clearPassword();

		// remove the principals the login module added
		Iterator it = subject.getPrincipals(RdbmsPrincipal.class).iterator();
		while (it.hasNext()) {
			RdbmsPrincipal p = (RdbmsPrincipal) it.next();
			if (debug)
				System.out.println("\t\t[RdbmsLoginModule] removing principal "
						+ p.toString());
			subject.getPrincipals().remove(p);
		}

		// remove the credentials the login module added
		it = subject.getPublicCredentials(RdbmsCredential.class).iterator();
		while (it.hasNext()) {
			RdbmsCredential c = (RdbmsCredential) it.next();
			if (debug)
				System.out
						.println("\t\t[RdbmsLoginModule] removing credential "
								+ c.toString());
			subject.getPrincipals().remove(c);
		}

		return (true);
	}

	/**
	 * Validate the given user and password against the JDBC datasource.
	 * <p>
	 * 
	 * @param user
	 *            the username to be authenticated.
	 *            <p>
	 * @param pass
	 *            the password to be authenticated.
	 *            <p>
	 * @exception Exception
	 *                if the validation fails.
	 */
	private boolean rdbmsValidate(int userId, String password) throws Exception {

		RdbmsCredential c = null;
		boolean passwordMatch = false;

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] Trying to connect...");

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] connected!");

		User user = PojoUtil.getDAOManager().getUserDAO().getUserById(userId);
		if (user == null)
			throw new LoginException("userNotFound");

		passwordMatch = user.getPassword().equals(password);
		if (passwordMatch) {
			if (debug)
				System.out.println("\t\t[RdbmsLoginModule] passwords match!");

			c = new RdbmsCredential();
			c.setProperty("password", password);
			this.tempCredentials.add(c);
			this.tempPrincipals.add(new RdbmsPrincipal(user.getName(), user));
		} else {
			if (debug)
				System.out
						.println("\t\t[RdbmsLoginModule] passwords do NOT match!");
			throw new LoginException("pwdNotMatch");
		}
		return passwordMatch;
	}
}
