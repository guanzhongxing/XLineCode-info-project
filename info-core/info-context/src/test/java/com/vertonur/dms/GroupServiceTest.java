package com.vertonur.dms;

import java.util.Set;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.dms.exception.Assigned2SubGroupException;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;

public class GroupServiceTest {
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
		service.commitTransaction();
	}

	@After
	public void tearDown() throws Exception {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}

	@Test
	public void testSaveGroup() {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();
	}

	@Test(expected = AccessDeniedException.class)
	public void testSaveGroupWithAccessDeniedException() throws LoginException {
		service.beginTransaction();
		saver.loginInfoUser();
		saver.addGroup();
		service.commitTransaction();
	}

	@Test
	public void testUpdateGroup() throws Assigned2SubGroupException {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		Group group = saver.getGroup();
		group.setName("modified");
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.updateGroup(group);
		service.commitTransaction();
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateGroupWithAccessDeniedException()
			throws Assigned2SubGroupException, LoginException {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		Group group = saver.getGroup();
		group.setName("modified");
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.updateGroup(group);
		service.commitTransaction();
	}

	@Test
	public void testDeleteGroup() {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		Group group = saver.getGroup();
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.deleteGroup(group);
		service.commitTransaction();
	}

	@Test(expected = AccessDeniedException.class)
	public void testDeleteGroupWithAccessDeniedException()
			throws LoginException {
		service.beginTransaction();
		saver.addGroup();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		Group group = saver.getGroup();
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.deleteGroup(group);
		service.commitTransaction();
	}

	@Test
	public void testUpdateBackendPermission() throws Assigned2SubGroupException {
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
		Set<Permission> backendPermissions = group.getBackendPermissions();
		backendPermissions.add(permission);
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupService.updateBackendPermissions(group, backendPermissions);
		service.commitTransaction();
	}
}
