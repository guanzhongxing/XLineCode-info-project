package com.vertonur.dao.hibernate.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;

import com.vertonur.dao.api.ConfigDAO;
import com.vertonur.pojo.config.Config;

public class ConfigHibernateDAO<T extends Config, ID extends Serializable>
		extends GenericHibernateDAO<T, ID> implements ConfigDAO<T, ID> {

	public ConfigHibernateDAO(Class<T> clazz) {
		super(clazz);
	}

	@SuppressWarnings("unchecked")
	public T getConfig() {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		List<T> results = crit.list();
		if (results.size() == 0)
			return null;
		return (T) results.get(0);
	}

	public int saveConfig(Config config) {
		return (Integer) getSession().save(config);
	}

	public void updateConfig(Config config) {
		getSession().update(config);
	}
}
