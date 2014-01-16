package com.vertonur.dao.hibernate.impl;

import java.io.Serializable;

import com.vertonur.dao.api.ExtendedBeanDAO;
import com.vertonur.pojo.ExtendedBean;

public abstract class ExtendedBeanHibernateDAO<T extends ExtendedBean, ID extends Serializable>
		extends GenericHibernateDAO<T, ID> implements ExtendedBeanDAO<T, ID> {

	public ExtendedBeanHibernateDAO(Class<T> clazz) {
		super(clazz);
	}
}
