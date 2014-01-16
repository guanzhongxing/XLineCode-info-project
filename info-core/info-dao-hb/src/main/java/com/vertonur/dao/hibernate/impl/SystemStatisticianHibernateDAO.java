package com.vertonur.dao.hibernate.impl;

import org.hibernate.Criteria;

import com.vertonur.dao.api.SystemStatisticianDAO;
import com.vertonur.pojo.statistician.SystemStatistician;

public class SystemStatisticianHibernateDAO extends
		GenericHibernateDAO<SystemStatistician, Integer> implements
		SystemStatisticianDAO {

	public SystemStatistician getSystemStatistician() {
		Criteria crit = getSession().createCriteria(SystemStatistician.class);
		SystemStatistician statistician = (SystemStatistician) crit
				.uniqueResult();
		return statistician;
	}

	public void updateSystemStatistician(SystemStatistician statistician) {
		getSession().update(statistician);
	}

	public void saveSystemStatistician(SystemStatistician statistician) {
		getSession().save(statistician);
	}
}
