package com.vertonur.robot;

import java.util.Collection;
import java.util.Iterator;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.session.SessionManager;
import com.vertonur.session.UserSession;

public class InvalidateLoginUserSessionRobot implements Runnable {
	private static Logger logger = LoggerFactory
			.getLogger(InvalidateLoginUserSessionRobot.class.getCanonicalName());

	private SessionManager manager;

	public InvalidateLoginUserSessionRobot() {
		this.manager = SessionManager.getManager();
	}

	public void run() {
		try {
			logger.trace("Running LoginUserSession time out task of InvalidateLoginUserSessionRobot...\n");
			Collection<UserSession> sessions = manager.getLoginSessions()
					.values();
			Iterator<UserSession> iterator = sessions.iterator();
			logger.info("There are " + sessions.size()
					+ " login user session to check.\n");
			while (iterator.hasNext()) {
				UserSession session = iterator.next();
				synchronized (session) {
					if (session.isExpired()) {
						String id = session.getSessionId();
						try {
							manager.logout(session, iterator);
						} catch (LoginException e) {
							logger.error("Fail to log out session :" + id + "in "
									+ InvalidateLoginUserSessionRobot.class + "\n");
						}
						logger.info("Remove session with id:" + id + "\n");
					}
				}
			}

			logger.trace("Finishing running LoginUserSession time out task of InvalidateLoginUserSessionRobot...\n");
		} catch (Throwable e) {
			logger.error(e.getMessage());
		}
	}
}