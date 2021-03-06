package com.vertonur.dao.api;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T, ID extends Serializable> {
	T findById(ID id, boolean lock);

	List<T> findAll();

	List<T> findByExample(T exampleInstance, String... excludeProperty);

	void makePersistent(T entity);

	boolean makeTransient(T entity);

	void flush();

	void clear();
}
