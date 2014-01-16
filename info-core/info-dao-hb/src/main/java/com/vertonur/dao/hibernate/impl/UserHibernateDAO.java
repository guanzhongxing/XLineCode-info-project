/**
 * 
 */
package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.UserDAO;
import com.vertonur.pojo.User;

/**
 * @author Vertonur
 * 
 */
public class UserHibernateDAO extends GenericHibernateDAO<User, Integer>
		implements UserDAO {

	/*
	 * get a user with the specified ID
	 */
	public User getUserById(int userId) {
		User theUser = (User) getSession().get(getPersistentClass(), userId);
		return theUser;
	}

	@SuppressWarnings("unchecked")
	public User getLatestUser() {
		Criteria crit = getSession().createCriteria(User.class);
		crit.add(Restrictions.eq("activated", true));
		crit.addOrder(Order.desc("regTime"));
		List<User> users = crit.list();
		if (users.size() != 0)
			return users.get(0);
		else
			return null;
	}

	/*
	 * get all users
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsers(int start, int offset) {
		Criteria crit = getSession().createCriteria(User.class);
		crit.add(Restrictions.eq("activated", true));
		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersByName_EM(String userName) {
		Criteria crit = getSession().createCriteria(User.class);
		crit.add(Restrictions.eq("activated", true));
		crit.add(Restrictions.like("name", userName, MatchMode.EXACT));
		List<User> users = crit.list();
		return users;
	}

	public Integer saveUser(User user) {
		Integer theUserId = (Integer) getSession().save(user);
		return theUserId;
	}

	public boolean updateUser(User user) {
		try {
			getSession().update(user);
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	public boolean deleteUser(User user) {
		return makeTransient(user);
	}

	@SuppressWarnings("unchecked")
	public long getUserNum() {
		Criteria crit = getSession().createCriteria(User.class);
		crit.add(Restrictions.eq("activated", true));
		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	public long getUserNumByNameAndGroupId_AM(String userName, int groupId) {
		Criteria crit = getSession().createCriteria(User.class, "user");
		crit.add(Restrictions.eq("user.activated", true));

		if (null != userName && !"".equals(userName))
			crit.add(Restrictions.like("user.name", userName,
					MatchMode.ANYWHERE));

		if (groupId != 0) {
			crit.createAlias("user.groups", "group");
			crit.add(Restrictions.eq("group.id", groupId));
		}

		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersByNameAndGroupId_AM(String userName, int start,
			int offset, int groupId) {
		Criteria crit = getSession().createCriteria(User.class, "user");
		crit.add(Restrictions.eq("user.activated", true));

		if (null != userName && !"".equals(userName))
			crit.add(Restrictions.like("user.name", userName,
					MatchMode.ANYWHERE));

		if (groupId != 0) {
			crit.createAlias("user.groups", "group");
			crit.add(Restrictions.eq("group.id", groupId));
		}

		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		crit.addOrder(Order.desc("regTime"));
		List<User> users = crit.list();
		return users;
	}

	public User getGuest(User guestExample) {
		return findByExample(guestExample, "regTime", "signature",
				"lastLoginDate", "qq", "webSite", "interests", "location",
				"userPres", "statistician", "comments", "infos", "grade",
				"attm").get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getFirstUser() {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.eq("activated", true));
		crit.addOrder(Order.desc("regTime"));

		List<User> results = crit.list();
		if (results.isEmpty())
			return null;
		else
			return (User) results.get(0);
	}

	@Override
	public User getUserByEmail(String email) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.eq("email", email));
		return (User)crit.uniqueResult();
	}
}
