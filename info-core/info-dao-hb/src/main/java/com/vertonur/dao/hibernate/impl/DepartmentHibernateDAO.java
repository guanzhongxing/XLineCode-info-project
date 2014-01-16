/**
 * 
 */
package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.DepartmentDAO;
import com.vertonur.pojo.Department;

/**
 * @author Vertonur
 * 
 */
public class DepartmentHibernateDAO extends
		GenericHibernateDAO<Department, Integer> implements DepartmentDAO {

	/*
	 * get all Departments
	 */
	@SuppressWarnings("unchecked")
	public List<Department> getDepartments() {
		Criteria crit = getSession().createCriteria(Department.class);
		crit.add(Restrictions.eq("deprecated", false));
		crit.addOrder(Order.desc("priority"));
		crit.addOrder(Order.desc("createdTime"));
		return crit.list();
	}

	public Department getDepartmentById(int id) {
		Department theDepartment = (Department) getSession().get(
				getPersistentClass(), id);
		return theDepartment;
	}

	public Integer saveDepartment(Department department) {
		return (Integer) getSession().save(department);
	}

	public void updateDepartment(Department department) {
		makePersistent(department);
	}

	public boolean deleteDepartment(Department department) {
		return makeTransient(department);
	}
}
