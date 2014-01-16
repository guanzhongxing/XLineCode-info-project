package com.vertonur.dao.manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import com.vertonur.dao.api.AdminDAO;
import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.dao.api.AttachmentInfoDAO;
import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.dao.api.CommentDAO;
import com.vertonur.dao.api.ConfigDAO;
import com.vertonur.dao.api.DepartmentDAO;
import com.vertonur.dao.api.ExtendedBeanDAO;
import com.vertonur.dao.api.GroupDAO;
import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.api.ModerationLogDAO;
import com.vertonur.dao.api.ModeratorDAO;
import com.vertonur.dao.api.PermissionDAO;
import com.vertonur.dao.api.PrivateMessageDAO;
import com.vertonur.dao.api.RoleDAO;
import com.vertonur.dao.api.SystemStatisticianDAO;
import com.vertonur.dao.api.UserDAO;
import com.vertonur.pojo.ExtendedBean;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.Config;
import com.vertonur.pojo.config.EmailConfig;
import com.vertonur.pojo.config.InfoConfig;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.config.PrivateMsgConfig;
import com.vertonur.pojo.config.SystemContextConfig;
import com.vertonur.pojo.config.UserConfig;

/**
 * @author Vertonur
 * 
 */
public abstract class DAOManager {
	private static DAOManager manager = null;
	private static final Object OBJ_LOCK = new Object();

	/**
	 * The Factory method for instantiation of concrete factories.
	 */
	public static DAOManager instance(
			Class<? extends DAOManager> specifiedFactory,
			Set<Class<? extends ExtendedBean>> extendedBeans,
			Set<Class<? extends Config>> extendedConfigs, String fileName) {
		if (manager == null)
			synchronized (OBJ_LOCK) {
				try {
					Constructor<? extends DAOManager> constructor;
					constructor = specifiedFactory.getDeclaredConstructor(
							Set.class, Set.class, String.class);
					constructor.setAccessible(true);
					manager = (DAOManager) constructor.newInstance(
							extendedBeans, extendedConfigs, fileName);

					return manager;
				} catch (Exception ex) {
					throw new RuntimeException("Couldn't create DAOFactory: "
							+ manager);
				}
			}
		else
			return manager;
	}

	public abstract CommentDAO getCommentDAO();

	public abstract InfoDAO getInfoDAO();

	public abstract UserDAO getUserDAO();

	public abstract AdminDAO getAdminDAO();

	public abstract DepartmentDAO getDepartmentDAO();

	public abstract CategoryDAO getCategoryDAO();

	public abstract SystemStatisticianDAO getSystemStatisticianDAO();

	public abstract PrivateMessageDAO getPrivateMsgDAO();

	public abstract AttachmentDAO getAttachmentDAO();

	public abstract AttachmentInfoDAO getAttachmentInfoDAO();

	public abstract RoleDAO getRoleDAO();

	public abstract GroupDAO getGroupDAO();

	public abstract PermissionDAO getPermissionDAO();

	public abstract ModerationLogDAO getModerationLogDAO();

	public abstract ModeratorDAO getModeratorDAO();

	public abstract ConfigDAO<InfoConfig, Integer> getInfoConfigDAO();

	public abstract ConfigDAO<CommentConfig, Integer> getCommetConfigDAO();

	public abstract ConfigDAO<UserConfig, Integer> getUserConfigDAO();

	public abstract ConfigDAO<PrivateMsgConfig, Integer> getPrivateMsgConfigDAO();

	public abstract ConfigDAO<SystemContextConfig, Integer> getSystemContextConfigDAO();

	public abstract ConfigDAO<EmailConfig, Integer> getEmailConfigDAO();

	public abstract ConfigDAO<ModerationConfig, Integer> getModerationConfigDAO();

	public abstract <T extends Config> ConfigDAO<T, Integer> getExtendedConfigDAO(
			Class<T> clazz);

	public <T extends ExtendedBean, L extends ExtendedBeanDAO<T, Integer>> ExtendedBeanDAO<T, Integer> getExtendedBeanDAO(
			Class<L> daoClazz, Class<T> clazz) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Constructor<L> constructor = daoClazz.getDeclaredConstructor(clazz
				.getClass());
		return constructor.newInstance(clazz);
	}

	public abstract boolean isTransactionSupported();

	public abstract void beginTransaction();

	public abstract void commitTransaction();

	public abstract boolean isTransactionAlive();

	public abstract void rollbackTransaction();

	/**
	 * Roll back any transaction and release all resources of underling
	 * implementation (caches, connection pools, etc).
	 */
	public abstract void shutdown();

	public static void destroy() {
		manager = null;
	}
}
