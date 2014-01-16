package com.vertonur.dao.util;

import com.vertonur.dao.api.AdminDAO;
import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.dao.api.DepartmentDAO;
import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.api.ModerationLogDAO;
import com.vertonur.dao.api.UserDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.pojo.User;
import com.vertonur.test.CommonDataGenerator;

public class TestDataUtil {

	public static Admin saveAdmin(DAOManager manager) {
		manager.beginTransaction();
		AdminDAO dao = manager.getAdminDAO();
		Admin admin = CommonDataGenerator.generateAdmin();
		dao.saveAdmin(admin);
		manager.commitTransaction();

		return admin;
	}

	public static User saveUser(DAOManager manager) {
		manager.beginTransaction();
		UserDAO dao = manager.getUserDAO();
		User user = CommonDataGenerator.generateUser();
		dao.saveUser(user);
		manager.commitTransaction();

		return user;
	}

	public static Department saveDepartment(DAOManager manager, Admin admin) {
		manager.beginTransaction();
		DepartmentDAO dao = manager.getDepartmentDAO();
		Department department = CommonDataGenerator.generateDepartment(admin,
				false);
		dao.saveDepartment(department);
		manager.commitTransaction();

		return department;
	}

	public static Category saveCategory(DAOManager manager, Admin admin) {
		manager.beginTransaction();
		CategoryDAO dao = manager.getCategoryDAO();
		Category category = CommonDataGenerator.generateCategory(admin);
		dao.saveCategory(category);
		manager.commitTransaction();

		return category;
	}

	public static Info saveInfo(DAOManager manager, User user,
			Category category, boolean useTransaction) {
		if (useTransaction)
			manager.beginTransaction();
		InfoDAO dao = manager.getInfoDAO();
		Info info = CommonDataGenerator.generateInfo(user, category);
		dao.saveInfo(info);
		if (useTransaction)
			manager.commitTransaction();

		return info;
	}

	public static void saveModerationLogs(DAOManager manager, int logNum,
			ModerationStatus status, Category category, User user, Admin admin) {
		for (int i = 0; i < logNum; i++)
			saveModerationLog(manager, status, category, user, admin);
	}

	public static void saveDefferedModerationLogs(DAOManager manager,
			int logNum, Category category, User user, Admin admin) {
		for (int i = 0; i < logNum; i++)
			saveModerationLog(manager, ModerationStatus.DEFERRED, category,
					user, admin);
	}

	public static ModerationLog saveModerationLog(DAOManager manager,
			ModerationStatus status, Category category, User user, Admin admin) {
		manager.beginTransaction();
		Info info = saveInfo(manager, user, category, false);
		ModerationLog log = CommonDataGenerator.generateModerationLog(admin,
				status, info);
		ModerationLogDAO dao = manager.getModerationLogDAO();
		dao.saveLog(log);
		manager.commitTransaction();

		return log;
	}

	public static void updateModerationLog(ModerationLog log, DAOManager manager) {
		ModerationLogDAO dao = manager.getModerationLogDAO();
		dao.updateLog(log);
	}
}
