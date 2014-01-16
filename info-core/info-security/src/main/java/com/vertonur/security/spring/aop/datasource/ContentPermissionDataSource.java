package com.vertonur.security.spring.aop.datasource;

import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.security.Permission;

public class ContentPermissionDataSource extends PermissionDataSource {

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		setProprietaryMark(permissionExample, (AbstractInfo)param);
	}
}
