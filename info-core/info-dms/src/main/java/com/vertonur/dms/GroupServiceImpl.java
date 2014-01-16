package com.vertonur.dms;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.dao.api.GroupDAO;
import com.vertonur.dms.exception.Assigned2SubGroupException;
import com.vertonur.dms.exception.FailToSetPropertyException;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Group.GroupType;
import com.vertonur.pojo.security.Permission;

public class GroupServiceImpl extends GenericService implements GroupService {
	private static Logger logger = LoggerFactory
			.getLogger(GroupServiceImpl.class.getCanonicalName());

	private GroupDAO dao;

	protected GroupServiceImpl() {
		dao = manager.getGroupDAO();
	}

	@Override
	public Integer saveGroup(Group group) {
		return dao.saveGroup(group);
	}

	@Override
	public void updateGroup(Group group) throws Assigned2SubGroupException {
		// TODO: limit group relationship changing to dedicated methods with
		// only group id and it sub groups parameters
		Set<Integer> ids = new HashSet<Integer>();
		iterateGroups(ids, group);
		if (ids.contains(group.getId()))
			throw new Assigned2SubGroupException(group, group.getParent());
		dao.updateGroup(group);

		if (group.getGroupType() == GroupType.GENERIC_MDR) {
			Set<User> users = group.getUsers();
			ModeratorManager moderatorManager = ModeratorManager.getManager();
			for (User user : users)
				moderatorManager.rearrangeModeratorWorkload((Moderator) user);
		}
	}

	private void iterateGroups(Set<Integer> ids, Group group) {
		Set<Group> groups = group.getSubGroups();
		if (groups != null && groups.size() != 0) {
			for (Group subGroup : groups) {
				ids.add(subGroup.getId());
				iterateGroups(ids, subGroup);
			}
		}
	}

	@Override
	public void deleteGroup(int groupId) {
		deleteGroup(dao.getGroupById(groupId));
	}

	@Override
	public void deleteGroup(Group group) {
		Set<Group> groups = group.getSubGroups();
		Group parent = group.getParent();
		for (Group subGroup : groups) {
			subGroup.setNestedLevel(group.getNestedLevel());
			subGroup.setParent(parent);
			dao.updateGroup(subGroup);
		}
		group.setSubGroups(null);
		if (parent != null) {
			parent.removeSubGroup(group);
			parent.addSubGroups(groups);
			group.setParent(null);
		}

		dao.deleteGroup(group);
	}

	@Override
	public Group getGroupById(int id) {
		return dao.getGroupById(id);
	}

	@Override
	public List<Group> getGroups() {
		return dao.getGroups();
	}

	@Override
	public List<Group> getTopLevelGroups() {
		return dao.getTopLevelGroups();
	}

	@Override
	public List<Group> findByExample(Group example) {
		return dao.findByExample(example);
	}

	public List<Group> getAuditPermissionGroups(int categoryId) {
		return dao.getAuditPermissionGroups(categoryId);
	}

	@Override
	public List<Group> getAdminTopLevelGroups() {
		// TODO: add related test cases
		List<Group> groups = dao.getAdminGroups();
		for (Group group : groups)
			if (hasAdminGroupParent(group))
				groups.remove(group);

		return groups;
	}

	private boolean hasAdminGroupParent(Group group) {
		Group parent = group.getParent();
		if (parent == null)
			return false;

		if (parent.getGroupType() != GroupType.GENERIC_ADMIN)
			return hasAdminGroupParent(parent);

		return true;
	}

	@Override
	public void updateBackendPermissions(Group group,
			Set<Permission> backendPermissions) {
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
	}
}
