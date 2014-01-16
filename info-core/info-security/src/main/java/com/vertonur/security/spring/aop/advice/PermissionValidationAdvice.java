package com.vertonur.security.spring.aop.advice;

import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.aop.MethodBeforeAdvice;

import com.vertonur.pojo.security.Permission;
import com.vertonur.security.exception.InsufficientPermissionException;
import com.vertonur.security.spring.aop.datasource.PermissionDataSource;
import com.vertonur.security.util.PermissionUtils;

public class PermissionValidationAdvice implements MethodBeforeAdvice {

	private PermissionDataSource dataSource;

	/**
	 * Validate permissions of the current login user against the permissions
	 * needed to take action, the action will be token only if any of the
	 * permissions returned from PermissionDataSource are met
	 */
	public void before(Method paramMethod, Object[] paramArrayOfObject,
			Object paramObject) throws Throwable {
		Set<Permission> requiredPermissions = dataSource
				.getPermissions(paramArrayOfObject);
		boolean hasPermission = PermissionUtils
				.checkPermissions(requiredPermissions);

		if (!hasPermission) {
			StringBuilder builder = new StringBuilder();
			int size = requiredPermissions.size();
			int counter = 0;
			builder.append("Method invocation failed due to absentation of one of the following permission(s):\n[");
			for (Permission permission : requiredPermissions) {
				builder.append(permission.getName());
				counter++;
				if (counter < size)
					builder.append(",\n");
			}
			builder.append("]");

			throw new InsufficientPermissionException(builder.toString());
		}
	}

	public void setDataSource(PermissionDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
