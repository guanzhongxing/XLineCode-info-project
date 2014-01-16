package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.ModeratorDAO;
import com.vertonur.pojo.Moderator;

public class ModeratorHibernateDAO extends
		GenericHibernateDAO<Moderator, Integer> implements ModeratorDAO {

	@Override
	public Integer saveModerator(Moderator moderator) {
		Integer theUserId = (Integer) getSession().save(moderator);
		return theUserId;
	}

	@Override
	public void updateModerator(Moderator moderator) {
		getSession().update(moderator);
	}

	@Override
	public Moderator getModeratorById(int moderatorId) {
		return (Moderator) getSession().get(getPersistentClass(), moderatorId);
	}

	@Override
	public Moderator getModeratorByName(String name) {
		Criteria crit = getSession().createCriteria(Moderator.class);
		crit.add(Restrictions.eq("activated", true));
		crit.add(Restrictions.ilike("name", name, MatchMode.EXACT));
		Moderator moderator = (Moderator) crit.uniqueResult();
		return moderator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Moderator> getModerators() {
		Criteria crit = getSession().createCriteria(Moderator.class);
		crit.add(Restrictions.eq("activated", true));
		crit.addOrder(Order.asc("digestingNum"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getModeratorNum() {
		Criteria crit = getSession().createCriteria(Moderator.class);
		crit.add(Restrictions.eq("activated", true));
		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Moderator> getAvailableModerators(int digestNum) {
		Criteria crit = getSession().createCriteria(Moderator.class);
		crit.add(Restrictions.lt("digestingNum", digestNum));
		crit.add(Restrictions.eq("activated", true));
		crit.addOrder(Order.asc("digestingNum"));
		return crit.list();
	}
}