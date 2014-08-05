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
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.LogType;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.pojo.User;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.statistician.CategoryStatistician;
import com.vertonur.pojo.statistician.UserMsgStatistician;
import com.vertonur.test.CommonDataGenerator;

public class ModerationServiceTest {

	private int moderatorId = 1;
	private String moderatorPwd = CommonDataGenerator.PASSWORD;;
	private DataGenerator saver;
	private SystemContextService service;

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
		moderationService.setDigestionNum(10);
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
	public void testSaveInfo() throws LoginException {
		service.beginTransaction();
		ModerationStatus status = saver.addInfo();
		assertEquals(ModerationStatus.DEFERRED, status);
		service.commitTransaction();

		service.beginTransaction();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());
		assertEquals(ModerationStatus.DEFERRED, logs.get(0).getStatus());
		assertEquals(LogType.INFO, logs.get(0).getLogType());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 1);
		service.commitTransaction();
	}

	@Test
	public void testSave25Info() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfos(25);
		RuntimeParameterService runtimeParameterService = new RuntimeParameterServiceImpl();
		ModerationConfig moderationConfig = runtimeParameterService
				.getModerationConfig();
		moderationConfig.setMdrLgPgnOffset(25);
		runtimeParameterService.updateModerationConfig(moderationConfig);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		int size = logs.size();
		assertEquals(10, size);
		for (int i = 0; i < size; i++)
			assertEquals(ModerationStatus.DEFERRED, logs.get(i).getStatus());
		long num = moderationService.getLogNum(saver.getCategoryId(), saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(10, num);

		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getModerator().getId(), ModerationStatus.DEFERRED);
		size = logs.size();
		assertEquals(10, size);
		for (int i = 0; i < size; i++)
			assertEquals(ModerationStatus.DEFERRED, logs.get(i).getStatus());
		num = moderationService.getLogNum(saver.getCategoryId(),
				saver.getModeratorId(), ModerationStatus.DEFERRED);
		assertEquals(10, num);

		num = moderationService.getPendingLogNum(saver.getCategoryId());
		assertEquals(5, num);

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 10);
		saver.checkModeratorCategoryDigestingNum(saver.getModerator().getId(),
				saver.getCategoryId(), 10);
		service.commitTransaction();
	}

	@Test
	public void testModifyInfo() throws LoginException,
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
		saver.loginUser(moderatorId + "", moderatorPwd);
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(saver.getInfoId());
		String modifiedContent = "modified test";
		String content = info.getContent();
		String reason = "test";
		info.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.modifyContent(content, info, moderatorId, reason);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, moderatorId,
				ModerationStatus.MODIFIED);
		assertEquals(1, logs.size());
		ModerationLog log = logs.get(0);
		assertEquals(reason, log.getReason());
		assertEquals(content, log.getArchiveContent());
		assertEquals(ModerationStatus.MODIFIED, log.getStatus());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(modifiedContent, info.getContent());

		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(modifiedContent, info.getContent());
		service.commitTransaction();

		service.beginTransaction();
		departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		department = departmentService.getDepartmentById(saver.getDeptId());
		department.setModerated(true);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginUser(moderatorId + "", moderatorPwd);
		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		modifiedContent = "modified test2";
		content = info.getContent();
		reason = "test2";
		info.setContent(modifiedContent);
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.modifyContent(content, info, moderatorId, reason);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderatorId,
				ModerationStatus.MODIFIED);
		assertEquals(2, logs.size());
		log = logs.get(0);
		assertEquals(reason, log.getReason());
		assertEquals(content, log.getArchiveContent());
		assertEquals(ModerationStatus.MODIFIED, log.getStatus());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(modifiedContent, info.getContent());

		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(modifiedContent, info.getContent());
		service.commitTransaction();
	}

	@Test
	public void testModifyCmt() throws LoginException,
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
		saver.loginUser(moderatorId + "", moderatorPwd);
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Comment cmt = infoService.getCommentById(saver.getCmtId());
		String modifiedContent = "modified test";
		String content = cmt.getContent();
		String reason = "test";
		cmt.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.modifyContent(content, cmt, moderatorId, reason);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, moderatorId,
				ModerationStatus.MODIFIED);
		assertEquals(1, logs.size());
		ModerationLog log = logs.get(0);
		assertEquals(reason, log.getReason());
		assertEquals(content, log.getArchiveContent());
		assertEquals(ModerationStatus.MODIFIED, log.getStatus());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		cmt = infoService.getCommentById(saver.getCmtId());
		assertEquals(modifiedContent, cmt.getContent());
		service.commitTransaction();

		service.beginTransaction();
		departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		department = departmentService.getDepartmentById(saver.getDeptId());
		department.setModerated(true);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginUser(moderatorId + "", moderatorPwd);
		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		cmt = infoService.getCommentById(saver.getCmtId());
		modifiedContent = "modified test2";
		content = cmt.getContent();
		reason = "test2";
		cmt.setContent(modifiedContent);
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.modifyContent(content, cmt, moderatorId, reason);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderatorId,
				ModerationStatus.MODIFIED);
		assertEquals(2, logs.size());
		log = logs.get(0);
		assertEquals(reason, log.getReason());
		assertEquals(content, log.getArchiveContent());
		assertEquals(ModerationStatus.MODIFIED, log.getStatus());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		cmt = infoService.getCommentById(saver.getCmtId());
		assertEquals(modifiedContent, cmt.getContent());
		service.commitTransaction();
	}

	@Test
	public void testDeleteInfo() throws LoginException,
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
		int infoId = saver.getInfoId();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		Info info = saver.getInfo();
		saver.addCmts(info, 9);
		service.commitTransaction();

		service.beginTransaction();
		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(2, statistician.getInfoNum());
		assertEquals(9, statistician.getCommentNum());

		User user = saver.getInfoUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(2, userMsgStatistician.getInfoNum());

		user = saver.getCmtUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(9, userMsgStatistician.getCommentNum());

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(infoId);
		assertEquals(false, info.getStatistician().isLatestOne());

		info = saver.getInfo();
		assertEquals(true, info.getStatistician().isLatestOne());
		assertEquals(9, infoService.getCommentNumByInfo(info));
		service.commitTransaction();

		service.beginTransaction();
		saver.loginUser(moderatorId + "", moderatorPwd);
		String reason = "test";
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.deleteInfo(saver.getInfoId(), moderatorId, reason);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService
				.getLogs(saver.getCategoryId(), 0, moderatorId,
						ModerationStatus.DELETED);
		assertEquals(1, logs.size());
		ModerationLog log = logs.get(0);
		assertEquals(reason, log.getReason());
		assertEquals(info.getContent(), log.getArchiveContent());
		assertEquals(ModerationStatus.DELETED, log.getStatus());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(1, statistician.getInfoNum());
		assertEquals(0, statistician.getCommentNum());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(infoId);
		assertEquals(true, info.getStatistician().isLatestOne());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);

		user = saver.getInfoUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getInfoNum());

		user = saver.getCmtUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getCommentNum());
		service.commitTransaction();
	}

	@Test
	public void testDeleteCmt() throws LoginException,
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
		int cmtId = saver.getCmtId();
		saver.addCmts(saver.getInfo(), 9);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		department = departmentService.getDepartmentById(saver.getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(10, statistician.getCommentNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(10, userMsgStatistician.getCommentNum());

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Comment cmt = infoService.getCommentById(cmtId);
		assertEquals(false, cmt.isLatestOne());

		cmt = saver.getCmt();
		assertEquals(true, cmt.isLatestOne());
		assertEquals(10, infoService.getCommentNumByInfo(saver.getInfo()));
		service.commitTransaction();

		service.beginTransaction();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.loginUser(moderatorId + "", moderatorPwd);
		String reason = "test";
		moderationService.deleteComment(saver.getCmtId(), moderatorId, reason);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, moderatorId,
				ModerationStatus.DELETED);
		assertEquals(1, logs.size());
		ModerationLog log = logs.get(0);
		assertEquals(reason, log.getReason());
		assertEquals(cmt.getContent(), log.getArchiveContent());
		assertEquals(ModerationStatus.DELETED, log.getStatus());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(9, statistician.getCommentNum());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		cmt = infoService.getCommentById(saver.getCmtId());
		assertEquals(null, cmt);

		user = saver.getCmtUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(9, userMsgStatistician.getCommentNum());
		service.commitTransaction();
	}

	@Test
	public void testApproveInfo() throws LoginException {
		service.beginTransaction();
		saver.loginInfoUser();
		ModerationStatus status = saver.addInfo();
		service.commitTransaction();

		assertEquals(ModerationStatus.DEFERRED, status);

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);

		info = saver.getInfo();
		assertEquals(null, info);

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getInfoNum());

		User user = saver.getInfoUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getInfoNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 1);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());
		assertEquals(ModerationStatus.DEFERRED, logs.get(0).getStatus());
		moderationService.approveInfo(saver.getInfoId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		info = saver.getInfo();
		assertEquals(saver.getInfoId(), info.getId());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(1, statistician.getInfoNum());

		user = saver.getInfoUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getInfoNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testApproveModifiedInfo() throws LoginException,
			DeptModerationListNotEmptyException {
		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
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
		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(1, statistician.getToModerateNum());

		User user = saver.getInfoUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getInfoNum());

		info = saver.getInfo();
		assertEquals(null, info);

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 1);

		saver.loginAdmin();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		moderationService.approveInfo(saver.getInfoId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.APPROVED);
		assertEquals(2, logs.size());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(1, statistician.getInfoNum());
		assertEquals(0, statistician.getToModerateNum());

		user = saver.getInfoUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getInfoNum());

		info = saver.getInfo();
		assertEquals(saver.getInfoId(), info.getId());
		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(saver.getInfoId(), info.getId());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRejectInfo() throws LoginException {
		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(1, statistician.getToModerateNum());

		User user = saver.getInfoUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getInfoNum());

		Info info = saver.getInfo();
		assertEquals(null, info);

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 1);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		moderationService.rejectInfo(saver.getInfoId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(0, statistician.getToModerateNum());

		user = saver.getInfoUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getInfoNum());

		info = saver.getInfo();
		assertEquals(null, info);

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.REJECTED);
		assertEquals(1, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRejectModifiedInfo() throws LoginException {
		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
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
		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(1, statistician.getToModerateNum());

		User user = saver.getInfoUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getInfoNum());

		info = saver.getInfo();
		assertEquals(null, info);

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 1);

		saver.loginAdmin();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		moderationService.rejectInfo(saver.getInfoId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.APPROVED);
		assertEquals(1, logs.size());

		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.REJECTED);
		assertEquals(1, logs.size());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(0, statistician.getToModerateNum());

		user = saver.getInfoUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getInfoNum());

		info = saver.getInfo();
		assertEquals(null, info);
		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info);

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testApproveComment() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Comment cmt = infoService.getCommentById(saver.getCmtId());
		assertEquals(null, cmt);

		Info info = saver.getInfo();
		assertEquals(0, info.getStatistician().getCommentNum());
		assertEquals(null, info.getStatistician().getLatestComment());

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getCommentNum());
		assertEquals(1, statistician.getToModerateNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 1);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());
		assertEquals(ModerationStatus.DEFERRED, logs.get(0).getStatus());
		assertEquals(LogType.CMT, logs.get(0).getLogType());
		moderationService.approveComment(saver.getCmtId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.APPROVED);
		assertEquals(1, logs.size());

		info = saver.getInfo();
		assertEquals(1, info.getStatistician().getCommentNum());
		assertEquals(saver.getCmtId(), info.getStatistician()
				.getLatestComment().getId());

		cmt = saver.getCmt();
		assertEquals(saver.getCmtId(), cmt.getId());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(1, statistician.getCommentNum());
		assertEquals(0, statistician.getToModerateNum());

		user = saver.getCmtUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testApproveModifiedComment()
			throws DeptModerationListNotEmptyException, LoginException,
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
		departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		department = departmentService.getDepartmentById(saver.getDeptId());
		department.setModerated(true);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		Comment cmt = saver.getCmt();
		String modifiedContent = "modified content";
		cmt.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.updateComment(cmt);
		service.commitTransaction();

		service.beginTransaction();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		cmt = infoService.getCommentById(saver.getCmtId());
		assertEquals(null, cmt);

		Info info = saver.getInfo();
		assertEquals(0, info.getStatistician().getCommentNum());
		assertEquals(null, info.getStatistician().getLatestComment());

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getCommentNum());
		assertEquals(1, statistician.getToModerateNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 1);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());
		assertEquals(ModerationStatus.DEFERRED, logs.get(0).getStatus());
		moderationService.approveComment(saver.getCmtId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.APPROVED);
		assertEquals(1, logs.size());

		info = saver.getInfo();
		assertEquals(1, info.getStatistician().getCommentNum());
		assertEquals(saver.getCmtId(), info.getStatistician()
				.getLatestComment().getId());

		cmt = saver.getCmt();
		assertEquals(saver.getCmtId(), cmt.getId());
		assertEquals(modifiedContent, cmt.getContent());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(1, statistician.getCommentNum());
		assertEquals(0, statistician.getToModerateNum());

		user = saver.getCmtUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				category.getId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRejectComment() throws DeptModerationListNotEmptyException,
			LoginException, CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();

		service.beginTransaction();
		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 1);

		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());
		assertEquals(ModerationStatus.DEFERRED, logs.get(0).getStatus());
		moderationService.rejectComment(saver.getCmtId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.REJECTED);
		assertEquals(1, logs.size());

		Info info = saver.getInfo();
		assertEquals(0, info.getStatistician().getCommentNum());
		assertEquals(null, info.getStatistician().getLatestComment());

		Comment cmt = saver.getCmt();
		assertEquals(null, cmt);

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getCommentNum());
		assertEquals(0, statistician.getToModerateNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testRejectModifiedComment()
			throws DeptModerationListNotEmptyException, LoginException,
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
		departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		department = departmentService.getDepartmentById(saver.getDeptId());
		department.setModerated(true);
		departmentService.updateDepartment(department);
		service.commitTransaction();
		// test set up end

		service.beginTransaction();
		saver.loginCmtUser();
		Comment cmt = saver.getCmt();
		String modifiedContent = "modified content";
		cmt.setContent(modifiedContent);
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.updateComment(cmt);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());

		Info info = saver.getInfo();
		assertEquals(0, info.getStatistician().getCommentNum());
		assertEquals(null, info.getStatistician().getLatestComment());

		cmt = saver.getCmt();
		assertEquals(null, cmt);

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getCommentNum());
		assertEquals(1, statistician.getToModerateNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 1);

		moderationService.rejectComment(saver.getCmtId(), logs.get(0).getId());
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.REJECTED);
		assertEquals(1, logs.size());

		info = saver.getInfo();
		assertEquals(0, info.getStatistician().getCommentNum());
		assertEquals(null, info.getStatistician().getLatestComment());

		cmt = saver.getCmt();
		assertEquals(null, cmt);

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(0, statistician.getCommentNum());
		assertEquals(0, statistician.getToModerateNum());

		user = saver.getCmtUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testUpdateInfo() throws LoginException,
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
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
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
		saver.loginAdmin();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(1, statistician.getInfoNum());
		assertEquals(0, statistician.getToModerateNum());

		User user = saver.getInfoUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getInfoNum());

		info = saver.getInfo();
		assertEquals(saver.getInfoId(), info.getId());

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(saver.getInfoId(), info.getId());
		assertEquals(modifiedContent, info.getContent());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testUpdateInfoWithModeration() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException {
		service.beginTransaction();
		saver.loginInfoUser();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(1, logs.size());

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(1, statistician.getToModerateNum());

		User user = saver.getInfoUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getInfoNum());

		Info info = saver.getInfo();
		assertEquals(null, info);

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(null, info, info);

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 1);

		moderationService.approveInfo(saver.getInfoId(), logs.get(0).getId());
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
		info = saver.getInfo();
		String modifiedContent = "modified content";
		info.setContent(modifiedContent);
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.updateInfo(info);
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		category = saver.getCategory();
		statistician = category.getStatistician();
		assertEquals(1, statistician.getInfoNum());
		assertEquals(0, statistician.getToModerateNum());

		user = saver.getInfoUser();
		userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getInfoNum());

		info = saver.getInfo();
		assertEquals(saver.getInfoId(), info.getId());

		infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(saver.getInfoId(), info.getId());
		assertEquals(modifiedContent, info.getContent());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testSaveComment() throws LoginException,
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
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(1, statistician.getCommentNum());
		assertEquals(0, statistician.getToModerateNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getCommentNum());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test(expected = SavingCommentToLockedInfoException.class)
	public void testSaveCommentToLockedInfo() throws LoginException,
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
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.lockInfo(saver.getInfoId(), saver.getAdminId(),
				"test");
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmt(saver.getInfo());
		service.commitTransaction();
	}

	@Test
	public void testSave10CommentWithModeration() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		saver.setUpAddCmtEnvironment_Moderated();

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
		assertEquals(10, logs.size());
		moderationService.getLogNum(saver.getCategoryId(), saver.getAdmin()
				.getId(), ModerationStatus.DEFERRED);

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(0, statistician.getCommentNum());
		assertEquals(10, statistician.getToModerateNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(0, userMsgStatistician.getCommentNum());
		service.commitTransaction();
	}

	@Test
	public void testUpdateComment() throws LoginException,
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
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		List<ModerationLog> logs = moderationService.getLogs(
				saver.getCategoryId(), 0, saver.getAdmin().getId(),
				ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
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
		saver.loginAdmin();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		logs = moderationService.getLogs(saver.getCategoryId(), 0, saver
				.getAdmin().getId(), ModerationStatus.DEFERRED);
		assertEquals(0, logs.size());

		Category category = saver.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		assertEquals(1, statistician.getInfoNum());
		assertEquals(1, statistician.getCommentNum());
		assertEquals(0, statistician.getToModerateNum());

		User user = saver.getCmtUser();
		UserMsgStatistician userMsgStatistician = user.getStatistician();
		assertEquals(1, userMsgStatistician.getCommentNum());

		cmt = saver.getCmt();
		assertEquals(modifiedContent, cmt.getContent());

		saver.checkModeratorCategoryDigestingNum(saver.getAdmin().getId(),
				saver.getCategoryId(), 0);
		service.commitTransaction();
	}

	@Test
	public void testUnlockInfo() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
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
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.unlockInfo(saver.getInfoId(), saver.getAdminId(),
				"test");
		service.commitTransaction();

		service.beginTransaction();
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		long num = moderationService.getLogNum(saver.getAdminId(),
				ModerationStatus.UNLOCKED);
		assertEquals(1, num);

		Info info = saver.getInfo();
		assertEquals(false, info.isLocked());

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(false, info.isLocked());
		service.commitTransaction();
	}

	@Test
	public void testLockInfo() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
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
		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		long num = moderationService.getLogNum(saver.getAdminId(),
				ModerationStatus.LOCKED);
		assertEquals(1, num);

		Info info = saver.getInfo();
		assertEquals(true, info.isLocked());

		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		info = infoService.getInfoById(saver.getInfoId());
		assertEquals(true, info.isLocked());
		service.commitTransaction();
	}

	@Test
	public void testMoveInfo() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		int categoryId = saver.getCategoryId();
		int categoryId2 = saver.addCategory();
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
		saver.addInfo();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginCmtUser();
		saver.addCmts(saver.getInfo(), 10);
		service.commitTransaction();

		service.beginTransaction();
		Info info = saver.getInfo();
		assertEquals(10, info.getStatistician().getCommentNum());

		CategoryService categoryService = service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category1 = categoryService.getCategoryById(categoryId);
		CategoryStatistician statistician = category1.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(0, statistician.getCommentNum());

		Category category2 = categoryService.getCategoryById(categoryId2);
		statistician = category2.getStatistician();
		assertEquals(3, statistician.getInfoNum());
		assertEquals(10, statistician.getCommentNum());

		category1 = categoryService.getCategoryById(categoryId);
		statistician = category1.getStatistician();
		assertEquals(0, statistician.getInfoNum());
		assertEquals(0, statistician.getCommentNum());

		category2 = categoryService.getCategoryById(categoryId2);
		statistician = category2.getStatistician();
		assertEquals(3, statistician.getInfoNum());
		assertEquals(10, statistician.getCommentNum());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.moveInfo(saver.getInfoId(), categoryId,
				saver.getAdminId(), "test");
		service.commitTransaction();

		service.beginTransaction();
		info = saver.getInfo();
		assertEquals(10, info.getStatistician().getCommentNum());

		categoryService = service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		category1 = categoryService.getCategoryById(categoryId);
		statistician = category1.getStatistician();
		assertEquals(1, statistician.getInfoNum());
		assertEquals(10, statistician.getCommentNum());

		category2 = categoryService.getCategoryById(categoryId2);
		statistician = category2.getStatistician();
		assertEquals(2, statistician.getInfoNum());
		assertEquals(0, statistician.getCommentNum());

		category1 = categoryService.getCategoryById(categoryId);
		statistician = category1.getStatistician();
		assertEquals(1, statistician.getInfoNum());
		assertEquals(10, statistician.getCommentNum());

		category2 = categoryService.getCategoryById(categoryId2);
		statistician = category2.getStatistician();
		assertEquals(2, statistician.getInfoNum());
		assertEquals(0, statistician.getCommentNum());

		moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		long logNum = moderationService.getLogNum(categoryId2,
				saver.getAdminId(), ModerationLog.ModerationStatus.MOVED);
		assertEquals(1, logNum);
		service.commitTransaction();
	}
}
