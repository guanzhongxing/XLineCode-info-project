package com.vertonur.session;

import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vertonur.context.SystemContextService;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;

public class SessionManagerTest {

	private SystemContextService service;

	@Before
	public void init() throws Exception {
		SystemContextService.init();
		service = SystemContextService.getService();
	}

	@Test
	public void testStartExpireSessionRobots() {
		DAOManager daomanager = PojoUtil.getDAOManager();
		daomanager.beginTransaction();
		SessionManager manager = SessionManager.getManager();
		manager.startExpireSessionRobots(10, 5, 5, 5);
	}

	@After
	public void destroy() throws LoginException, SQLException {
		service.shutdown();
	}
}
