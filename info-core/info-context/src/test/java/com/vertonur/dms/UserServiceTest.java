package com.vertonur.dms;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.dms.exception.InvalidOldPasswordException;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.test.CommonDataGenerator;

public class UserServiceTest {
	private DataGenerator saver;
	private SystemContextService service;

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		service.beginTransaction();
		saver = new DataGenerator();
		service.commitTransaction();
	}

	@After
	public void tearDown() throws Exception {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}

	@Test
	public void testAdminChangePassword() throws LoginException,
			InvalidOldPasswordException {
		service.beginTransaction();
		saver.loginAdmin();
		String password = "1234567890";
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.changePassword(saver.getAdmin().getId(),
				CommonDataGenerator.PASSWORD, password);
		service.logout(saver.getSession());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginUser("1", password);
		service.commitTransaction();
	}

	@Test(expected = InvalidOldPasswordException.class)
	public void testAdminChangePasswordWithException() throws LoginException,
			InvalidOldPasswordException {
		service.beginTransaction();
		saver.loginAdmin();
		String password = "1234567890";
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.changePassword(saver.getAdmin().getId(), "fail", password);
		service.logout(saver.getSession());
		service.commitTransaction();
	}

	@Test
	public void testUserChangePassword() throws LoginException,
			InvalidOldPasswordException {
		service.beginTransaction();
		saver.loginInfoUser();
		String password = "1234567890";
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.changePassword(saver.getInfoUserId(),
				CommonDataGenerator.PASSWORD, password);
		service.logout(saver.getSession());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginUser("" + saver.getInfoUserId(), password);
		service.commitTransaction();
	}

	@Test(expected = InvalidOldPasswordException.class)
	public void testUserChangePasswordWithException() throws LoginException,
			InvalidOldPasswordException {
		service.beginTransaction();
		saver.loginInfoUser();
		String password = "1234567890";
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.changePassword(saver.getInfoUserId(), "fail", password);
		service.logout(saver.getSession());
		service.commitTransaction();
	}

	@Test
	public void testUpdateUser() throws LoginException {
		service.beginTransaction();
		saver.loginInfoUser();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		User user = userService.getUserById(saver.getInfoUserId());
		user.setName("modified");
		userService.updateUser(user);
		service.commitTransaction();
	}

	@Test
	public void testUpdateAdmin() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(saver.getAdminId());
		admin.setName("modified");
		userService.updateAdmin(admin);
		service.commitTransaction();
	}

	@Test
	public void testUpdateModerator() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginModerator();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Moderator moderator = userService.getModeratorById(saver
				.getModeratorId());
		moderator.setName("modified");
		userService.updateModerator(moderator);
		service.commitTransaction();
	}

	@Test
	public void testLockAdmin() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addAdmin();
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(id);
		userService.lockUser(admin);
		service.commitTransaction();
	}

	@Test
	public void testUnlockAdmin() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
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
		id = saver.getUserIds()[0];
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		admin = userService.getAdminById(id);
		userService.unLockUser(admin);
		service.commitTransaction();
	}

	@Test
	public void testLockModerator() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addModerator();
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getModeratorId();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Moderator moderator = userService.getModeratorById(id);
		userService.lockUser(moderator);
		service.commitTransaction();
	}

	@Test
	public void testUnlockModerator() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
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
		id = saver.getModeratorId();
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		moderator = userService.getModeratorById(id);
		userService.unLockUser(moderator);
		service.commitTransaction();
	}

	@Test
	public void testLockUser() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addUsers(1);
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getUserIds()[0];
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		User user = userService.getUserById(id);
		userService.lockUser(user);
		service.commitTransaction();
	}

	@Test
	public void testUnlockUser() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
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
		id = saver.getUserIds()[0];
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		user = userService.getUserById(id);
		userService.unLockUser(user);
		service.commitTransaction();
	}
}
