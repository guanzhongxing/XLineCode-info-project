/**
 * 
 */
package com.vertonur.dao.hibernate.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.hibernate.Session;

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
import com.vertonur.dao.hibernate.impl.AdminHibernateDAO;
import com.vertonur.dao.hibernate.impl.AttachmentHibernateDAO;
import com.vertonur.dao.hibernate.impl.AttachmentInfoHibernateDAO;
import com.vertonur.dao.hibernate.impl.CategoryHibernateDAO;
import com.vertonur.dao.hibernate.impl.CommentHibernateDAO;
import com.vertonur.dao.hibernate.impl.ConfigHibernateDAO;
import com.vertonur.dao.hibernate.impl.DepartmentHibernateDAO;
import com.vertonur.dao.hibernate.impl.ExtendedBeanHibernateDAO;
import com.vertonur.dao.hibernate.impl.GenericHibernateDAO;
import com.vertonur.dao.hibernate.impl.GroupHibernateDAO;
import com.vertonur.dao.hibernate.impl.InfoHibernateDAO;
import com.vertonur.dao.hibernate.impl.ModerationLogHibernateDAO;
import com.vertonur.dao.hibernate.impl.ModeratorHibernateDAO;
import com.vertonur.dao.hibernate.impl.PermissionHibernateDAO;
import com.vertonur.dao.hibernate.impl.PrivateMessageHibernateDAO;
import com.vertonur.dao.hibernate.impl.RoleHibernateDAO;
import com.vertonur.dao.hibernate.impl.SystemStatisticianHibernateDAO;
import com.vertonur.dao.hibernate.impl.UserHibernateDAO;
import com.vertonur.dao.hibernate.util.HibernateUtils;
import com.vertonur.dao.manager.DAOManager;
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
public class HibernateDAOManager extends DAOManager {

	private HibernateDAOManager() {
		this(null, null, null);
	}

	private HibernateDAOManager(
			Set<Class<? extends ExtendedBean>> extendedBeans,
			Set<Class<? extends Config>> extendedConfigs, String fileName) {
		HibernateUtils.init(extendedBeans, extendedConfigs, fileName);
	}

	@SuppressWarnings("rawtypes")
	private GenericHibernateDAO instantiateDAO(
			Class<? extends GenericHibernateDAO> daoClass) {
		try {
			GenericHibernateDAO dao = daoClass.newInstance();
			dao.setSession(getCurrentSession());
			return dao;
		} catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass,
					ex);
		}
	}

	private Session getCurrentSession() {
		return HibernateUtils.getSessionFactory().getCurrentSession();
	}

	@Override
	public CommentDAO getCommentDAO() {
		return (CommentDAO) instantiateDAO(CommentHibernateDAO.class);
	}

	@Override
	public InfoDAO getInfoDAO() {
		return (InfoDAO) instantiateDAO(InfoHibernateDAO.class);
	}

	@Override
	public UserDAO getUserDAO() {
		return (UserDAO) instantiateDAO(UserHibernateDAO.class);
	}

	public AdminDAO getAdminDAO() {
		return (AdminDAO) instantiateDAO(AdminHibernateDAO.class);
	}

	@Override
	public DepartmentDAO getDepartmentDAO() {
		return (DepartmentDAO) instantiateDAO(DepartmentHibernateDAO.class);
	}

	@Override
	public CategoryDAO getCategoryDAO() {
		return (CategoryDAO) instantiateDAO(CategoryHibernateDAO.class);
	}

	public SystemStatisticianDAO getSystemStatisticianDAO() {
		return (SystemStatisticianDAO) instantiateDAO(SystemStatisticianHibernateDAO.class);
	}

	@Override
	public PrivateMessageDAO getPrivateMsgDAO() {
		return (PrivateMessageDAO) instantiateDAO(PrivateMessageHibernateDAO.class);
	}

	@Override
	public AttachmentDAO getAttachmentDAO() {
		return (AttachmentDAO) instantiateDAO(AttachmentHibernateDAO.class);
	}

	@Override
	public AttachmentInfoDAO getAttachmentInfoDAO() {
		return (AttachmentInfoDAO) instantiateDAO(AttachmentInfoHibernateDAO.class);
	}

	@Override
	public RoleDAO getRoleDAO() {
		return (RoleDAO) instantiateDAO(RoleHibernateDAO.class);
	}

	@Override
	public GroupDAO getGroupDAO() {
		return (GroupDAO) instantiateDAO(GroupHibernateDAO.class);
	}

	@Override
	public PermissionDAO getPermissionDAO() {
		return (PermissionDAO) instantiateDAO(PermissionHibernateDAO.class);
	}

	@Override
	public ModerationLogDAO getModerationLogDAO() {
		return (ModerationLogDAO) instantiateDAO(ModerationLogHibernateDAO.class);
	}

	@Override
	public ModeratorDAO getModeratorDAO() {
		return (ModeratorDAO) instantiateDAO(ModeratorHibernateDAO.class);
	}

	public ConfigDAO<InfoConfig, Integer> getInfoConfigDAO() {
		ConfigHibernateDAO<InfoConfig, Integer> dao = new ConfigHibernateDAO<InfoConfig, Integer>(
				InfoConfig.class);
		dao.setSession(getCurrentSession());
		return dao;
	}

	@Override
	public ConfigDAO<CommentConfig, Integer> getCommetConfigDAO() {
		ConfigHibernateDAO<CommentConfig, Integer> dao = new ConfigHibernateDAO<CommentConfig, Integer>(
				CommentConfig.class);
		dao.setSession(getCurrentSession());
		return dao;
	}

	@Override
	public ConfigDAO<UserConfig, Integer> getUserConfigDAO() {
		ConfigHibernateDAO<UserConfig, Integer> dao = new ConfigHibernateDAO<UserConfig, Integer>(
				UserConfig.class);
		dao.setSession(getCurrentSession());
		return dao;
	}

	@Override
	public ConfigDAO<PrivateMsgConfig, Integer> getPrivateMsgConfigDAO() {
		ConfigHibernateDAO<PrivateMsgConfig, Integer> dao = new ConfigHibernateDAO<PrivateMsgConfig, Integer>(
				PrivateMsgConfig.class);
		dao.setSession(getCurrentSession());
		return dao;
	}

	@Override
	public ConfigDAO<SystemContextConfig, Integer> getSystemContextConfigDAO() {
		ConfigHibernateDAO<SystemContextConfig, Integer> dao = new ConfigHibernateDAO<SystemContextConfig, Integer>(
				SystemContextConfig.class);
		dao.setSession(getCurrentSession());
		return dao;
	}

	@Override
	public ConfigDAO<EmailConfig, Integer> getEmailConfigDAO() {
		ConfigHibernateDAO<EmailConfig, Integer> dao = new ConfigHibernateDAO<EmailConfig, Integer>(
				EmailConfig.class);
		dao.setSession(getCurrentSession());
		return dao;
	}

	public ConfigDAO<ModerationConfig, Integer> getModerationConfigDAO() {
		ConfigHibernateDAO<ModerationConfig, Integer> dao = new ConfigHibernateDAO<ModerationConfig, Integer>(
				ModerationConfig.class);
		dao.setSession(getCurrentSession());
		return dao;
	}

	@Override
	public <T extends Config> ConfigDAO<T, Integer> getExtendedConfigDAO(
			Class<T> clazz) {
		ConfigHibernateDAO<T, Integer> dao = new ConfigHibernateDAO<T, Integer>(
				clazz);
		dao.setSession(getCurrentSession());
		return dao;
	}

	@Override
	public <T extends ExtendedBean, L extends ExtendedBeanDAO<T, Integer>> ExtendedBeanDAO<T, Integer> getExtendedBeanDAO(
			Class<L> daoClazz, Class<T> clazz) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		ExtendedBeanDAO<T, Integer> dao = super.getExtendedBeanDAO(daoClazz,
				clazz);
		ExtendedBeanHibernateDAO<T, Integer> extendedBeanDAO = (ExtendedBeanHibernateDAO<T, Integer>) dao;
		extendedBeanDAO.setSession(getCurrentSession());

		return (ExtendedBeanDAO<T, Integer>) extendedBeanDAO;
	}

	@Override
	public boolean isTransactionSupported() {
		return true;
	}

	@Override
	public void beginTransaction() {
		Session session = getCurrentSession();
		session.beginTransaction();
	}

	@Override
	public void commitTransaction() {
		Session session = getCurrentSession();
		session.getTransaction().commit();
	}

	@Override
	public boolean isTransactionAlive() {
		Session session = getCurrentSession();
		return session.getTransaction().isActive();
	}

	@Override
	public void rollbackTransaction() {
		Session session = getCurrentSession();
		session.getTransaction().rollback();
	}

	@Override
	public void shutdown() {
		if (isTransactionAlive())
			rollbackTransaction();
		getCurrentSession().close();
		HibernateUtils.destroy();
	}
}
