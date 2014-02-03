package com.vertonur.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Set;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.vertonur.dao.api.AdminDAO;
import com.vertonur.dao.api.SystemStatisticianDAO;
import com.vertonur.dao.api.UserDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dms.UserService;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Role;
import com.vertonur.pojo.statistician.SystemStatistician;

public class SystemDataInitializer {

	private User guest;
	private Admin superAdmin;
	private Moderator moderator;
	private AuthorityInitializer authorityInitializer;
	private RuntimeParameterInitializer runtimeParameterInitializer;
	private BackendPermissionInitializer backendPermissionInitializer;

	public void init(DAOManager daoManager, PasswordEncoder passwordEncoder,
			UserService userService) throws IllegalAccessException,
			InvocationTargetException, IOException, URISyntaxException {
		AdminDAO adminDao = daoManager.getAdminDAO();
		UserDAO userDao = daoManager.getUserDAO();
		long num = adminDao.getAdminNum();
		if (num == 0) {
			runtimeParameterInitializer.init(daoManager);

			Set<Role> defaultUserRoles = authorityInitializer
					.initDefaultUserRoles(daoManager);
			Group defaultUserGroup = authorityInitializer.initDefaultUserGroup(
					daoManager, defaultUserRoles);
			Set<Role> adminRoles = authorityInitializer.initAdminRoles(
					daoManager, defaultUserRoles);
			authorityInitializer.initDefaultAdminGroup(daoManager, adminRoles,
					defaultUserGroup);
			Group superAdminGroup = authorityInitializer.initSuperAdminGroup(
					daoManager, adminRoles);
			Group defaultModeratorGroup = authorityInitializer
					.initDefaultModeratorGroup(daoManager);
			superAdmin.addGroup(defaultUserGroup);
			superAdmin.addGroup(superAdminGroup);
			superAdmin.addGroup(defaultModeratorGroup);

			Integer id = adminDao.saveAdmin(superAdmin);
			String password = superAdmin.getPassword();
			password = passwordEncoder.encodePassword(password, id);
			superAdmin.setPassword(password);

			AttachmentConfig config = runtimeParameterInitializer
					.getAttachmentConfig();
			AttachmentType uploadFileSystem = config.getUploadFileSystem();
			String avatarRoot = config.getAvatarRoot();
			userService.setUpDefaultAvatar(uploadFileSystem, avatarRoot,
					superAdmin);
			userDao.updateUser(superAdmin);

			Group defaultGuestGroup = authorityInitializer
					.initDefaultGuestGroup(daoManager);
			guest.addGroup(defaultGuestGroup);
			id = userDao.saveUser(guest);
			password = guest.getPassword();
			password = passwordEncoder.encodePassword(password, id);
			guest.setPassword(password);

			userService.setUpDefaultAvatar(uploadFileSystem, avatarRoot, guest);
			userDao.updateUser(guest);

			SystemStatisticianDAO systemStatisticianDAO = daoManager
					.getSystemStatisticianDAO();
			SystemStatistician systemStatistician = systemStatisticianDAO
					.getSystemStatistician();
			systemStatistician.setGuestId(guest.getId());
			systemStatistician
					.setDefaultGuestGroupId(defaultGuestGroup.getId());
			systemStatistician.setDefaultUserGroupId(defaultUserGroup.getId());
			systemStatisticianDAO.updateSystemStatistician(systemStatistician);

			backendPermissionInitializer.init(daoManager);
		}
	}

	public Admin getSuperAdmin() {
		return superAdmin;
	}

	public void setSuperAdmin(Admin superAdmin) {
		this.superAdmin = superAdmin;
	}

	public User getGuest() {
		return guest;
	}

	public void setGuest(User guest) {
		this.guest = guest;
	}

	public AuthorityInitializer getAuthorityInitializer() {
		return authorityInitializer;
	}

	public void setAuthorityInitializer(
			AuthorityInitializer authorityInitializer) {
		this.authorityInitializer = authorityInitializer;
	}

	public Moderator getModerator() {
		return moderator;
	}

	public void setModerator(Moderator moderator) {
		this.moderator = moderator;
	}

	public void setRuntimeParameterInitializer(
			RuntimeParameterInitializer runtimeParameterInitializer) {
		this.runtimeParameterInitializer = runtimeParameterInitializer;
	}

	public void setBackendPermissionInitializer(
			BackendPermissionInitializer backendPermissionInitializer) {
		this.backendPermissionInitializer = backendPermissionInitializer;
	}
}
