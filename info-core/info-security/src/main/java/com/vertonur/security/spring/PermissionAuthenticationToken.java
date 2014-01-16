package com.vertonur.security.spring;

import java.util.Set;

import org.springframework.security.core.Authentication;

import com.vertonur.pojo.security.Permission;

public interface PermissionAuthenticationToken extends Authentication {

	/**
	 * Get permissions of the current login in user, either automatically login
	 * or manually login
	 * 
	 * @return
	 */
	public Set<Permission> getPermissions();

	/**
	 * Add a permission entry to the current login user.
	 * 
	 * @param permission
	 */
	public void addPermission(Permission permission);

	/**
	 * @return the isGuest
	 */
	public boolean isGuest();

	/**
	 * @param isGuest
	 *            the isGuest to set
	 */
	public void setGuest(boolean isGuest);
}
