package com.vertonur.security.spring.aop.datasource;

import java.util.HashSet;
import java.util.Set;

import com.vertonur.dao.api.PermissionDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.security.Permission;
import com.vertonur.pojo.security.Permission.Component;
import com.vertonur.security.exception.PermissionProprietaryMarkNotSetException;
import com.vertonur.security.util.PermissionUtils;

public abstract class PermissionDataSource {

	private Set<Permission> permissionExamples;
	private int paramIndex;

	public Set<Permission> getPermissions(
			Object[] paramArrayOfObject) {
		return getPermissionSet(paramArrayOfObject[getParamIndex()]);
	}

	public Set<Permission> getPermissionSet(Object param) {
		Set<Permission> permissions = new HashSet<Permission>();
		PermissionDAO dao = PojoUtil.getDAOManager().getPermissionDAO();
		for (Permission permissionExample : permissionExamples) {
			setProprietaryMark(permissionExample, param);
			Permission permission = dao.findByExample(permissionExample).get(0);
			permissions.add(permission);
		}

		return permissions;
	}

	protected abstract void setProprietaryMark(Permission permissionExample,
			Object param);

	/**
	 * Set proprietary mark according to component type of permissionExample, if
	 * the type equals to Permission.Component.CATEGORY then set proprietary
	 * mark to id of category, if the type equals to
	 * Permission.Component.DEPARTMENT the get department id from category and
	 * set proprietary mark to it and throw
	 * PermissionProprietaryMarkNotSetException if none of those two condition
	 * is met.
	 * 
	 * @param permissionExample
	 * @param category
	 */
	protected void setProprietaryMark(Permission permissionExample,
			Category category) {
		Component component = permissionExample.getComponent();
		if (Permission.Component.CATEGORY.equals(component))
			permissionExample.setProprietaryMark(String.valueOf(category
					.getId()));
		else if (Permission.Component.DEPARTMENT.equals(component))
			permissionExample.setProprietaryMark(String.valueOf(category
					.getDepartment().getId()));
		else
			throw new PermissionProprietaryMarkNotSetException(
					PermissionUtils
							.assemblePermissionProprietaryMarkNotSetMsg(permissionExample));
	}

	protected void setProprietaryMark(Permission permissionExample,
			AbstractInfo content) {
		Category category;
		if (content instanceof Info) {
			Info info = (Info) content;
			category = info.getCategory();
		} else {
			Comment comment = (Comment) content;
			category = comment.getInfo().getCategory();
		}

		setProprietaryMark(permissionExample, category);
	}

	public int getParamIndex() {
		return paramIndex;
	}

	public void setParamIndex(int paramIndex) {
		this.paramIndex = paramIndex;
	}

	public Set<Permission> getPermissionExamples() {
		return permissionExamples;
	}

	public void setPermissionExamples(Set<Permission> permissionExamples) {
		this.permissionExamples = permissionExamples;
	}
}
