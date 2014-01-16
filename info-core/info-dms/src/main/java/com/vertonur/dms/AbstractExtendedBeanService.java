package com.vertonur.dms;

import java.lang.reflect.InvocationTargetException;

import com.vertonur.dao.api.ExtendedBeanDAO;
import com.vertonur.pojo.ExtendedBean;

public abstract class AbstractExtendedBeanService<T extends ExtendedBean>
		extends GenericService implements ExtendedBeanService<T> {

	private ExtendedBeanDAO<T, Integer> extendedBeanDAO;

	@SuppressWarnings("unchecked")
	protected <L extends ExtendedBeanDAO<T, Integer>> AbstractExtendedBeanService(
			String daoImplClass, Class<T> clazz) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, ClassNotFoundException {
		extendedBeanDAO = manager.getExtendedBeanDAO(
				(Class<L>) Class.forName(daoImplClass), clazz);
	}

	protected ExtendedBeanDAO<T, Integer> getExtendedBeanDAO() {
		return extendedBeanDAO;
	}

	protected void setExtendedBeanDAO(
			ExtendedBeanDAO<T, Integer> extendedBeanDAO) {
		this.extendedBeanDAO = extendedBeanDAO;
	}

}
