package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.ModerationLogDAO;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;

public class ModerationLogHibernateDAO extends
		GenericHibernateDAO<ModerationLog, Integer> implements ModerationLogDAO {

	@Override
	public ModerationLog getLog(int logId) {
		return (ModerationLog) getSession().get(getPersistentClass(), logId);
	}

	@Override
	public int saveLog(ModerationLog log) {
		return (Integer) getSession().save(log);
	}

	@Override
	public void updateLog(ModerationLog log) {
		getSession().update(log);
	}

	@Override
	public List<ModerationLog> getLogs(Integer categoryId, int start,
			int offset, int moderatorId, ModerationStatus... statuses) {
		return getLogs_Core(categoryId, start, offset, moderatorId, statuses);
	}

	@SuppressWarnings("unchecked")
	private List<ModerationLog> getLogs_Core(Integer categoryId, int start,
			int offset, Integer moderatorId, ModerationStatus... statuses) {
		Criteria crit = getSession().createCriteria(ModerationLog.class);
		if (categoryId != null)
			crit.add(Restrictions.eq("category.id", categoryId));
		if (moderatorId != null)
			crit.add(Restrictions.eq("moderator.id", moderatorId));
		if (statuses != null && statuses.length != 0) {
			Disjunction disjunction = Restrictions.disjunction();
			for (ModerationStatus status : statuses)
				disjunction.add(Restrictions.eq("status", status));
			crit.add(disjunction);
		}
		crit.addOrder(Order.desc("moderatedDate"));

		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		return crit.list();
	}

	@Override
	public long getLogNum(int categoryId, int moderatorId,
			ModerationStatus... statuses) {
		return getLogNum_Core(categoryId, moderatorId, statuses);
	}

	private long getLogNum_Core(Integer categoryId, Integer moderatorId,
			ModerationStatus... statuses) {
		Criteria crit = getSession().createCriteria(ModerationLog.class);
		if (categoryId != null)
			crit.add(Restrictions.eq("category.id", categoryId));
		if (moderatorId != null)
			crit.add(Restrictions.eq("moderator.id", moderatorId));
		if (statuses != null && statuses.length != 0) {
			Disjunction disjunction = Restrictions.disjunction();
			for (ModerationStatus status : statuses)
				disjunction.add(Restrictions.eq("status", status));
			crit.add(disjunction);
		}

		crit.setProjection(Projections.rowCount());
		return (Long) crit.list().get(0);
	}

	@Override
	public long getPendingLogNum(int categoryId) {
		return getLogNum_Core(categoryId, null, ModerationStatus.PENDING);
	}

	@Override
	public long getLogNum(int moderatorId, ModerationStatus... statuses) {
		return getLogNum_Core(null, moderatorId, statuses);
	}

	@Override
	public List<ModerationLog> getPendingLogs(int start, int offset) {
		return getLogs_Core(null, start, offset, null, ModerationStatus.PENDING);
	}
}
