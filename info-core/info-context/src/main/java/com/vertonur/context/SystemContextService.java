package com.vertonur.context;

import java.net.URL;
import java.security.Security;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;

import com.vertonur.CommonUtil;
import com.vertonur.context.exception.SystemContextNotInitializedException;
import com.vertonur.dao.api.CommentDAO;
import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.api.SystemStatisticianDAO;
import com.vertonur.dao.api.UserDAO;
import com.vertonur.dao.hibernate.manager.HibernateDAOManager;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.dms.CacheService;
import com.vertonur.dms.CategoryService;
import com.vertonur.dms.ExtendedBeanService;
import com.vertonur.dms.GroupService;
import com.vertonur.dms.ModeratorManager;
import com.vertonur.dms.RuntimeParameterService;
import com.vertonur.dms.UserService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.ExtendedBean;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.User;
import com.vertonur.pojo.config.Config;
import com.vertonur.pojo.config.SystemContextConfig;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.statistician.SystemStatistician;
import com.vertonur.security.util.PermissionUtils;
import com.vertonur.session.SessionManager;
import com.vertonur.session.UserSession;

/**
 * Session Management Service, this service is used to logout user sessions or
 * expire guest session. User sessions that logged out will be reclaimed as a
 * guest session
 * 
 * @author Vertonur
 * 
 */
public class SystemContextService {
	public static final String SET_SESSION_TIMEOUT_METHOD_NAME = "setSessionTiming";
	public static final String SET_LOGIN_SESSION_TIMEOUT_METHOD_NAME = "setLoginSessionTiming";

	private static final Logger logger = LoggerFactory
			.getLogger(SystemContextService.class.getCanonicalName());
	private static final String DEFAULT_DAO_MANAGER_IMPL_CLASS = HibernateDAOManager.class
			.getCanonicalName();
	private static final String DEFAULT_DAO_CFG_FILE_NAME = "hibernate.cfg.pro.xml";
	private static final String DEFAULT_JAAS_CONFIG = "jaas.config";
	private static final Object registeredUserLock = new Object();
	private static SystemContextService service;
	private static String daoManagerImplClass;
	private static String daoConfigFileName;
	private static String jaasConfigFile;
	private static Set<Class<? extends Config>> extendedConfigs;
	private static Set<Class<? extends ExtendedBean>> extendedBeans;
	private static Set<String> externalConfigXmls;

	public static SystemContextService getService() {
		if (service == null)
			throw new SystemContextNotInitializedException();

		return service;
	}

	private SystemState state;
	private SessionManager manager;
	private DAOManager daoManager;
	private ClassPathXmlApplicationContext context;
	private int guestId;
	private int defaultGuestGroupId;
	private PasswordEncoder passwordEncoder;

	private SystemContextService() throws Exception {
	}

	private void setUpDaoManagerEnvironment() {
		if (daoManagerImplClass == null || "".equals(daoManagerImplClass))
			daoManagerImplClass = DEFAULT_DAO_MANAGER_IMPL_CLASS;
		System.setProperty(PojoUtil.INFO_DAO_MANAGER_IMPL_CLASS,
				daoManagerImplClass);

		if (daoConfigFileName == null || "".equals(daoConfigFileName))
			daoConfigFileName = DEFAULT_DAO_CFG_FILE_NAME;
	}

	private void setUpJaasConfig() {
		if (getJaasConfigFile() == null || "".equals(getJaasConfigFile()))
			setJaasConfigFile(DEFAULT_JAAS_CONFIG);
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(getJaasConfigFile());
		Security.setProperty("login.config.url.1", url.toString());
	}

	private void initSystemData() throws Exception {
		SystemDataInitializer initializer = (SystemDataInitializer) context
				.getBean("systemDataInitializer");
		UserService userService = (UserService) context.getBean("userService");
		initializer.init(getDaoManager(), passwordEncoder, userService);
	}

	private void setUpGuestData() {
		SystemStatisticianDAO systemStatisticianDAO = daoManager
				.getSystemStatisticianDAO();
		SystemStatistician systemStatistician = systemStatisticianDAO
				.getSystemStatistician();
		guestId = systemStatistician.getGuestId();
		defaultGuestGroupId = systemStatistician.getDefaultGuestGroupId();
	}

	public synchronized void shutdown() {
		context = null;
		manager = null;
		daoManager = null;
		service = null;
		SessionManager.shutdown();
		PojoUtil.destroy();
		ModeratorManager.destroy();
		CacheService.destroy();
	}

	public synchronized static SystemContextService init() throws Exception {
		service = new SystemContextService();
		service.setUpDaoManagerEnvironment();
		service.setUpJaasConfig();
		service.setManager(SessionManager.getManager());
		DAOManager localDaoManager = PojoUtil.getDAOManager(extendedBeans,
				extendedConfigs, daoConfigFileName);
		service.setDaoManager(localDaoManager);
		if (localDaoManager.isTransactionSupported()) {
			localDaoManager.beginTransaction();
		}
		service.setState(service.new SystemState());

		ClassPathXmlApplicationContext context = null;
		if (externalConfigXmls != null && externalConfigXmls.size() != 0) {
			externalConfigXmls.add("info-context-beans.xml");
			String[] xmls = new String[externalConfigXmls.size()];
			externalConfigXmls.toArray(xmls);
			context = new ClassPathXmlApplicationContext(xmls);
		} else
			context = new ClassPathXmlApplicationContext(
					"info-context-beans.xml");
		service.setContext(context);

		service.setPasswordEncoder((PasswordEncoder) context
				.getBean("passwordEncoder"));
		service.initSystemData();
		service.setUpGuestData();
		RuntimeParameterService runtimeParameterService = (RuntimeParameterService) context
				.getBean("runtimeParameterService");
		SystemContextConfig config = runtimeParameterService
				.getSystemContextConfig();

		ModeratorManager.init();
		service.initSessionManager(config);
		if (localDaoManager.isTransactionSupported()) {
			localDaoManager.commitTransaction();
		}

		return service;
	}

	private void initSessionManager(SystemContextConfig config) {
		manager.setSessionTiming(config.getSessionTiming());
		manager.setLoginSessionTiming(config.getLoginSessionTiming());
		manager.setAcctActivationSessionTiming(config
				.getAcctActivationSessionTiming());

		manager.startExpireSessionRobots(config.getThreadPoolSize(),
				config.getCheckUserSessionDelay(),
				config.getCheckGuestSessionDelay(),
				config.getCheckAcctActivationSessionDelay());
	}

	public SystemState getState() {
		return state;
	}

	public UserSession getNewUserSession(String locale, String ip) {
		UserSession session = manager.getNewUserSession(locale, ip, guestId);
		if (logger.isDebugEnabled()) {
			logger.debug("Method getNewUserSession - Thread:"
					+ Thread.currentThread().getName()
					+ " New session generated - session id:"
					+ session.getSessionId() + ",ip:" + ip + "\n");
		}
		state.checkPeakOnlineUserNum();
		return session;
	}

	public String addNewAcctActivationSession(int userId) {
		return manager.addNewAcctActivationSession(userId);
	}

	public Map<String, UserSession> getSessions() {
		return manager.getSessions();
	}

	public Map<String, UserSession> getLoginSessions() {
		return manager.getLoginSessions();
	}

	public boolean isAcctActivationSessionValid(String sessionId, int userId) {
		return manager.isAcctActivationSessionValid(sessionId, userId);
	}

	private String checkUserReLogin(int userId) {
		return manager.checkUserReLogin(userId);
	}

	/**
	 * Login in a user
	 * 
	 * @param session
	 * @param userId
	 * @param password
	 * @return
	 * @throws LoginException
	 */
	private User login(UserSession session, int userId, String password)
			throws LoginException {
		String staleSessionId = checkUserReLogin(userId);
		if (staleSessionId != null) {
			logout(staleSessionId);
		}

		User user = manager.login(session, userId, password);
		user.setLastLoginDate(new Date());
		UserDAO dao = getDaoManager().getUserDAO();
		dao.updateUser(user);

		return user;
	}

	/**
	 * 
	 * @param session
	 * @param authentication
	 * @return
	 * @throws LoginException
	 */
	public Authentication login(UserSession session,
			Authentication authentication) throws LoginException {
		int userId = Integer.parseInt((String) authentication.getPrincipal());
		if (userId != guestId) {
			synchronized (session) {
				if (logger.isDebugEnabled()) {
					logger.debug("Method login - Logging in with thread:"
							+ Thread.currentThread().getName() + ",user id:"
							+ authentication.getPrincipal() + ",session id:"
							+ session.getSessionId() + "\n");
				}

				if (!session.isLogin()) {
					String password = (String) authentication.getCredentials();
					password = passwordEncoder.encodePassword(password,
							authentication.getPrincipal());
					login(session, userId, password);
					AuthenticationManager authenticationManager = (AuthenticationManager) context
							.getBean("authenticationManager");
					Authentication result = authenticationManager
							.authenticate(authentication);
					session.setAuthentication(result);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Method login - Done of logging in with thread:"
							+ Thread.currentThread().getName()
							+ ",user id:"
							+ authentication.getPrincipal()
							+ ",session id:"
							+ session.getSessionId() + "\n");
				}
			}

			return session.getAuthentication();
		} else {
			GroupService groupService = service
					.getDataManagementService(ServiceEnum.GROUP_SERVICE);
			Group defaultGuestGroup = groupService
					.getGroupById(defaultGuestGroupId);
			User guest = daoManager.getUserDAO().getUserById(guestId);

			return PermissionUtils.generateGuestPermissionToken(
					defaultGuestGroup, guest);
		}
	}

	public void autoLogin(UserSession session, int userId, String password)
			throws LoginException {
		synchronized (session) {
			if (logger.isDebugEnabled()) {
				logger.debug("Method login - Logging in with thread:"
						+ Thread.currentThread().getName() + ",user id:"
						+ userId + ",session id:" + session.getSessionId()
						+ "\n");
			}

			if (!session.isLogin()) {
				login(session, userId, password);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Method login - Done of logging in with thread:"
						+ Thread.currentThread().getName() + ",user id:"
						+ userId + ",session id:" + session.getSessionId()
						+ "\n");
			}
		}
	}

	/**
	 * Used to login out a user
	 * 
	 * @param sessionId
	 * @throws LoginException
	 */
	private void logout(String sessionId) throws LoginException {
		logout(manager.getLoginUserSession(sessionId));
	}

	/**
	 * Used to login out a user
	 * 
	 * @param session
	 * @throws LoginException
	 */
	public void logout(UserSession session) throws LoginException {
		if (session.isLogin()) {
			manager.logout(session);
		}
	}

	public ApplicationContext getSpringContext() {
		return context;
	}

	public Object getSpringBean(String beanName) {
		return context.getBean(beanName);
	}

	public boolean isInNewInfoInterval(String userSessionId,
			long newInfoInterval) {
		UserSession session = manager.getLoginUserSession(userSessionId);
		if (session == null)
			session = manager.getSession(userSessionId);
		Date lastInfoDate = session.getLastInfoDate();
		if (lastInfoDate != null && newInfoInterval > 0) {
			Date date = new Date();
			long interval = date.getTime() - lastInfoDate.getTime();
			if (newInfoInterval > interval)
				return true;
		}

		return false;
	}

	public boolean isInNewCmtInterval(String userSessionId, long newCmtInterval) {
		UserSession session = manager.getLoginUserSession(userSessionId);
		if (session == null)
			session = manager.getSession(userSessionId);
		Date lastCmtDate = session.getLastCmtDate();
		if (lastCmtDate != null && newCmtInterval > 0) {
			Date date = new Date();
			long interval = date.getTime() - lastCmtDate.getTime();
			if (newCmtInterval > interval)
				return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> T getDataManagementService(Class<T> clazz) {
		String serviceName = StringUtils.uncapitalize(clazz.getSimpleName());
		return (T) context.getBean(serviceName);
	}

	@SuppressWarnings("unchecked")
	public <T extends ExtendedBeanService<? extends ExtendedBean>> T getExtendedBeanService(
			Class<T> clazz) {
		String serviceName = StringUtils.uncapitalize(clazz.getSimpleName());
		return (T) context.getBean(serviceName);
	}

	public boolean isTransactionSupported() {
		return getDaoManager().isTransactionSupported();
	}

	public void beginTransaction() {
		getDaoManager().beginTransaction();
	}

	public void commitTransaction() {
		getDaoManager().commitTransaction();
	}

	public boolean isTransactionAlive() {
		return getDaoManager().isTransactionAlive();
	}

	public void rollbackTransaction() {
		getDaoManager().rollbackTransaction();
	}

	public static String getDaoManagerImplClass() {
		return daoManagerImplClass;
	}

	public static void setDaoManagerImplClass(String daoManagerImplClass) {
		SystemContextService.daoManagerImplClass = daoManagerImplClass;
	}

	public static String getJaasConfigFile() {
		return jaasConfigFile;
	}

	public static void setJaasConfigFile(String jaasConfigFile) {
		SystemContextService.jaasConfigFile = jaasConfigFile;
	}

	public static Set<Class<? extends Config>> getExtendedConfigs() {
		return extendedConfigs;
	}

	public static void setExtendedConfigs(
			Set<Class<? extends Config>> extendedConfigs) {
		SystemContextService.extendedConfigs = extendedConfigs;
	}

	public DAOManager getDaoManager() {
		return daoManager;
	}

	private void setDaoManager(DAOManager daoManager) {
		this.daoManager = daoManager;
	}

	public int getGuestId() {
		return guestId;
	}

	public static Set<Class<? extends ExtendedBean>> getExtendedBeans() {
		return extendedBeans;
	}

	public static void setExtendedBeans(
			Set<Class<? extends ExtendedBean>> extendedBeans) {
		SystemContextService.extendedBeans = extendedBeans;
	}

	/**
	 * Reserved for aop advisor usage, which uses reflection to set value
	 * 
	 * @param sessionTiming
	 */
	@SuppressWarnings("unused")
	private void setSessionTiming(int sessionTiming) {
		manager.setSessionTiming(sessionTiming);
	}

	/**
	 * Reserved for aop advisor usage, which uses reflection to set value
	 * 
	 * @param loginSessionTiming
	 */
	@SuppressWarnings("unused")
	private void setLoginSessionTiming(long loginSessionTiming) {
		manager.setLoginSessionTiming(loginSessionTiming);
	}

	private void setManager(SessionManager manager) {
		this.manager = manager;
	}

	private void setState(SystemState state) {
		this.state = state;
	}

	private void setContext(ClassPathXmlApplicationContext context) {
		this.context = context;
	}

	public static void setExternalConfigXmls(Set<String> externalConfigXmls) {
		SystemContextService.externalConfigXmls = externalConfigXmls;
	}

	/**
	 * @return the daoConfigFileName
	 */
	public static String getDaoConfigFileName() {
		return daoConfigFileName;
	}

	/**
	 * @param daoConfigFileName
	 *            the daoConfigFileName to set
	 */
	public static void setDaoConfigFileName(String daoConfigFileName) {
		SystemContextService.daoConfigFileName = daoConfigFileName;
	}

	public int getDefaultGuestGroupId() {
		return defaultGuestGroupId;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public class SystemState {
		private long registeredUserNum = 0;
		private int peakOnlineUserNum = 0;
		private Date peakOnlineUserDate;
		private long commentNum;
		private int commentPerDay;
		private long infoNum;
		private int infoPerDay;
		private long userNum;
		private long userPerDay;

		private User latestRegisteredUser;

		private SystemState() {
			UserDAO userDao = PojoUtil.getDAOManager().getUserDAO();
			registeredUserNum = userDao.getUserNum();
			latestRegisteredUser = userDao.getLatestUser();

			SystemStatisticianDAO systemStatisticianDAO = PojoUtil
					.getDAOManager().getSystemStatisticianDAO();
			SystemStatistician statistician = systemStatisticianDAO
					.getSystemStatistician();
			if (statistician == null) {
				statistician = new SystemStatistician();
				statistician.setMostUsersEverOnlineNum(0);
				statistician.setMostUsersEverOnlineDate(new Date());
				systemStatisticianDAO.saveSystemStatistician(statistician);
			}

			peakOnlineUserNum = statistician.getMostUsersEverOnlineNum();
			peakOnlineUserDate = statistician.getMostUsersEverOnlineDate();

			Date today = new Date();
			setCommentStatistic(today);
			setInfoStatistic(today);
			setUserStatistic(today);
		}

		public int getOnlineUserNum() {
			return getGuestNum() + getLoginUserNum();
		}

		private synchronized void checkPeakOnlineUserNum() {
			int guestNum = getGuestNum();
			int loginUserNum = getLoginUserNum();
			int onlineUserNum = guestNum + loginUserNum;
			if (onlineUserNum > peakOnlineUserNum) {
				peakOnlineUserNum++;
				SystemStatisticianDAO dao = PojoUtil.getDAOManager()
						.getSystemStatisticianDAO();
				SystemStatistician statistician = PojoUtil.getDAOManager()
						.getSystemStatisticianDAO().getSystemStatistician();
				statistician.addMostUsersEverOnlineNum();
				Date date = new Date();
				peakOnlineUserDate = date;
				statistician.setMostUsersEverOnlineDate(date);
				dao.updateSystemStatistician(statistician);
			}
		}

		public int getGuestNum() {
			return manager.getGuestNum();
		}

		public int getLoginUserNum() {
			return manager.getLoginUserNum();
		}

		public int getPeakOnlineUserNum() {
			return peakOnlineUserNum;
		}

		public Date getPeakOnlineUserDate() {
			return peakOnlineUserDate;
		}

		public int getSystemCommentNum() {
			CategoryService categoryService = getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
			return categoryService.getCategoriesCmtNum();
		}

		public long getRegisteredUserNum() {
			return registeredUserNum;
		}

		public User getLatestRegisteredUser() {
			return latestRegisteredUser;
		}

		public void updateLatestRegisteredUser(User user) {
			synchronized (registeredUserLock) {
				this.latestRegisteredUser = user;
				registeredUserNum++;
			}
		}

		private void setCommentStatistic(Date today) {
			CommentDAO cmtDao = daoManager.getCommentDAO();
			Comment cmt = cmtDao.getFirstComment();
			if (cmt != null) {
				Date createdTime = cmt.getCreatedTime();
				int days = CommonUtil.daysUntilToday(today, createdTime);
				commentNum = cmtDao.getCommentNumOfAllInfos();
				commentPerDay = new Long(commentNum / days).intValue();
			}
		}

		private void setInfoStatistic(Date today) {
			InfoDAO infoDao = daoManager.getInfoDAO();
			Info info = infoDao.getFirstInfo();
			if (info != null) {
				Date createdTime = info.getCreatedTime();
				int days = CommonUtil.daysUntilToday(today, createdTime);
				infoNum = infoDao.getAllInfoNum();
				infoPerDay = new Long(infoNum / days).intValue();
			}
		}

		private void setUserStatistic(Date today) {
			UserDAO userDao = daoManager.getUserDAO();
			User user = userDao.getFirstUser();
			if (user != null) {
				Date userRegTime = user.getRegTime();
				userNum = userDao.getUserNum();
				int days = CommonUtil.daysUntilToday(today, userRegTime);
				userPerDay = userNum / days;
			}
		}

		public long getCommentNum() {
			return commentNum;
		}

		public int getCommentPerDay() {
			return commentPerDay;
		}

		public long getInfoNum() {
			return infoNum;
		}

		public int getInfoPerDay() {
			return infoPerDay;
		}

		public long getUserPerDay() {
			return userPerDay;
		}

		public long getUserNum() {
			return userNum;
		}

		public void setUserNum(int userNum) {
			this.userNum = userNum;
		}
	}
}
