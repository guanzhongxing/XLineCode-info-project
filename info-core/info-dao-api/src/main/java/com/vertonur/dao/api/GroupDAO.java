package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.security.Group;

public interface GroupDAO extends GenericDAO<Group, Integer> {

	public Integer saveGroup(Group group);

	public void updateGroup(Group group);

	public void deleteGroup(Group group);

	public Group getGroupById(int id);

	public List<Group> getGroups();

	public List<Group> getAdminGroups();

	public List<Group> getTopLevelGroups();

	public List<Group> getAuditPermissionGroups(int categoryId);
}
