package com.vertonur.dms;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dao.api.DepartmentDAO;
import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Department;

public class DepartmentServiceImpl extends GenericService implements
		DepartmentService {

	private DepartmentDAO departmentDao;

	protected DepartmentServiceImpl() {
		super();
		departmentDao = manager.getDepartmentDAO();
	}

	public Integer saveDepartment(Department department) {
		return departmentDao.saveDepartment(department);
	}

	public Department getDepartmentById(int id) {
		return departmentDao.getDepartmentById(id);
	}

	public List<Department> getDepartments() {
		return departmentDao.getDepartments();
	}

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public boolean deleteDepartment(Department department) {
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
		if (department.isDeprecated())
			// TODO:remove infos of categories
			categoryService.deleteCategories(id);
	}
}
