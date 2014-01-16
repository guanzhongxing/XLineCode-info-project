package com.vertonur.dms;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.User;
import com.vertonur.pojo.statistician.CategoryStatistician;

public interface CategoryService {

	public Category getCategoryById(int deptId, int categoryId);

	public Category getCategoryById(int deptId, int categoryId, boolean useCache);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public boolean deleteCategory(Category category);

	/**
	 * Be caution to use this method cuz it will not intercepted by Spring AOP
	 * to filter out categorys according to permissions of a user.Put
	 * differently,it should be used only for system aspect other than getting
	 * categorys for a user.
	 * 
	 * @return
	 */
	public List<Category> getCategories();

	/**
	 * Intercepted by both method before advice and after returning advice
	 * 
	 * @param deptId
	 * @return
	 */
	public List<Category> getDeptCategories(int deptId);

	public void hasNewInfosToUser(Category category, User user);

	/**
	 * Update the statistician both in cache and in db
	 * 
	 * @param deptId
	 *            department id
	 * @param categoryId
	 * @param statistician
	 *            category statistician
	 */
	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public void updateStatistician(int deptId, int categoryId,
			CategoryStatistician statistician);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public int saveCategory(int deptId, Category category);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updateCategory(Category category)
			throws CategoryModerationListNotEmptyException;

	/**
	 * Get the overall comment num of the system.
	 */
	public int getCategoriesCmtNum();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void changeDepartment(int deptId, Category category);
}
