package com.vertonur.dao.hibernate.util;

import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.vertonur.pojo.ExtendedBean;
import com.vertonur.pojo.config.Config;

/**
 * @author Vertonur
 * 
 */
public class HibernateUtils {
	private static SessionFactory sessionFactory;

	public static void init(Set<Class<? extends ExtendedBean>> extendedBeans,
			Set<Class<? extends Config>> extendedConfigs, String fileName) {
		if (sessionFactory == null) {
			try {
				AnnotationConfiguration config = new AnnotationConfiguration();
				if (extendedBeans != null && extendedBeans.size() != 0)
					for (Class<? extends ExtendedBean> extendedBean : extendedBeans)
						config.addAnnotatedClass(extendedBean);
				if (extendedConfigs != null && extendedConfigs.size() != 0)
					for (Class<? extends Config> extendedConfig : extendedConfigs)
						config.addAnnotatedClass(extendedConfig);
				if(fileName!=null)
					config.configure(fileName);
				else	config.configure();
				sessionFactory = config.buildSessionFactory();
			} catch (Throwable ex) {
				ex.printStackTrace();
				throw new ExceptionInInitializerError(ex);
			}
		}
	}

	public static SessionFactory getSessionFactory() {
		// Alternatively, you could look up in JNDI here
		return sessionFactory;
	}

	/**
	 * Destroy Hibernate SessionFactory
	 */
	public static void destroy() {
		// Close caches and connection pools
		sessionFactory.close();
		sessionFactory = null;
	}
}
