package com.vertonur.test;

import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;

public class LayerInitializer {

	public static void setDaoManageImplClass() {
		System.setProperty(PojoUtil.INFO_DAO_MANAGER_IMPL_CLASS,
				"com.vertonur.dao.hibernate.manager.HibernateDAOManager");
	}

	public static DAOManager initHibernateDaoLayer() {
		setDaoManageImplClass();
		return PojoUtil.getDAOManager();
	}

	public static void destroyHibernateDaoLayer() {
		PojoUtil.destroy();
	}
}
