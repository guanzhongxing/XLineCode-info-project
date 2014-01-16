package com.vertonur.dms;

import java.util.List;

import com.vertonur.dao.api.PermissionDAO;
import com.vertonur.pojo.security.Permission;

public class PermissionService extends GenericService {

	private PermissionDAO dao;

	protected PermissionService() {
		dao = manager.getPermissionDAO();
	}

	public Integer savePermission(Permission permission) {
		return dao.savePermission(permission);
	}

	public void deletePermission(Permission permission) {
		dao.deletePermission(permission);
	}

	public Permission getPermissionById(int id) {
		return dao.getPermissionById(id);
	}

	public List<Permission> findByExample(Permission exampleInstance) {
		return dao.findByExample(exampleInstance);
	}
}
