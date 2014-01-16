package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;

import com.vertonur.dao.api.PermissionDAO;
import com.vertonur.pojo.security.Permission;

public class PermissionHibernateDAO extends
		GenericHibernateDAO<Permission, Integer> implements PermissionDAO {

	public Permission getPermissionById(int id) {
		Permission permission = (Permission) getSession().get(
				getPersistentClass(), id);
		return permission;
	}

	@SuppressWarnings("unchecked")
	public List<Permission> getPermissions() {
		Criteria crit = getSession().createCriteria(Permission.class);
		return crit.list();
	}

	public Integer savePermission(Permission permission) {
		return (Integer) getSession().save(permission);
	}

	public void updatePermission(Permission permission) {
		getSession().update(permission);
	}

	public void deletePermission(Permission permission) {
		getSession().delete(permission);
	}

	public List<Permission> findByExample(Permission template) {
		return findByExample(template, "description");
	}
}
