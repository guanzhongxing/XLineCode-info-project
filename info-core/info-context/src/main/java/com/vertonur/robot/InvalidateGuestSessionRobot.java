package com.vertonur.robot;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.session.SessionManager;
import com.vertonur.session.UserSession;

public class InvalidateGuestSessionRobot implements Runnable {
	private static String className = InvalidateGuestSessionRobot.class
			.getCanonicalName();
	private static Logger logger = LoggerFactory.getLogger(className);

	private SessionManager manager;

	public InvalidateGuestSessionRobot() {
		this.manager = SessionManager.getManager();
	}

	public void run() {
		try {
			logger.trace("Running session time out task of " + className
					+ "...\n");
			Collection<UserSession> sessions = manager.getSessions().values();
			Iterator<UserSession> iterator = sessions.iterator();
			logger.info("There are " + sessions.size()
					+ " sessions of guests to check.\n");
			while (iterator.hasNext()) {
				UserSession session = iterator.next();
				if (session.isExpired()) {
					session.setInvalid(true);
					iterator.remove();

					logger.info("Remove session with id:"
							+ session.getSessionId() + "\n");
				}
			}

			manager.logUserNumStatistic();
			logger.trace("Finishing running session time out task of "
					+ className + "...\n");
		} catch (Throwable e) {
			logger.error(e.getMessage());
		}
	}
}