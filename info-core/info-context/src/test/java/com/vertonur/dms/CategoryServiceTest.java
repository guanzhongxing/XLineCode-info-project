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
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.pojo.Category;

public class CategoryServiceTest {
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
	public void testUpdateCategory() throws LoginException,
			DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException {
		service.beginTransaction();
		saver.addDepartment(false);
		saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getCategoryId();
		CategoryService categoryService = service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(id);
		category.setName("modified");
		categoryService.updateCategory(category);
		service.commitTransaction();
	}

	@Test
	public void testDeleteCategory() throws LoginException,
			DeptModerationListNotEmptyException {
		service.beginTransaction();
		saver.addDepartment(false);
		saver.addCategory();
		service.commitTransaction();

		service.beginTransaction();
		int id = saver.getCategoryId();
		CategoryService categoryService = service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(
				id);
		categoryService.deleteCategory(category);
		service.commitTransaction();
	}
}
