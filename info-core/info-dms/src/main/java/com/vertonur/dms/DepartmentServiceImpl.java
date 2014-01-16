package com.vertonur.dms;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dao.api.DepartmentDAO;
import com.vertonur.dms.cache.CategoryCache;
import com.vertonur.dms.cache.DepartmentCache;
import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Department;

public class DepartmentServiceImpl extends GenericService implements
		DepartmentService {

	private DepartmentDAO departmentDao;
	private DepartmentCache cache;

	protected DepartmentServiceImpl() {
		super();
		departmentDao = manager.getDepartmentDAO();
		CacheService service = CacheService.getCacheService();
		cache = service.getDepartmentCache();
	}

	public Integer saveDepartment(Department department) {
		int id = departmentDao.saveDepartment(department);
		cache.reloadDepartments(departmentDao);
		return id;
	}

	public Department getDepartmentById(int id) {
		return getDepartmentById(id, true);
	}

	public Department getDepartmentById(int id, boolean useCache) {
		Department department = null;
		if (useCache)
			department = cache.getDepartment(id);
		else
			department = departmentDao.getDepartmentById(id);
		return department;
	}

	public List<Department> getDepartments() {
		List<Department> departList = cache.getDepartments();
		boolean isEmpty = departList.isEmpty();
		if (isEmpty) {
			departList = departmentDao.getDepartments();
			for (Department department : departList) {
				cache.addDepartment(department);
			}
		}

		return departList;
	}

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public boolean deleteDepartment(Department department) {
		cache.removeDepartment(department.getId());
		return departmentDao.deleteDepartment(department);
	}

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updateDepartment(Department department)
			throws DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException {
		CategoryServiceImpl categoryService = new CategoryServiceImpl();
		int id = department.getId();
		if (!department.isModerated()) {
			List<Category> categories = categoryService.getDeptCategories(id);
			for (Category category : categories) {
				if (!category.isModerated()
						&& category.getStatistician().getToModerateNum() != 0) {
					departmentDao.clear();
					throw new DeptModerationListNotEmptyException(
							"There are pending messages to moderate under categories of the department["
									+ department.getId()
									+ ","
									+ department.getName()
									+ "] being updated.To disable moderation function of this department,"
									+ "those messages must be conducted first.");
				}
			}
		}

		departmentDao.updateDepartment(department);
		if (department.isDeprecated()) {
			// TODO:remove infos of categories
			categoryService.deleteCategories(id);
			cache.removeDepartment(id);
		} else {
			cache.reloadDepartments(departmentDao);
			CategoryCache categoryCache = CacheService.getCacheService()
					.getCategoryCache();
			categoryCache.reloadCategories(id, manager.getCategoryDAO());
		}
	}
}
