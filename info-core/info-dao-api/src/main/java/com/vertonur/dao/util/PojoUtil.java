/**
 * 
 */
package com.vertonur.dao.util;

import java.util.Set;

import com.vertonur.dao.api.DaoManagerPropertyNotFoundException;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.pojo.ExtendedBean;
import com.vertonur.pojo.config.Config;

public class PojoUtil {

	public static final String INFO_DAO_MANAGER_IMPL_CLASS = "info.dao.manager.impl.class";
	private static DAOManager manager;

	/**
	 * 
	 * @return return a DAOFactory
	 */
	@SuppressWarnings("unchecked")
	public synchronized static DAOManager getDAOManager(
			Set<Class<? extends ExtendedBean>> extendedBeans,
			Set<Class<? extends Config>> extendedConfigs, String fileName) {
		if (manager == null) {
			String value = System.getProperty(INFO_DAO_MANAGER_IMPL_CLASS);
			if (value == null || "".equals(value))
				throw new DaoManagerPropertyNotFoundException();
			try {
				manager = DAOManager.instance(
						(Class<? extends DAOManager>) Class.forName(value),
						extendedBeans, extendedConfigs, fileName);
				return manager;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}

		return manager;
	}

	public synchronized static DAOManager getDAOManager() {
		return getDAOManager(null, null, null);
	}

	public synchronized static void destroy() {
		manager.shutdown();
		manager = null;
		DAOManager.destroy();
	}
}
