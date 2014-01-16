package com.vertonur.dms;

import static org.junit.Assert.assertEquals;

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
import com.vertonur.dms.exception.Assigned2SubGroupException;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.pojo.Moderator;

public class ModerationWorkloadRearrangementTest {

	private DataGenerator saver;
	private SystemContextService service;
	private int digestionNum = 5;

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		service.beginTransaction();
		saver = new DataGenerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		saver.loginAdmin();
		moderationService.setDigestionNum(digestionNum);
		saver.addDepartment(true);
		saver.addCategory();
		service.commitTransaction();
	}

	@After
	public void tearDown() throws Exception {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}

	@Test
	public void testInitManager() {
		service.beginTransaction();
		saver.addModerator();
		saver.addModerator();
		saver.addCategory();
		saver.addCategory();
		service.commitTransaction();

		service.shutdown();
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
	}

	@Test
	public void testRemoveAuditPermission_Save_Info() throws LoginException,
			Assigned2SubGroupException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		Admin admin = saver.getAdmin();
		admin.setLocked(true);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getModerator().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());

		Moderator moderator = saver.getModerator();
		Integer digestingNum = moderator.getCategoryDigestingNum(saver
				.getCategoryId());
		assertEquals(1, digestingNum.intValue());
		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 1);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());
		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(1, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Save_Info_10_3Moderators()
			throws LoginException, Assigned2SubGroupException,
			DeptModerationListNotEmptyException {
		service.beginTransaction();
		saver.loginAdmin();
		int moderator2 = saver.addModerator();
		int moderator3 = saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfos(10);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 4);

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2,
				saver.getCategoryId(), 3);

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3,
				saver.getCategoryId(), 3);
		service.commitTransaction();

		service.beginTransaction();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 5);

		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(5, num);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_User_Save_Info()
			throws LoginException, Assigned2SubGroupException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		Admin admin = saver.getAdmin();
		admin.setLocked(true);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getModerator().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());

		Moderator moderator = saver.getModerator();
		Integer digestingNum = moderator.getCategoryDigestingNum(saver
				.getCategoryId());
		assertEquals(1, digestingNum.intValue());
		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 1);
		service.commitTransaction();

		service.beginTransaction();
		saver.removDefaultModeratorGroup(saver.getModerator());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());
		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(1, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Save_Info_10() throws LoginException,
			Assigned2SubGroupException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		Admin admin = saver.getAdmin();
		admin.setLocked(true);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfos(10);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getModerator().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());
		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(5, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 5);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());
		num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(10, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Save_Info_10_3Moderators_3Categories()
			throws LoginException, Assigned2SubGroupException {
		service.beginTransaction();
		int category1 = saver.getCategoryId();
		int category2 = saver.addCategory();
		int category3 = saver.addCategory();
		int moderator2 = saver.addModerator();
		int moderator3 = saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfos(10, saver.getDeptId(), category1);
		saver.addInfos(10, saver.getDeptId(), category2);
		saver.addInfos(10, saver.getDeptId(), category3);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(category1, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category1, 4);

		logs = moderationService.getLogs(category2, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category2, 4);

		logs = moderationService.getLogs(category3, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category3, 4);
		// done of checking admin moderation status

		logs = moderationService.getLogs(category1, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2, category1, 3);

		logs = moderationService.getLogs(category2, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2, category2, 3);

		logs = moderationService.getLogs(category3, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2, category3, 3);
		// done of checking moderator 2 moderation status

		logs = moderationService.getLogs(category1, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3, category1, 3);

		logs = moderationService.getLogs(category2, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3, category2, 3);

		logs = moderationService.getLogs(category3, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3, category3, 3);
		service.commitTransaction();
		// done of checking moderator 3 moderation status

		service.beginTransaction();
		saver.removDefaultModeratorGroupAuditPermission(category1);
		saver.removDefaultModeratorGroupAuditPermission(category2);
		saver.removDefaultModeratorGroupAuditPermission(category3);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(category1, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		logs = moderationService.getLogs(category2, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		logs = moderationService.getLogs(category3, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category1, 5);
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category2, 5);
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category3, 5);
		// done of checking admin moderation status

		logs = moderationService.getLogs(category1, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category2, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category3, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(moderator2, category1, 0);
		saver.checkModeratorCategoryDigestingNum(moderator2, category2, 0);
		saver.checkModeratorCategoryDigestingNum(moderator2, category3, 0);
		// done of checking moderator 2 moderation status

		logs = moderationService.getLogs(category1, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category2, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category3, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(moderator3, category1, 0);
		saver.checkModeratorCategoryDigestingNum(moderator3, category2, 0);
		saver.checkModeratorCategoryDigestingNum(moderator3, category3, 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Save_Cmt() throws LoginException,
			DeptModerationListNotEmptyException, Assigned2SubGroupException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();
		service.beginTransaction();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		Admin admin = saver.getAdmin();
		admin.setLocked(true);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getModerator().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 1);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());
		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(1, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Save_Cmt_10() throws LoginException,
			DeptModerationListNotEmptyException, Assigned2SubGroupException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();
		service.beginTransaction();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		Admin admin = saver.getAdmin();
		admin.setLocked(true);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmts(saver.getInfo(), 10);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getModerator().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());
		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(5, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 5);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());
		num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(10, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Save_Cmt_10_3Moderators()
			throws LoginException, DeptModerationListNotEmptyException,
			Assigned2SubGroupException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();
		service.beginTransaction();
		int moderator2 = saver.addModerator();
		int moderator3 = saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmts(saver.getInfo(), 10);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 4);

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2,
				saver.getCategoryId(), 3);

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3,
				saver.getCategoryId(), 3);
		service.commitTransaction();

		service.beginTransaction();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 5);

		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(5, num);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Save_Cmt_10_3Moderators_3Categories()
			throws LoginException, Assigned2SubGroupException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		int category1 = saver.getCategoryId();
		int category2 = saver.addCategory();
		int category3 = saver.addCategory();
		int moderator2 = saver.addModerator();
		int moderator3 = saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo(saver.getDeptId(), category1);
		int info1 = saver.getInfoId();
		saver.addInfo(saver.getDeptId(), category2);
		int info2 = saver.getInfoId();
		saver.addInfo(saver.getDeptId(), category3);
		int info3 = saver.getInfoId();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		department = departmentService.getDepartmentById(saver.getDeptId());
		department.setModerated(true);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(category1, info1, false);
		saver.addCmts(info, 10);

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(category2, info2, false);
		saver.addCmts(info, 10);

		info = infoService.getInfoById(category3, info3, false);
		saver.addCmts(info, 10);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(category1, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category1, 4);

		logs = moderationService.getLogs(category2, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category2, 4);

		logs = moderationService.getLogs(category3, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(4, logs.size());
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category3, 4); // done of checking admin moderation status

		logs = moderationService.getLogs(category1, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2, category1, 3);

		logs = moderationService.getLogs(category2, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2, category2, 3);

		logs = moderationService.getLogs(category3, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator2, category3, 3);
		// done of checking moderator 2 moderation status

		logs = moderationService.getLogs(category1, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3, category1, 3);

		logs = moderationService.getLogs(category2, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3, category2, 3);

		logs = moderationService.getLogs(category3, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(3, logs.size());
		saver.checkModeratorCategoryDigestingNum(moderator3, category3, 3);
		service.commitTransaction();
		// done of checking moderator 3 moderation status

		service.beginTransaction();
		saver.removDefaultModeratorGroupAuditPermission(category1);
		saver.removDefaultModeratorGroupAuditPermission(category2);
		saver.removDefaultModeratorGroupAuditPermission(category3);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(category1, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		logs = moderationService.getLogs(category2, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		logs = moderationService.getLogs(category3, 0,
				saver.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(5, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category1, 5);
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category2, 5);
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category3, 5); // done of checking admin moderation status

		logs = moderationService.getLogs(category1, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category2, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category3, 0, moderator2,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(moderator2, category1, 0);
		saver.checkModeratorCategoryDigestingNum(moderator2, category2, 0);
		saver.checkModeratorCategoryDigestingNum(moderator2, category3, 0);
		// done of checking moderator 2 moderation status

		logs = moderationService.getLogs(category1, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category2, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		logs = moderationService.getLogs(category3, 0, moderator3,
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(moderator3, category1, 0);
		saver.checkModeratorCategoryDigestingNum(moderator3, category2, 0);
		saver.checkModeratorCategoryDigestingNum(moderator3, category3, 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Update_Info() throws LoginException,
			DeptModerationListNotEmptyException, Assigned2SubGroupException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		Admin admin = saver.getAdmin();
		admin.setLocked(true);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getModerator().getId(),
				ModerationStatus.DEFERRED);
		moderationService.approveInfo(saver.getInfoId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		Info info = saver.getInfo();
		String modifiedContent = "modified content";
		info.setContent(modifiedContent);
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.updateInfo(info);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(1, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRemoveAuditPermission_Update_Cmt() throws LoginException,
			DeptModerationListNotEmptyException, Assigned2SubGroupException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();
		service.beginTransaction();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		Admin admin = saver.getAdmin();
		admin.setLocked(true);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getModerator().getId(),
				ModerationStatus.DEFERRED);
		moderationService.approveComment(saver.getCmtId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		Comment cmt = saver.getCmt();
		String modifiedContent = "modified content";
		cmt.setContent(modifiedContent);
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.updateComment(cmt);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removDefaultModeratorGroupAuditPermission(saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		long num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(1, num);

		saver.checkModeratorCategoryDigestingNum(saver.getModeratorId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}
}
