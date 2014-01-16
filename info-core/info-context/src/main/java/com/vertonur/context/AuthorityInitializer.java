package com.vertonur.context;

import java.util.HashSet;
import java.util.Set;

import com.vertonur.dao.api.GroupDAO;
import com.vertonur.dao.api.RoleDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Role;

class AuthorityInitializer {

	private Group defaultGuestGroupTemplate;
	private Group defaultUserGroupTemplate;
	private Group defaultAdminGroupTemplate;
	private Group superAdminGroupTemplate;
	private Group defaultModeratorGroupTemplate;

	public Set<Role> initDefaultUserRoles(DAOManager daoManager) {
		RoleDAO dao = daoManager.getRoleDAO();

		Role ROLE_USER = new Role();
		ROLE_USER.setName(RoleEnum.ROLE_USER);
		dao.saveRole(ROLE_USER);

		Set<Role> defaultUserRoles = new HashSet<Role>();
		defaultUserRoles.add(ROLE_USER);
		return defaultUserRoles;
	}

	public Group initDefaultGuestGroup(DAOManager daoManager) {
		RoleDAO dao = daoManager.getRoleDAO();
		Role ROLE_GUEST = new Role();
		ROLE_GUEST.setName(RoleEnum.ROLE_GUEST);
		dao.saveRole(ROLE_GUEST);

		GroupDAO groupDao = daoManager.getGroupDAO();
		defaultGuestGroupTemplate.addRole(ROLE_GUEST);
		groupDao.saveGroup(defaultGuestGroupTemplate);

		return defaultGuestGroupTemplate;
	}
	
	public Group initDefaultModeratorGroup(DAOManager daoManager) {
		RoleDAO dao = daoManager.getRoleDAO();
		Role ROLE_MODERATOR = new Role();
		ROLE_MODERATOR.setName(RoleEnum.ROLE_MODERATOR);
		dao.saveRole(ROLE_MODERATOR);

		GroupDAO groupDao = daoManager.getGroupDAO();
		defaultModeratorGroupTemplate.addRole(ROLE_MODERATOR);
		groupDao.saveGroup(defaultModeratorGroupTemplate);

		return defaultModeratorGroupTemplate;
	}

	public Group initDefaultUserGroup(DAOManager daoManager,
			Set<Role> defaultUserRoles) {
		GroupDAO groupDao = daoManager.getGroupDAO();
		defaultUserGroupTemplate.setRoles(defaultUserRoles);
		groupDao.saveGroup(defaultUserGroupTemplate);

		return defaultUserGroupTemplate;
	}

	public Set<Role> initAdminRoles(DAOManager daoManager,
			Set<Role> defaultUserRoles) {
		Set<Role> adminRoles = new HashSet<Role>(defaultUserRoles);

		RoleDAO dao = daoManager.getRoleDAO();
		Role ROLE_ADMIN = new Role();
		ROLE_ADMIN.setName(RoleEnum.ROLE_ADMIN);
		dao.saveRole(ROLE_ADMIN);

		adminRoles.add(ROLE_ADMIN);
		return adminRoles;
	}

	public Group initDefaultAdminGroup(DAOManager daoManager,
			Set<Role> adminRoles, Group defaultUserGroup) {
		defaultAdminGroupTemplate.setParent(defaultUserGroup);
		defaultAdminGroupTemplate.setRoles(adminRoles);
		GroupDAO groupDao = daoManager.getGroupDAO();
		groupDao.saveGroup(defaultAdminGroupTemplate);

		return defaultAdminGroupTemplate;
	}

	public Group initSuperAdminGroup(DAOManager daoManager,
			Set<Role> superAdminRoles) {
		superAdminGroupTemplate.setRoles(superAdminRoles);
		GroupDAO groupDao = daoManager.getGroupDAO();
		groupDao.saveGroup(superAdminGroupTemplate);

		return superAdminGroupTemplate;
	}

	public Group getDefaultUserGroupTemplate() {
		return defaultUserGroupTemplate;
	}

	public void setDefaultUserGroupTemplate(Group defaultUserGroupTemplate) {
		this.defaultUserGroupTemplate = defaultUserGroupTemplate;
	}

	public Group getDefaultAdminGroupTemplate() {
		return defaultAdminGroupTemplate;
	}

	public void setDefaultAdminGroupTemplate(Group defaultAdminGroupTemplate) {
		this.defaultAdminGroupTemplate = defaultAdminGroupTemplate;
	}

	public Group getSuperAdminGroupTemplate() {
		return superAdminGroupTemplate;
	}

	public void setSuperAdminGroupTemplate(Group superAdminGroupTemplate) {
		this.superAdminGroupTemplate = superAdminGroupTemplate;
	}

	public Group getDefaultGuestGroupTemplate() {
		return defaultGuestGroupTemplate;
	}

	public void setDefaultGuestGroupTemplate(Group defaultGuestGroupTemplate) {
		this.defaultGuestGroupTemplate = defaultGuestGroupTemplate;
	}

	/**
	 * @return the defaultModeratorGroupTemplate
	 */
	public Group getDefaultModeratorGroupTemplate() {
		return defaultModeratorGroupTemplate;
	}

	/**
	 * @param defaultModeratorGroupTemplate the defaultModeratorGroupTemplate to set
	 */
	public void setDefaultModeratorGroupTemplate(
			Group defaultModeratorGroupTemplate) {
		this.defaultModeratorGroupTemplate = defaultModeratorGroupTemplate;
	}
}
