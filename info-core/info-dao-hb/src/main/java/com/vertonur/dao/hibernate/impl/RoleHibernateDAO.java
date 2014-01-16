package com.vertonur.dao.hibernate.impl;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;

import com.vertonur.dao.api.RoleDAO;
import com.vertonur.pojo.security.Role;

public class RoleHibernateDAO extends GenericHibernateDAO<Role, Integer> implements
		RoleDAO {

	public Role getRoleById(int id) {
		Role role = (Role) getSession().get(getPersistentClass(), id);
		return role;
	}

	@SuppressWarnings("unchecked")
	public Set<Role> getRoles() {
		Criteria crit = getSession().createCriteria(Role.class);
		return new HashSet<Role>(crit.list());
	}

	public Integer saveRole(Role role) {
		 return (Integer)getSession().save(role);
	}

	public void updateRole(Role role) {
		getSession().update(role);
	}

	public void deleteRole(Role role) {
		getSession().delete(role);
	}


}
