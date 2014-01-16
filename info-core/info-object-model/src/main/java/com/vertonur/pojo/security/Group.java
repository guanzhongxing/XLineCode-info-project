package com.vertonur.pojo.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.vertonur.pojo.User;

@Entity(name = "INFO_COR_GRP")
public class Group {
	public static final String SET_BACKEND_PERMS_METHOD_NAME = "setBackendPermissions";

	public static enum GroupType {
		GENERIC_GST, GENERIC_USR, SUPER_ADMIN, GENERIC_ADMIN, GENERIC_MDR
	}

	private int id;
	private String name;
	private String description;
	private GroupType groupType;
	private int nestedLevel;
	private boolean deprecated = false;
	private boolean deletable = true;
	private boolean editable = true;
	private Group parent;
	private Set<User> users = new HashSet<User>();
	private Set<Role> roles = new HashSet<Role>();
	private Set<Group> subGroups = new HashSet<Group>();
	private Set<Permission> permissions = new HashSet<Permission>();
	private Set<Permission> backendPermissions = new HashSet<Permission>();

	public Group() {
	}

	@Id
	@GeneratedValue
	@Column(name = "GRP_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "GRP_NME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "GRP_DESC")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "GRP_TYP")
	public GroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}

	@Column(name = "GRP_NSTED_LV")
	public int getNestedLevel() {
		return nestedLevel;
	}

	public void setNestedLevel(int nestedLevel) {
		this.nestedLevel = nestedLevel;
	}

	@Column(name = "GRP_DEPRECATED")
	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	@Column(name = "GRP_DELETABLE")
	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	@Column(name = "GRP_EDITABLE")
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@ManyToOne(targetEntity = Group.class)
	@JoinColumn(name = "GRP_PARENT")
	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	@ManyToMany(mappedBy = "groups")
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	@ManyToMany
	@JoinTable(name = "INFO_COR_GRP_ROLE", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {
		roles.add(role);
	}

	@OneToMany(mappedBy = "parent")
	public Set<Group> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(Set<Group> subGroups) {
		this.subGroups = subGroups;
	}

	public void addSubGroup(Group subGroup) {
		subGroups.add(subGroup);
	}

	public void addSubGroups(Set<Group> groups) {
		subGroups.addAll(groups);
	}

	public void removeSubGroup(Group subGroup) {
		subGroups.remove(subGroup);
	}

	@ManyToMany
	@JoinTable(name = "INFO_COR_GRP_PERM", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "PERM_ID") })
	public Set<Permission> getPermissions() {
		return new HashSet<Permission>(permissions);
	}

	/**
	 * Only used by orm to set values
	 * 
	 * @param permissions
	 */
	@SuppressWarnings("unused")
	private void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public void addPermission(Permission permission) {
		permissions.add(permission);
	}

	public void addPermissions(Set<Permission> permissions) {
		this.permissions.addAll(permissions);
	}

	public void removePermission(Permission permission) {
		permissions.remove(permission);
	}

	public void removePermissions(Set<Permission> permissions) {
		this.permissions.removeAll(permissions);
	}

	@ManyToMany
	@JoinTable(name = "INFO_COR_GRP_BD_PERM", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "PERM_ID") })
	public Set<Permission> getBackendPermissions() {
		return new HashSet<Permission>(backendPermissions);
	}

	/**
	 * Only used by orm to set values
	 * 
	 * @param permissions
	 */
	@SuppressWarnings("unused")
	private void setBackendPermissions(Set<Permission> backendPermissions) {
		this.backendPermissions = backendPermissions;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Group))
			return false;

		Group group = (Group) obj;
		if (group.getGroupType().equals(groupType)
				&& group.getName().equals(name)
				&& group.getNestedLevel() == nestedLevel)
			return true;
		else
			return false;
	}

	public int hashCode() {
		int groupTypeCode = groupType.hashCode();
		int nameCode = name.hashCode();
		int nestedLevelCode = new Integer(nestedLevel).hashCode();
		int hashCode = new Integer(groupTypeCode + nameCode + nestedLevelCode)
				.hashCode();
		return hashCode;
	}
}
