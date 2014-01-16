package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.AdminDAO;
import com.vertonur.pojo.Admin;

public class AdminHibernateDAO extends GenericHibernateDAO<Admin, Integer>
		implements AdminDAO {

	public Integer saveAdmin(Admin admin) {
		Integer theUserId = (Integer) getSession().save(admin);
		return theUserId;
	}

	public Admin getAdminById(int adminId) {
		return (Admin) getSession().get(getPersistentClass(), adminId);
	}

	public Admin getAdminByName(String name) {
		Criteria crit = getSession().createCriteria(Admin.class);
		crit.add(Restrictions.ilike("name", name, MatchMode.EXACT));
		Admin admin = (Admin) crit.uniqueResult();
		return admin;
	}

	public List<Admin> getAdmins() {
		return findByCriteria();
	}

	@SuppressWarnings("unchecked")
	public long getAdminNum() {
		Criteria crit = getSession().createCriteria(Admin.class);
		crit.add(Restrictions.eq("activated", true));
		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}
}