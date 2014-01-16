package com.vertonur.dao.hibernate.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.PrivateMessageDAO;
import com.vertonur.pojo.PrivateMessage;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadPrivateMessage;

public class PrivateMessageHibernateDAO extends
		GenericHibernateDAO<PrivateMessage, Integer> implements
		PrivateMessageDAO {

	public Integer savePrivateMsg(PrivateMessage pm) {
		return (Integer) getSession().save(pm);
	}

	public PrivateMessage getPrivateMessageById(int pmId) {
		PrivateMessage thePm = (PrivateMessage) getSession().get(
				getPersistentClass(), pmId);
		return thePm;
	}

	public List<PrivateMessage> getPrivateMsgsByAuthor(User author, int start,
			int offset) {
		return getPrivateMsgsByUser(author, start, offset, true);
	}

	public List<PrivateMessage> getPrivateMsgsByReceiver(User receiver,
			int start, int offset) {
		return getPrivateMsgsByUser(receiver, start, offset, false);
	}

	@SuppressWarnings("unchecked")
	private List<PrivateMessage> getPrivateMsgsByUser(User user, int start,
			int offset, boolean isAuthor) {
		Criteria crit = getSession().createCriteria(PrivateMessage.class);
		if (isAuthor)
			crit.add(Restrictions.eq("author", user));
		else
			crit.add(Restrictions.eq("receiver", user));
		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		crit.addOrder(Order.desc("createdTime"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<PrivateMessage> getNewPrivateMsgsByReceiver(User receiver,
			int days, int entries) {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - days);

		Criteria crit = getSession().createCriteria(PrivateMessage.class);
		crit.setFirstResult(0);
		crit.setMaxResults(entries);
		crit.add(Restrictions.ge("createdTime", c.getTime()));
		crit.add(Restrictions.eq("receiver", receiver));
		crit.addOrder(Order.desc("createdTime"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public Set<UserReadPrivateMessage> getReadPrivateMsgsByReceiver(
			User receiver) {
		Criteria crit = getSession().createCriteria(
				UserReadPrivateMessage.class);
		crit.add(Restrictions.eq("reader", receiver));
		return new HashSet<UserReadPrivateMessage>(crit.list());
	}

	public UserReadPrivateMessage getReadPrivateMsgByUserAndPrivateMsg(
			User user, PrivateMessage pm) {
		Criteria crit = getSession().createCriteria(
				UserReadPrivateMessage.class);
		crit.add(Restrictions.eq("reader", user));
		crit.add(Restrictions.eq("readPm", pm));
		return (UserReadPrivateMessage) crit.uniqueResult();
	}

	public void saveUserReadPriateMsg(UserReadPrivateMessage userReadPm) {
		getSession().save(userReadPm);
	}

	@SuppressWarnings("rawtypes")
	public long getPrivateMsgNumByReceiver(User receiver) {
		Criteria crit = getSession().createCriteria(PrivateMessage.class);
		crit.add(Restrictions.eq("receiver", receiver));
		crit.setProjection(Projections.rowCount());
		List results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	public long getPrivateMsgNumByAuthor(User author) {
		Criteria crit = getSession().createCriteria(PrivateMessage.class);
		crit.add(Restrictions.eq("author", author));
		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}
}
