package com.vertonur.dms;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.Assigned2SubGroupException;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;

public interface GroupService {

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public Integer saveGroup(Group group);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updateGroup(Group group) throws Assigned2SubGroupException;

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void deleteGroup(int groupId);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void deleteGroup(Group group);

	public Group getGroupById(int id);

	public List<Group> getGroups();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public List<Group> getTopLevelGroups();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public List<Group> getAdminTopLevelGroups();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public List<Group> findByExample(Group example);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updateBackendPermissions(Group group,
			Set<Permission> backendPermissions);
}
