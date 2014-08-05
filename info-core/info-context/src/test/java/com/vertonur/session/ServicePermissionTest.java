package com.vertonur.session;

import static junit.framework.Assert.assertEquals;

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
import com.vertonur.dms.CategoryService;
import com.vertonur.dms.DepartmentService;
import com.vertonur.dms.ModerationService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Department;
import com.vertonur.security.exception.InsufficientPermissionException;

public class ServicePermissionTest {
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
	public void testGetDepartment() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		deptService.getDepartmentById(saver.getDeptId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testGetDepartmentWithoutPermission() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("readDeptPermissionTemplate", saver.getDeptId());
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		deptService.getDepartmentById(saver.getDeptId());
		service.commitTransaction();
	}

	@Test
	public void testGetDepartments() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		List<Department> departments = deptService.getDepartments();
		assertEquals(1, departments.size());
		service.commitTransaction();
	}

	@Test
	public void testGetDepartmentsWithRecordFilteredOut() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("readDeptPermissionTemplate", saver.getDeptId());
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		List<Department> departments = deptService.getDepartments();
		assertEquals(0, departments.size());
		service.commitTransaction();
	}

	@Test
	public void testGetCategoryPermission() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		categoryService.getCategoryById(saver.getCategoryId());
		service.commitTransaction();
	}

	@Test
	public void testGetDeptCategories() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addCategory();
		saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		List<Category> categories = categoryService.getDeptCategories(saver
				.getDeptId());
		assertEquals(3, categories.size());
		service.commitTransaction();
	}

	@Test
	public void testGetDeptCategoriesWithRecordFilteredOut()
			throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addCategory();
		saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("readCategoryPermissionTemplate",
				saver.getCategoryId());
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		List<Category> categories = categoryService.getDeptCategories(saver
				.getDeptId());
		assertEquals(2, categories.size());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testGetDeptCategoriesWithoutReadDepartmentPermission()
			throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addCategory();
		saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("readDeptPermissionTemplate", saver.getDeptId());
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		categoryService.getDeptCategories(saver.getDeptId());
		service.commitTransaction();
	}

	@Test
	public void testAddAndGetDepartmentPermission() throws LoginException {
		SystemContextService service = SystemContextService.getService();
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		DepartmentService departmentService = (DepartmentService) service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		departmentService.getDepartmentById(saver.getDeptId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testMoveInfoWithoutPermission() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		int categoryId = saver.getCategoryId();
		saver.addCategory();
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
		saver.loginAdmin();
		saver.removePermission("moveInfoPermissionTemplate",
				saver.getCategoryId());
		ModerationService moderationService = service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.moveInfo(saver.getInfoId(), categoryId,
				saver.getAdminId(), "test");
		service.commitTransaction();
	}

	@After
	public void destroy() throws LoginException, SQLException {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}
}
