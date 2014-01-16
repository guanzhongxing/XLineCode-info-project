package com.vertonur.dms.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.dms.ModeratorManager;

public class AssignPendingLogRobot implements Runnable {
	private static Logger logger = LoggerFactory
			.getLogger(AssignPendingLogRobot.class.getCanonicalName());

	private ModeratorManager manager;
	private DAOManager daoManager;

	public AssignPendingLogRobot(ModeratorManager manager) {
		this.manager = manager;
		daoManager = PojoUtil.getDAOManager();
	}

	public void run() {
		logger.trace("Start to run pending logs assignment task of AssignPendingLogRobot...\n");
		daoManager.beginTransaction();
		manager.assignPendingLogs();
		daoManager.commitTransaction();
		logger.trace("Finishing running pending logs assignment task of AssignPendingLogRobot...\n");
	}
}