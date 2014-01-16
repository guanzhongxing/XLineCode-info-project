package com.vertonur.dms.cache;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vertonur.dao.api.DepartmentDAO;
import com.vertonur.pojo.Department;

public class DepartmentCache {
	private Map<Integer, Department> departments;

	public DepartmentCache() {
		departments = new LinkedHashMap<Integer, Department>();
	}

	/**
	 * this list is used to cache all departments when system is started,or new
	 * departments created
	 */
	public List<Department> getDepartments() {
		Collection<Department> departs = departments.values();
		List<Department> departList = new LinkedList<Department>();
		departList.addAll(departs);
		return departList;
	}

	/**
	 * Add or update department
	 * 
	 * @param department
	 */
	public void addDepartment(Department department) {
		departments.put(department.getId(), department);
	}

	public void reloadDepartments(DepartmentDAO dao) {
		List<Department> depts = dao.getDepartments();
		departments = new LinkedHashMap<Integer, Department>();
		for (Department department : depts)
			departments.put(department.getId(), department);
	}

	public Department getDepartment(int id) {
		return departments.get(id);
	}

	public void removeDepartment(int id) {
		departments.remove(id);
	}
}
