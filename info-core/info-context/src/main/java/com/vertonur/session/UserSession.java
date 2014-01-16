package com.vertonur.session;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.login.LoginContext;

import org.springframework.security.core.Authentication;

public class UserSession extends ExpireSession {
	public class RequestContext {
		private String requestUri;
		private Map<String, String> parameters;

		public RequestContext(String requestUri, Map<String, String> parameters) {
			this.requestUri = requestUri;
			this.parameters = parameters;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public String getRequestUri() {
			StringBuilder builder = new StringBuilder(requestUri);
			if (!parameters.isEmpty()) {
				builder.append("?");
				Set<Entry<String, String>> entries = parameters.entrySet();
				Iterator<Entry<String, String>> iterator = entries.iterator();
				int mark = 0;
				while (iterator.hasNext()) {
					if (mark != 0)
						builder.append("&");
					Entry<String, String> entry = iterator.next();
					builder.append(entry.getKey());
					builder.append("=");
					builder.append(entry.getValue());
					mark++;
				}
			}

			return builder.toString();
		}

		public void setRequestUri(String requestUri) {
			this.requestUri = requestUri;
		}
	}

	private static final String GUEST = "Guest";

	private int userId;
	private String username;
	private String locale;
	private String ip;
	private boolean isAdmin;
	private boolean moderator;
	private boolean guest;
	private boolean login;
	private boolean invalid = false;
	private Date lastInfoDate;
	private Date lastCmtDate;
	private Date loginDate;

	private RequestContext requestContext;

	private LoginContext loginContext;
	private Authentication authentication;

	protected UserSession(String locale, String ip, long validPeriod, int userId) {
		super(validPeriod);
		username = GUEST;
		guest = true;
		this.locale = locale;
		this.ip = ip;
		this.userId = userId;
	}

	public void activateSession() {
		setStartDate(new Date());
	}

	public String getIp() {
		return this.ip;
	}

	public Date getLastCmtDate() {
		return lastCmtDate;
	}

	public Date getLastInfoDate() {
		return lastInfoDate;
	}

	public String getLocale() {
		return locale;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public int getUserId() {
		return this.userId;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public boolean isGuest() {
		return guest;
	}

	public boolean isLogin() {
		return login;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public void setGuest(boolean isGuest) {
		this.guest = isGuest;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setLastCmtDate(Date lastCmtDate) {
		this.lastCmtDate = lastCmtDate;
	}

	public void setLastInfoDate(Date lastInfoDate) {
		this.lastInfoDate = lastInfoDate;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
		setStartDate(loginDate);
	}

	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getActivatedDate() {
		return getStartDate();
	}

	public void setActivatedDate(Date activatedDate) {
		setStartDate(activatedDate);
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public boolean isModerator() {
		return moderator;
	}

	public void setModerator(boolean isModerator) {
		moderator = isModerator;
	}

	public synchronized boolean isInvalid() {
		return invalid;
	}

	/**
	 * Used when a session time out
	 * 
	 * @param invalid
	 */
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
}
