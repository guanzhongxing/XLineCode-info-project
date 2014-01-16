package com.vertonur.dms;

import java.util.Set;

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
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.exception.RankingWithPointsExistException;
import com.vertonur.ext.ranking.service.RankingService;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.EmailConfig;
import com.vertonur.pojo.config.InfoConfig;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.config.PrivateMsgConfig;
import com.vertonur.pojo.config.SystemContextConfig;
import com.vertonur.pojo.config.UserConfig;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;
import com.vertonur.security.exception.BackendPermissionAssignmentException;
import com.vertonur.security.exception.InsufficientPermissionException;

public class BackendPermissionTest {
	private DataGenerator saver;
	private SystemContextService service;
	private String backendAdminProprietary;

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		backendAdminProprietary = (String) service
				.getSpringBean("backendAdminProprietary");
		service.beginTransaction();
		saver = new DataGenerator();
		saver.loginAdmin();
		service.commitTransaction();
	}

	@After
	public void tearDown() throws Exception {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveGroupWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("operateGroupPermission",
				backendAdminProprietary);
		saver.addGroup();
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateGroupWithoutPermission()
			throws Assigned2SubGroupException {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateGroupPermission",
				backendAdminProprietary);
		Group group = saver.getGroup();
		group.setName("modified");
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.updateGroup(group);
		service.commitTransaction();
	}

	@Test(expected = BackendPermissionAssignmentException.class)
	public void testUpdateGroupWithBackendPermission()
			throws Assigned2SubGroupException {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		Permission permissionTemplate = (Permission) service
				.getSpringBean("operateUserPermissoin");
		permissionTemplate.setProprietaryMark("BACKEND_ADMIN");
		PermissionService permissionService = service
				.getDataManagementService(ServiceEnum.PERMISSION_SERVICE);
		Permission permission = permissionService.findByExample(
				permissionTemplate).get(0);

		Group group = saver.getGroup();
		group.addPermission(permission);
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.updateGroup(group);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateBackendPermissionWithoutConfigBackendPermissionPermission()
			throws Assigned2SubGroupException {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("configBackendPermissionPermission",
				backendAdminProprietary);
		service.commitTransaction();

		service.beginTransaction();
		Permission permissionTemplate = (Permission) service
				.getSpringBean("operateUserPermissoin");
		permissionTemplate.setProprietaryMark("BACKEND_ADMIN");
		PermissionService permissionService = service
				.getDataManagementService(ServiceEnum.PERMISSION_SERVICE);
		Permission permission = permissionService.findByExample(
				permissionTemplate).get(0);

		Group group = saver.getGroup();
		Set<Permission> backendPermissions = group.getBackendPermissions();
		backendPermissions.add(permission);
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.updateBackendPermissions(group, backendPermissions);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDeleteGroupWithoutPermission() {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateGroupPermission",
				backendAdminProprietary);
		Group group = saver.getGroup();
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.deleteGroup(group);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveUserWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		saver.addUsers(1);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateUserWithoutPermission() {
		service.beginTransaction();
		saver.addUsers(1);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		User user = userService.getUserById(id);
		user.setName("modified");
		userService.updateUser(user);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testLockUserWithoutPermission() {
		service.beginTransaction();
		saver.addUsers(1);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		User user = userService.getUserById(id);
		userService.lockUser(user);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUnlockUserWithoutPermission() {
		service.beginTransaction();
		saver.addUsers(1);
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		User user = userService.getUserById(id);
		userService.lockUser(user);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		id = saver.getUserIds()[0];
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		user = userService.getUserById(id);
		userService.unLockUser(user);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveAdminWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		saver.addAdmin();
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateAdminWithoutPermission() {
		service.beginTransaction();
		saver.addAdmin();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(id);
		admin.setName("modified");
		userService.updateAdmin(admin);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testLockAdminWithoutPermission() {
		service.beginTransaction();
		saver.addAdmin();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(id);
		userService.lockUser(admin);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUnlockAdminWithoutPermission() {
		service.beginTransaction();
		saver.addAdmin();
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(id);
		userService.lockUser(admin);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		id = saver.getUserIds()[0];
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		admin = userService.getAdminById(id);
		userService.unLockUser(admin);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveModeratorWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		saver.addModerator();
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateModeratorWithoutPermission() {
		service.beginTransaction();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		int id = saver.getModeratorId();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Moderator moderator = userService.getModeratorById(id);
		moderator.setName("modified");
		userService.updateModerator(moderator);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testLockModeratorWithoutPermission() {
		service.beginTransaction();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		int id = saver.getModeratorId();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Moderator moderator = userService.getModeratorById(id);
		userService.lockUser(moderator);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUnlockModeratorWithoutPermission() {
		service.beginTransaction();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getModeratorId();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Moderator moderator = userService.getModeratorById(id);
		userService.lockUser(moderator);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateUserPermissoin", backendAdminProprietary);
		id = saver.getModeratorId();
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		moderator = userService.getModeratorById(id);
		userService.unLockUser(moderator);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveDepartmentWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("operateDepartmentPermission",
				backendAdminProprietary);
		saver.addDepartment(false);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateDepartmentWithoutPermission()
			throws DeptModerationListNotEmptyException, CategoryModerationListNotEmptyException {
		service.beginTransaction();
		saver.addDepartment(false);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateDepartmentPermission",
				backendAdminProprietary);
		int id = saver.getDeptId();
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department dept = deptService.getDepartmentById(id);
		dept.setName("modified");
		deptService.updateDepartment(dept);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDeleteDepartmentWithoutPermission()
			throws DeptModerationListNotEmptyException {
		service.beginTransaction();
		saver.addDepartment(false);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateDepartmentPermission",
				backendAdminProprietary);
		int id = saver.getDeptId();
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department dept = deptService.getDepartmentById(id);
		deptService.deleteDepartment(dept);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveCategoryWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("operateCategoryPermission",
				backendAdminProprietary);
		saver.addDepartment(false);
		saver.addCategory();
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateCategoryWithoutPermission()
			throws CategoryModerationListNotEmptyException {
		service.beginTransaction();
		saver.addDepartment(false);
		saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateCategoryPermission",
				backendAdminProprietary);
		int id = saver.getCategoryId();
		CategoryService categoryService = service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(saver.getDeptId(),
				id, false);
		category.setName("modified");
		categoryService.updateCategory(category);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDeleteCategoryWithoutPermission() {
		service.beginTransaction();
		saver.addDepartment(false);
		saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateCategoryPermission",
				backendAdminProprietary);
		int id = saver.getCategoryId();
		CategoryService categoryService = service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(saver.getDeptId(),
				id, false);
		categoryService.deleteCategory(category);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateAttachmentConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		AttachmentConfig config = runtimeParameterService.getAttachmentConfig();
		config.setAttmtEnabled(false);
		runtimeParameterService.updateAttachmentConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateCommentConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		CommentConfig config = runtimeParameterService.getCommentConfig();
		config.setCmtPgnOffset(0);
		runtimeParameterService.updateCommentConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateEmailConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		EmailConfig config = runtimeParameterService.getEmailConfig();
		config.setRequireAuth(false);
		runtimeParameterService.updateEmailConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateInfoConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		InfoConfig config = runtimeParameterService.getInfoConfig();
		config.setHottestInfoGeCmts(0);
		runtimeParameterService.updateInfoConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateModerationConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		ModerationConfig config = runtimeParameterService.getModerationConfig();
		config.setAssignPendingLogInterval(0);
		runtimeParameterService.updateModerationConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdatePmConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		PrivateMsgConfig config = runtimeParameterService.getPmConfig();
		config.setNewPrivateMsgInDays(0);
		runtimeParameterService.updatePmConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateSystemContextConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		SystemContextConfig config = runtimeParameterService
				.getSystemContextConfig();
		config.setAcctActivationSessionTiming(0);
		runtimeParameterService.updateSystemContextConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateUserConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configRuntimeParameterPermission",
				backendAdminProprietary);
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		UserConfig config = runtimeParameterService.getUserConfig();
		config.setAvatarHeight(0);
		runtimeParameterService.updateUserConfig(config);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testSaveRankingWithoutPermission()
			throws RankingWithPointsExistException {
		service.beginTransaction();
		saver.addNightGroups();
		saver.removePermission("operateRankingPermission",
				backendAdminProprietary);
		saver.addSingleRaning(13);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdateRankingWithoutPermission()
			throws RankingWithPointsExistException {
		service.beginTransaction();
		saver.addNightGroups();
		saver.addSingleRaning(13);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateRankingPermission",
				backendAdminProprietary);
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(12);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDeleteRankingWithoutPermission()
			throws RankingWithPointsExistException {
		service.beginTransaction();
		saver.addNightGroups();
		saver.addSingleRaning(13);
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("operateRankingPermission",
				backendAdminProprietary);
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		rankingService.deleteRanking(ranking_1);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUpdatePointConfigWithoutPermission() {
		service.beginTransaction();
		saver.removePermission("configPointsPermission",
				backendAdminProprietary);
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		PointConfig config = rankingService.getPointConfig();
		config.setCmtPoints(0);
		rankingService.updatePointConfig(config);
		service.commitTransaction();
	}
}
