package com.vertonur.dms;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.pojo.Department;

public interface DepartmentService {

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public Integer saveDepartment(Department department);

	public Department getDepartmentById(int id);

	public Department getDepartmentById(int id, boolean useCache);

	public List<Department> getDepartments();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public boolean deleteDepartment(Department department);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updateDepartment(Department department)
			throws DeptModerationListNotEmptyException,
			CategoryModerationListNotEmptyException;
}
