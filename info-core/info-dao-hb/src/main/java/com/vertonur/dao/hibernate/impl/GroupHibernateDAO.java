package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.GroupDAO;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Group.GroupType;
import com.vertonur.pojo.security.Permission.Component;
import com.vertonur.pojo.security.Permission.Level;
import com.vertonur.pojo.security.Permission.PermissionType;

public class GroupHibernateDAO extends GenericHibernateDAO<Group, Integer>
		implements GroupDAO {

	public Group getGroupById(int id) {
		Group group = (Group) getSession().get(getPersistentClass(), id);
		return group;
	}

	@SuppressWarnings("unchecked")
	public List<Group> getGroups() {
		Criteria crit = getSession().createCriteria(Group.class);
		crit.add(Restrictions.eq("deprecated", false));
		crit.add(Restrictions.ne("groupType", Group.GroupType.SUPER_ADMIN));
		return crit.list();
	}

	public Integer saveGroup(Group group) {
		return (Integer) getSession().save(group);
	}

	public void updateGroup(Group group) {
		getSession().update(group);
	}

	public void deleteGroup(Group group) {
		group.setDeprecated(true);
		updateGroup(group);
	}

	@SuppressWarnings("unchecked")
	public List<Group> getTopLevelGroups() {
		Criteria crit = getSession().createCriteria(Group.class);
		crit.add(Restrictions.ne("groupType", Group.GroupType.SUPER_ADMIN));
		crit.add(Restrictions.eq("deprecated", false));
		crit.add(Restrictions.eq("nestedLevel", 0));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group> getAuditPermissionGroups(int categoryId) {
		Criteria crit = getSession().createCriteria(getPersistentClass(),
				"group");
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("group.groupType",
				GroupType.GENERIC_MDR));
		disjunction.add(Restrictions.eq("group.groupType",
				GroupType.SUPER_ADMIN));
		crit.add(disjunction);
		crit.add(Restrictions.eq("group.deprecated", false));

		crit.createAlias("group.permissions", "permission");
		crit.add(Restrictions.eq("permission.component", Component.CATEGORY));
		crit.add(Restrictions.eq("permission.type",
				PermissionType.AUDIT_CATEGORY));
		crit.add(Restrictions.eq("permission.level", Level.Level_3));
		crit.add(Restrictions.eq("permission.proprietaryMark", categoryId + ""));

		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group> getAdminGroups() {
		Criteria crit = getSession().createCriteria(Group.class);
		crit.add(Restrictions.eq("deprecated", false));
		crit.add(Restrictions.ne("groupType", Group.GroupType.SUPER_ADMIN));
		crit.add(Restrictions.eq("groupType", Group.GroupType.GENERIC_ADMIN));
		return crit.list();
	}
}
