package com.vertonur.security.spring.aop.advice;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.dao.api.PermissionDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.dms.GroupService;
import com.vertonur.dms.exception.Assigned2SubGroupException;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;
import com.vertonur.security.spring.PermissionAuthenticationToken;

public class AddRelatedPermissionAdvice implements AfterReturningAdvice {

	private Set<Permission> permissionTemplates;
	private List<Group> groupTemplates;
	private GroupService groupService;

	/**
	 * Persist all permissions set from permissionTemplates and add those to the
	 * groups set
	 */
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {
		PermissionDAO dao = PojoUtil.getDAOManager().getPermissionDAO();
		PermissionAuthenticationToken token = (PermissionAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		for (Permission permissionTemplate : permissionTemplates) {
			permissionTemplate.setProprietaryMark(returnValue.toString());
			dao.savePermission(permissionTemplate);
			addPermission2DefaultGroups(permissionTemplate);

			token.addPermission(permissionTemplate);
		}
	}

	private void addPermission2DefaultGroups(Permission permission) {
		for (Group groupTemplate : groupTemplates) {
			Group group = groupService.findByExample(groupTemplate).get(0);
			group.addPermission(permission);
			try {
				groupService.updateGroup(group);
			} catch (Assigned2SubGroupException e) {
				new RuntimeException(
						"Shit happens! System aop method [addPermission2DefaultGroups] will not act on changing group relationship of any group.");
			}
		}
	}

	public void setPermissionTemplates(Set<Permission> permissionTemplates) {
		this.permissionTemplates = permissionTemplates;
	}

	/**
	 * @param groupTemplates
	 *            the groupTemplates to set
	 */
	public void setGroupTemplates(List<Group> groupTemplates) {
		this.groupTemplates = groupTemplates;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}
}
