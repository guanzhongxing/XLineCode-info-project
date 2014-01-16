package com.vertonur.ext.ranking;

import java.lang.reflect.InvocationTargetException;

import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.dao.api.RankingDAO;

public class RankingExtensionInitializer {
	public static final String DEFAULT_DAO_IMPL_CLASS = "com.vertonur.ext.ranking.dao.RankingHibernateDAO";
	private String daoImplClass;
	private int defaultCmtPoints;
	private int defaultInfoPoints;
	private int defaultUploadAttmPoints;

	@SuppressWarnings("unchecked")
	public void init() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		if (daoImplClass == null)
			daoImplClass = DEFAULT_DAO_IMPL_CLASS;
		DAOManager manager = PojoUtil.getDAOManager();
		RankingDAO dao = (RankingDAO) manager.getExtendedBeanDAO(
				(Class<RankingDAO>) Class.forName(daoImplClass), Ranking.class);
		PointConfig config = dao.getPointConfig();
		if (config == null) {
			config = new PointConfig();
			config.setCmtPoints(defaultCmtPoints);
			config.setInfoPoints(defaultInfoPoints);
			config.setUploadAttmPoints(defaultUploadAttmPoints);
			dao.savePointConfig(config);
		}
	}

	public String getDaoImplClass() {
		return daoImplClass;
	}

	public void setDaoImplClass(String daoImplClass) {
		this.daoImplClass = daoImplClass;
	}

	public int getDefaultCmtPoints() {
		return defaultCmtPoints;
	}

	public void setDefaultCmtPoints(int defaultCmtPoints) {
		this.defaultCmtPoints = defaultCmtPoints;
	}

	public int getDefaultInfoPoints() {
		return defaultInfoPoints;
	}

	public void setDefaultInfoPoints(int defaultInfoPoints) {
		this.defaultInfoPoints = defaultInfoPoints;
	}

	public int getDefaultUploadAttmPoints() {
		return defaultUploadAttmPoints;
	}

	public void setDefaultUploadAttmPoints(int defaultUploadAttmPoints) {
		this.defaultUploadAttmPoints = defaultUploadAttmPoints;
	}
}
