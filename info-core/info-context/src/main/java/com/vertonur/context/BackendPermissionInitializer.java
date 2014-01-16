package com.vertonur.context;

import java.lang.reflect.Method;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.dao.api.GroupDAO;
import com.vertonur.dao.api.PermissionDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dms.exception.FailToSetPropertyException;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;

class BackendPermissionInitializer {
	private static Logger logger = LoggerFactory
			.getLogger(BackendPermissionInitializer.class.getCanonicalName());
	private Set<Permission> permissions;
	private Set<Group> groupTemplates;

	public void init(DAOManager daoManager) {
		PermissionDAO dao = daoManager.getPermissionDAO();
		GroupDAO groupDao = daoManager.getGroupDAO();
		for (Permission permission : permissions) {
			dao.savePermission(permission);
			for (Group groupTemplate : groupTemplates) {
				Group group = groupDao.findByExample(groupTemplate).get(0);
				Set<Permission> backendPermissions = group
						.getBackendPermissions();
				backendPermissions.add(permission);
				try {
					Method methodSetDepartment = Group.class.getDeclaredMethod(
							Group.SET_BACKEND_PERMS_METHOD_NAME, Set.class);
					methodSetDepartment.setAccessible(true);
					methodSetDepartment.invoke(group, backendPermissions);
				} catch (Exception e) {
					logger.error("Fails to set backend permissions to group with id:"
							+ group.getId());
					throw new FailToSetPropertyException(
							"Fails to set backend permissions to group with id:"
									+ group.getId()
									+ ", this exception might be caused by change of method name ["
									+ Group.SET_BACKEND_PERMS_METHOD_NAME
									+ "] of  "
									+ Group.class.getSimpleName()
									+ " class.Plz check the existence of the method for ["
									+ Group.class.getCanonicalName()
									+ "] or refer to log for more information.");
				}
				groupDao.updateGroup(group);
			}
		}
	}

	public void setGroupTemplates(Set<Group> groupTemplates) {
		this.groupTemplates = groupTemplates;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
}
