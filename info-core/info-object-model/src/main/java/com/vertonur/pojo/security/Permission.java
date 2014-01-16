package com.vertonur.pojo.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "INFO_COR_PERMS")
public class Permission {

	public enum Level {
		Level_1, Level_2, Level_3, Level_4, Level_5, Level_6
	}

	public enum Component {
		DEPARTMENT, CATEGORY, GROUP, USER, RANKING, RUNTIME_PARAMETER, BACKEND_PERMISSION
	}

	public enum PermissionType {
		READ_DEPARTMENT, WRITE_DEPARTMENT, READ_CATEGORY, WRITE_CATEGORY, AUDIT_CATEGORY, MODERATE_CNT, REMOVE_CNT, EDIT_CNT, SAVE_CMT, SAVE_INFO, UPLOAD_ATTM, DOWNLOAD_ATTM, OPERATE_GROUP, OPERATE_USER, OPERATE_DEPARTMENT, OPERATE_CATEGORY, OPERATE_RANKING, CONFIG_POINTS, CONFIG_RUNTIME_PARATEMER, CONFIG_BACKEND_PERMISSION, MOVE_INFO, INFO_LOCK
	}

	private int id;
	private String name;
	private String description;
	private Component component;
	private PermissionType type;
	private String proprietaryMark = "";
	private Level level;

	private Set<Group> groups = new HashSet<Group>();

	@Id
	@GeneratedValue
	@Column(name = "PERM_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "PERM_NME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(mappedBy = "permissions")
	public Set<Group> getGroups() {
		return groups;
	}

	/**
	 * Only used by orm to set values
	 * 
	 * @param groups
	 */
	@SuppressWarnings("unused")
	private void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	/**
	 * Add a group to this permission if it's not presented.
	 * 
	 * @param group
	 * @return boolean return true if it's added
	 */
	public boolean addGroup(Group group) {
		return groups.add(group);
	}

	public void removeGroup(Group group) {
		groups.remove(group);
	}

	@Column(name = "PERM_DESC")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "PERM_COMP")
	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	@Column(name = "PERM_TYP")
	public PermissionType getType() {
		return type;
	}

	public void setType(PermissionType type) {
		this.type = type;
	}

	@Column(name = "PERM_MRK")
	public String getProprietaryMark() {
		return proprietaryMark;
	}

	public void setProprietaryMark(String proprietaryMark) {
		this.proprietaryMark = proprietaryMark;
	}

	@Column(name = "PERM_LV")
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Permission))
			return false;

		Permission permission = (Permission) obj;
		if (permission.getProprietaryMark().equals(proprietaryMark)
				&& permission.getName().equals(name)
				&& permission.getLevel().equals(level)
				&& permission.getComponent().equals(component)
				&& permission.getType().equals(type))
			return true;
		else
			return false;
	}

	public int hashCode() {
		int proprietaryMarkCode = proprietaryMark.hashCode();
		int nameCode = name.hashCode();
		int levelCode = level.hashCode();
		int componentCode = component.hashCode();
		int typeCode = type.hashCode();
		int hashCode = new Integer(proprietaryMarkCode + nameCode + levelCode
				+ componentCode + typeCode).hashCode();
		return hashCode;
	}
}
