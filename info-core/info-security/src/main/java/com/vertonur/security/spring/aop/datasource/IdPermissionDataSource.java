package com.vertonur.security.spring.aop.datasource;

import com.vertonur.pojo.security.Permission;

public class IdPermissionDataSource extends PermissionDataSource {

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		permissionExample.setProprietaryMark(String.valueOf(param));
	}
}
