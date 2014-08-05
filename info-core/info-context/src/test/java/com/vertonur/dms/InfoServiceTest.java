package com.vertonur.dms;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.pojo.Info;
import com.vertonur.security.exception.InsufficientPermissionException;

public class InfoServiceTest {
	private SystemContextService service;
	private DataGenerator saver;

	@Before
	public void init() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		service.beginTransaction();
		saver = new DataGenerator();
		service.commitTransaction();
	}

	@Test
	public void testSaveInfoPermission() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.addInfo();
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveInfoWithoutPermission() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("saveInfoPermissionTemplate",
				saver.getCategoryId());
		saver.addInfo();
		service.commitTransaction();

	}

	@Test
	public void testSaveCmtPermissionWithAdmin() throws LoginException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		saver.addComment(saver.getAdmin());
		service.commitTransaction();
	}

	@Test
	public void testSaveCmtPermissionWithGuest() throws LoginException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.generateGuestAuthenticationToken();
		saver.addComment(saver.getGuest());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveCmtWithGuestWithoutPermission() throws LoginException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.generateGuestAuthenticationToken();
		saver.removePermission("saveCmtPermissionTemplate",
				saver.getCategoryId());
		saver.addComment(saver.getGuest());
		service.commitTransaction();
	}

	@Test
	public void testLockInfo() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		infoService.lockInfo(saver.getInfoId());
		service.commitTransaction();

		service.beginTransaction();
		Info info = saver.getInfo();
		assertEquals(true, info.isLocked());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(true, info.isLocked());
		service.commitTransaction();
	}

	@Test
	public void testUnlockInfo() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		infoService.lockInfo(saver.getInfoId());
		service.commitTransaction();

		service.beginTransaction();
		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		infoService.unlockInfo(saver.getInfoId());
		service.commitTransaction();

		service.beginTransaction();
		Info info = saver.getInfo();
		assertEquals(false, info.isLocked());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(false, info.isLocked());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testLockInfoWithoutAuthor() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		infoService.lockInfo(saver.getInfoId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUnlockInfoWithoutAuthor() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		infoService.lockInfo(saver.getInfoId());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		infoService.unlockInfo(saver.getInfoId());
		service.commitTransaction();
	}

	@Test
	public void testGetCategoryAnnouncements() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addCategoryAnnouncements(3);
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		List<Info> announcements = infoService.getCategoryAnnouncements(saver
				.getCategoryId());

		assertEquals(3, announcements.size());
		service.commitTransaction();
	}

	@Test
	public void testGetCategoriesAnnouncements() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		int categoryId1 = saver.getCategoryId();
		int categoryId2 = saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addCategoryAnnouncements(3);
		service.commitTransaction();

		service.beginTransaction();
		for (int i = 0; i < 4; i++)
			saver.addCategoryAnnouncement(saver.getDeptId(), categoryId1);
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		List<Info> announcements = infoService
				.getCategoryAnnouncements(categoryId1);
		assertEquals(4, announcements.size());

		announcements = infoService.getCategoryAnnouncements(categoryId2);
		assertEquals(3, announcements.size());
		service.commitTransaction();
	}

	@Test
	public void testGetDepartmentAnnouncements() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addDeptAnnouncements(3);
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		List<Info> announcements = infoService.getDeptAnnouncements(saver
				.getDeptId());

		assertEquals(3, announcements.size());
		service.commitTransaction();
	}

	@Test
	public void testGetDeptsAnnouncements() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		int deptId1 = saver.getDeptId();
		int categoryId1 = saver.getCategoryId();
		saver.addDepartmentAndCategory();
		int deptId2 = saver.getDeptId();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addDeptAnnouncements(3);
		service.commitTransaction();

		service.beginTransaction();
		for (int i = 0; i < 4; i++)
			saver.addDeptAnnouncement(deptId1, categoryId1);
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		List<Info> announcements = infoService.getDeptAnnouncements(deptId1);
		assertEquals(4, announcements.size());

		announcements = infoService.getDeptAnnouncements(deptId2);
		assertEquals(3, announcements.size());
		service.commitTransaction();
	}

	@Test
	public void testGetSystemAnnouncements() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		int deptId1 = saver.getDeptId();
		int categoryId1 = saver.getCategoryId();
		saver.addDepartmentAndCategory();
		int deptId2 = saver.getDeptId();
		int categoryId2 = saver.getCategoryId();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addSystemAnnouncements(3);
		service.commitTransaction();

		service.beginTransaction();
		for (int i = 0; i < 4; i++)
			saver.addSystemAnnouncement(deptId1, categoryId1);
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		List<Info> announcements = infoService.getSystemAnnouncements();
		assertEquals(7, announcements.size());

		announcements = infoService.getDeptAnnouncements(deptId1);
		assertEquals(0, announcements.size());
		announcements = infoService.getDeptAnnouncements(deptId2);
		assertEquals(0, announcements.size());
		announcements = infoService.getCategoryAnnouncements(categoryId1);
		assertEquals(0, announcements.size());
		announcements = infoService.getCategoryAnnouncements(categoryId2);
		assertEquals(0, announcements.size());
		service.commitTransaction();
	}

	@After
	public void destroy() throws LoginException, SQLException {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}
}
