package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.security.Permission;

public interface PermissionDAO extends GenericDAO<Permission, Integer> {

	public Integer savePermission(Permission permission);

	public void deletePermission(Permission permission);

	public Permission getPermissionById(int id);

	List<Permission> findByExample(Permission exampleInstance);
}
