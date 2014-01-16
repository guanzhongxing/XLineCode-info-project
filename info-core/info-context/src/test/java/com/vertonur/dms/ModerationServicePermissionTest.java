package com.vertonur.dms;

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
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.security.exception.InsufficientPermissionException;

public class ModerationServicePermissionTest {

	private DataGenerator saver;
	private SystemContextService service;

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		service.beginTransaction();
		saver = new DataGenerator();
		saver.loginAdmin();
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

	@Test(expected = InsufficientPermissionException.class)
	public void testModifyInfoWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException {
		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("editContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(saver.getCategoryId(),
				saver.getInfoId());
		String modifiedContent = "modified test";
		String content = info.getContent();
		String reason = "test";
		info.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.modifyContent(content, info,
				saver.getAdmin().getId(), reason);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testModifyCmtWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("editContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Comment cmt = infoService.getCommentById(saver.getCmtId());
		String modifiedContent = "modified test";
		String content = cmt.getContent();
		String reason = "test";
		cmt.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.modifyContent(content, cmt, saver.getAdmin().getId(),
				reason);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDeleteInfoWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		Info info = saver.getInfo();
		saver.addCmts(info, 9);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("removeContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		String reason = "test";
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.deleteInfo(saver.getInfoId(), saver.getAdmin()
				.getId(), reason);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDeleteCmtWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		saver.getCmtId();
		saver.addCmts(saver.getInfo(), 9);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("removeContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		String reason = "test";
		moderationService.deleteComment(saver.getCmtId(), saver.getAdmin()
				.getId(), reason);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testApproveInfoWithoutPermission() throws LoginException {
		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("auditContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		moderationService.approveInfo(saver.getInfoId(), logs.get(0).getId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testRejectInfoWithoutPermission() throws LoginException,
			Assigned2SubGroupException {
		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("auditContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		moderationService.rejectInfo(saver.getInfoId(), logs.get(0).getId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testApproveCommentWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("auditContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		moderationService.approveComment(saver.getCmtId(), logs.get(0).getId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testRejectCommentWithoutPermission()
			throws DeptModerationListNotEmptyException, LoginException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("auditContentPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		moderationService.rejectComment(saver.getCmtId(), logs.get(0).getId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateCommentWithoutAuthorRight() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		Comment cmt = saver.getCmt();
		String modifiedContent = "modified content";
		cmt.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.updateComment(cmt);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateInfoWithoutAuthorRight() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException {
		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		Info info = saver.getInfo();
		String modifiedContent = "modified content";
		info.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.updateInfo(info);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testLockInfoWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		saver.removePermission("infoLockPermissionTemplate",
				saver.getCategoryId());
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.lockInfo(saver.getInfoId(), saver.getAdminId(),
				"test");
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUnlockInfoWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService.getDepartmentById(saver
				.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.lockInfo(saver.getInfoId(), saver.getAdminId(),
				"test");
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("infoLockPermissionTemplate",
				saver.getCategoryId());
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.unlockInfo(saver.getInfoId(), saver.getAdminId(),
				"test");
		service.commitTransaction();
	}
}
