package com.vertonur.dao.hibernate.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Info;

/**
 * @author Vertonur
 * 
 */
public abstract class AbstractInfoHibernateDAO<T extends AbstractInfo, ID extends Serializable>
		extends GenericHibernateDAO<T, ID> {

	@SuppressWarnings("unchecked")
	public List<T> getPendingContentsByCategoryId(int categoryId) {
		Class<T> clazz = getPersistentClass();
		Criteria crit = getSession().createCriteria(clazz);
		crit.add(Restrictions.eq("state.pending", true));
		crit.addOrder(Order.desc("createdTime"));

		if (clazz.equals(Info.class))
			crit.add(Restrictions.eq("category.id", categoryId));
		else {
			crit.createAlias("info", "info");
			crit.createAlias("info.category", "category");
			crit.add(Restrictions.eq("category.id", categoryId));
		}
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	protected T getContentById(int id, boolean isPending, boolean isDeprecated) {
		Class<T> clazz = getPersistentClass();
		Criteria crit = getSession().createCriteria(clazz);
		crit.add(Restrictions.eq("id", id));
		crit.add(Restrictions.eq("state.pending", isPending));
		crit.add(Restrictions.eq("state.deprecated", isDeprecated));
		List<T> results = crit.list();
		if (!results.isEmpty())
			return (T) results.get(0);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	protected T getFirstContent() {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.eq("state.pending", false));
		crit.add(Restrictions.eq("state.deprecated", false));
		crit.addOrder(Order.desc("createdTime"));

		List<Comment> results = crit.list();
		if (results.isEmpty())
			return null;
		else
			return (T) results.get(0);
	}
}
