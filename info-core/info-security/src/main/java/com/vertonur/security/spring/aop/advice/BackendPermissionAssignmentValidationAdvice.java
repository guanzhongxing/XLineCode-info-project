package com.vertonur.security.spring.aop.advice;

import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.aop.MethodBeforeAdvice;

import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;
import com.vertonur.security.exception.BackendPermissionAssignmentException;
import com.vertonur.security.spring.aop.datasource.PermissionDataSource;

public class BackendPermissionAssignmentValidationAdvice implements
		MethodBeforeAdvice {

	private PermissionDataSource dataSource;

	public void before(Method paramMethod, Object[] paramArrayOfObject,
			Object paramObject) throws Throwable {
		Set<Permission> backendPermissions = dataSource
				.getPermissions(paramArrayOfObject);
		Group group = (Group) paramArrayOfObject[0];
		Set<Permission> permissions = group.getPermissions();
		for (Permission backendPermission : backendPermissions)
			if (permissions.contains(backendPermission))
				throw new BackendPermissionAssignmentException(
						"Backend permissions are not allowed to assign to permissions property of Group.class,"
								+ "use method setBackendPermissions of GroupService to set backend permissions of groups instead.");
	}

	public void setDataSource(PermissionDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
