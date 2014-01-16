package com.vertonur.security.spring.aop.datasource;

import com.vertonur.pojo.security.Permission;

public class FixedValuePermissionDataSource extends PermissionDataSource {

	private String proprietaryMark;

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		permissionExample.setProprietaryMark(proprietaryMark);
	}

	public void setProprietaryMark(String proprietaryMark) {
		this.proprietaryMark = proprietaryMark;
	}
}
