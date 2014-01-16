/**
 * 
 */
package com.vertonur.security.spring.aop.advice;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.pojo.Category;
import com.vertonur.pojo.security.Permission;
import com.vertonur.security.spring.PermissionAuthenticationToken;
import com.vertonur.security.spring.aop.datasource.PermissionDataSource;

/**
 * @author Vertonur
 * 
 */
public class CategoryListFilterAdvice implements AfterReturningAdvice {

	private PermissionDataSource dataSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang
	 * .Object, java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void afterReturning(Object arg0, Method arg1, Object[] arg2,
			Object arg3) throws Throwable {
		PermissionAuthenticationToken token = (PermissionAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Set<Permission> userPermissions = token.getPermissions();
		List<Category> categories = (List<Category>) arg0;
		Iterator<Category> iterator = categories.iterator();
		while (iterator.hasNext()) {
			Category category = iterator.next();
			Set<Permission> permissions = dataSource.getPermissionSet(category.getId());
			if (!userPermissions.containsAll(permissions))
				iterator.remove();
		}
	}

	/**
	 * @return the dataSource
	 */
	public PermissionDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(PermissionDataSource dataSource) {
		this.dataSource = dataSource;
	}

}
