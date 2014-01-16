/**
 * 
 */
package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.Department;

/**
 * @author Vertonur
 * 
 */
public interface DepartmentDAO extends GenericDAO<Department, Integer> {
	List<Department> getDepartments();

	Department getDepartmentById(int DepartmentId);

	Integer saveDepartment(Department Department);

	void updateDepartment(Department Department);

	boolean deleteDepartment(Department Department);
}
