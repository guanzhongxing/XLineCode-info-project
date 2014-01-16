package com.vertonur.robot;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.session.AcctActivationSession;
import com.vertonur.session.SessionManager;

public class InvalidateAcctActivationSessionRobot implements Runnable {
	private static Logger logger = LoggerFactory
			.getLogger(InvalidateAcctActivationSessionRobot.class
					.getCanonicalName());

	private SessionManager manager;

	public InvalidateAcctActivationSessionRobot() {
		this.manager = SessionManager.getManager();
	}

	public void run() {
		logger.trace("Start to run AcctActivationSession time out task of InvalidateAcctActivationSessionRobot...\n");
		Collection<AcctActivationSession> sessions = manager
				.getAcctActivationSessions().values();
		logger.info("There are " + sessions.size()
				+ " acctount activation sessions to check.\n");
		Iterator<AcctActivationSession> iterator = sessions.iterator();
		while (iterator.hasNext()) {
			AcctActivationSession session = iterator.next();
			if (session.isExpired()) {
				String id = session.getSessionId();
				manager.removeAcctActivationSession(id);
				logger.info("Remove session with id:" + id + "\n");
			}
		}

		logger.trace("Finishing running AcctActivationSession time out task of InvalidateAcctActivationSessionRobot...\n");
	}
}
