/**
 * 
 */
package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.pojo.Category;

/**
 * @author Vertonur
 * 
 */
public class CategoryHibernateDAO extends
		GenericHibernateDAO<Category, Integer> implements CategoryDAO {

	@SuppressWarnings("unchecked")
	public List<Category> getCategorys() {
		Criteria crit = getSession().createCriteria(Category.class);
		crit.add(Restrictions.eq("deprecated", false));
		crit.addOrder(Order.desc("priority"));
		crit.addOrder(Order.desc("createdTime"));
		return crit.list();
	}

	public Category getCategoryById(int categoryId) {
		Category theCategory = (Category) getSession().get(
				getPersistentClass(), categoryId);
		return theCategory;
	}
	
	@SuppressWarnings("unchecked")
	public List<Category> getDeptCategorys(int deptId) {
		Criteria crit = getSession().createCriteria(Category.class);
		crit.add(Restrictions.eq("department.id", deptId));
		crit.add(Restrictions.eq("deprecated", false));
		crit.addOrder(Order.desc("priority"));
		crit.addOrder(Order.desc("createdTime"));
		return crit.list();
	}

	public void updateCategory(Category category) {
		getSession().update(category);
	}

	public Integer saveCategory(Category category) {
		return (Integer) getSession().save(category);
	}

	public boolean deleteCategory(Category category) {
		return this.makeTransient(category);
	}

}
