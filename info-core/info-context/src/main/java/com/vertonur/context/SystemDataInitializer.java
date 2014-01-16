package com.vertonur.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.vertonur.dao.api.AdminDAO;
import com.vertonur.dao.api.SystemStatisticianDAO;
import com.vertonur.dao.api.UserDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Role;
import com.vertonur.pojo.statistician.SystemStatistician;

public class SystemDataInitializer {

	private User guest;
	private Admin superAdmin;
	private Moderator moderator;
	private AttachmentInfo attachmentInfo;
	private AuthorityInitializer authorityInitializer;
	private RuntimeParameterInitializer runtimeParameterInitializer;
	private BackendPermissionInitializer backendPermissionInitializer;

	public void init(DAOManager daoManager, PasswordEncoder passwordEncoder)
			throws IllegalAccessException, InvocationTargetException {
		AdminDAO adminDao = daoManager.getAdminDAO();
		UserDAO userDao = daoManager.getUserDAO();
		long num = adminDao.getAdminNum();
		if (num == 0) {
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

			attachmentInfo.setUploadTime(new Date());
			AttachmentInfo newInfo = new AttachmentInfo();
			BeanUtils.copyProperties(newInfo, attachmentInfo);
			Attachment avatar = new Attachment();
			avatar.setAttmInfo(newInfo);
			avatar.setUploader(superAdmin);
			superAdmin.setAvatar(avatar);
			userDao.updateUser(superAdmin);

			Group defaultGuestGroup = authorityInitializer
					.initDefaultGuestGroup(daoManager);
			guest.addGroup(defaultGuestGroup);
			id = userDao.saveUser(guest);
			password = guest.getPassword();
			password = passwordEncoder.encodePassword(password, id);
			guest.setPassword(password);

			newInfo = new AttachmentInfo();
			BeanUtils.copyProperties(newInfo, attachmentInfo);
			avatar = new Attachment();
			avatar.setAttmInfo(newInfo);
			avatar.setUploader(guest);
			guest.setAvatar(avatar);
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

			runtimeParameterInitializer.init(daoManager);
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

	public AttachmentInfo getAttachmentInfo() {
		return attachmentInfo;
	}

	public void setAttachmentInfo(AttachmentInfo attachmentInfo) {
		this.attachmentInfo = attachmentInfo;
	}
}
