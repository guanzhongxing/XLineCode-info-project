package com.vertonur.session;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserPreferences;
import com.vertonur.robot.InvalidateAcctActivationSessionRobot;
import com.vertonur.robot.InvalidateGuestSessionRobot;
import com.vertonur.robot.InvalidateLoginUserSessionRobot;
import com.vertonur.security.PassiveCallbackHandler;
import com.vertonur.security.RdbmsPrincipal;

public class SessionManager {
	private static final Logger logger = LoggerFactory
			.getLogger(SessionManager.class.getCanonicalName());

	private static SessionManager manager;

	private Map<String, UserSession> sessions;
	private Map<String, UserSession> loginSessions;
	private Map<String, AcctActivationSession> acctActivationSessions;
	private ScheduledThreadPoolExecutor executor;
	private long sessionTiming;// in seconds
	private long loginSessionTiming;// in seconds
	private long acctActivationSessionTiming;// in seconds

	private SessionManager() {
		sessions = new HashMap<String, UserSession>();
		loginSessions = new HashMap<String, UserSession>();
		acctActivationSessions = new HashMap<String, AcctActivationSession>();
	}

	public synchronized static SessionManager getManager() {
		if (manager == null)
			init();

		return manager;
	}

	private static void init() {
		manager = new SessionManager();
	}

	public Map<String, UserSession> getSessions() {
		return sessions;
	}

	public UserSession getSession(String sessionId) {
		return sessions.get(sessionId);
	}

	public UserSession getLoginUserSession(String sessionId) {
		return loginSessions.get(sessionId);
	}

	public Map<String, UserSession> getLoginSessions() {
		return loginSessions;
	}

	public void removeLoginUserSession(String staleSessionId) {
		loginSessions.remove(staleSessionId);
	}

	public String checkUserReLogin(int userId) {
		Set<String> keySet = loginSessions.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			UserSession session = loginSessions.get(iterator.next());
			if (session.getUserId() == userId)
				return session.getSessionId();
		}

		return null;
	}

	public User login(UserSession session, int userId, String password)
			throws LoginException {
		PassiveCallbackHandler cbh = new PassiveCallbackHandler(userId,
				password);
		LoginContext lc = new LoginContext("RdbmsLoginModuleId", cbh);
		lc.login();
		// end

		Iterator<Principal> it = lc.getSubject().getPrincipals().iterator();
		User user = null;
		while (it.hasNext())
			user = ((RdbmsPrincipal) it.next()).getUser();

		session.setLoginContext(lc);
		setUpLoginUserSession(session, user);
		String id = session.getSessionId();
		loginSessions.put(id, session);
		sessions.remove(id);
		return user;
	}

	private void setUpLoginUserSession(UserSession session, User user) {
		session.setUserId(user.getId());
		session.setUsername(user.getName());
		UserPreferences preferences = user.getUserPres();
		if (preferences != null && preferences.getLocale() != null)
			session.setLocale(preferences.getLocale());

		if (user instanceof Admin)
			session.setAdmin(true);
		if (user instanceof Moderator)
			session.setModerator(true);

		session.setLogin(true);
		session.setGuest(false);
		session.setLoginDate(new Date());
		session.setValidPeriod(loginSessionTiming);
	}

	/**
	 * Used to log out registered users
	 * 
	 * @throws LoginException
	 */
	public void logout(UserSession session, Iterator<UserSession> iterator)
			throws LoginException {
		synchronized (session) {
			if (!session.isInvalid()) {
				session.getLoginContext().logout();
				session.setLoginContext(null);
				session.setAuthentication(null);
				session.setLogin(false);
				session.setInvalid(true);
				String sessionId = session.getSessionId();
				if (iterator != null)
					iterator.remove();
				else
					removeLoginUserSession(sessionId);
			}

			logUserNumStatistic();
		}
	}

	public void logout(UserSession session) throws LoginException {
		logout(session, null);
	}

	public void removeAcctActivationSession(String sessionId) {
		acctActivationSessions.remove(sessionId);
	}

	public Map<String, AcctActivationSession> getAcctActivationSessions() {
		return acctActivationSessions;
	}

	public boolean isAcctActivationSessionValid(String sessionId, int userId) {
		AcctActivationSession session = acctActivationSessions.get(sessionId);
		if (session == null)
			return false;
		int userIdInSession = session.getUserId();
		if (userIdInSession == userId)
			return true;
		else
			return false;
	}

	public UserSession getNewUserSession(String locale, String ip, int guestId) {
		UserSession session = new UserSession(locale, ip, getSessionTiming(),
				guestId);
		sessions.put(session.getSessionId(), session);

		logUserNumStatistic();
		return session;
	}

	public String addNewAcctActivationSession(int userId) {
		AcctActivationSession session = new AcctActivationSession(
				acctActivationSessionTiming);
		session.setUserId(userId);
		String id = session.getSessionId();
		acctActivationSessions.put(id, session);

		return id;
	}

	public long getSessionTiming() {
		return sessionTiming;
	}

	public void setSessionTiming(long sessionTiming) {
		this.sessionTiming = sessionTiming;
	}

	/**
	 * @return the loginSessionTiming
	 */
	public long getLoginSessionTiming() {
		return loginSessionTiming;
	}

	/**
	 * @param loginSessionTiming
	 *            the loginSessionTiming to set
	 */
	public void setLoginSessionTiming(long loginSessionTiming) {
		this.loginSessionTiming = loginSessionTiming;
	}

	/**
	 * @return the acctActivationSessionTiming
	 */
	public long getAcctActivationSessionTiming() {
		return acctActivationSessionTiming;
	}

	/**
	 * @param acctActivationSessionTiming
	 *            the acctActivationSessionTiming to set
	 */
	public void setAcctActivationSessionTiming(long acctActivationSessionTiming) {
		this.acctActivationSessionTiming = acctActivationSessionTiming;
	}

	public void startExpireSessionRobots(int poolSize,
			int checkUserSessionDelay, int checkGuestSessionDelay,
			int checkAcctActivationSessionDelay) {
		executor = new ScheduledThreadPoolExecutor(poolSize);

		executor.scheduleWithFixedDelay(new InvalidateLoginUserSessionRobot(),
				0, checkUserSessionDelay, TimeUnit.SECONDS);
		executor.scheduleWithFixedDelay(new InvalidateGuestSessionRobot(), 0,
				checkGuestSessionDelay, TimeUnit.SECONDS);
		executor.scheduleWithFixedDelay(
				new InvalidateAcctActivationSessionRobot(), 0,
				checkAcctActivationSessionDelay, TimeUnit.SECONDS);
	}

	public void logUserNumStatistic() {
		if (logger.isDebugEnabled()) {
			int loginUserNum = loginSessions.size();
			int guestNum = sessions.size();
			int onlineUserNum = loginUserNum + guestNum;
			logger.debug("OnlineUserNum:" + onlineUserNum + ",loginUserNum:"
					+ loginUserNum + ",guestNum:" + guestNum + "\n");
		}
	}

	private void shutdownRobots() {
		// TODO:persist AcctActivationSessions into db
		executor.shutdown();
	}

	/**
	 * Persist any pending session informations if necessary and shut down the
	 * singleton session manager
	 */
	public static void shutdown() {
		manager.shutdownRobots();
		manager = null;
	}

	public int getLoginUserNum() {
		return loginSessions.size();
	}

	public int getGuestNum() {
		return sessions.size();
	}
}
