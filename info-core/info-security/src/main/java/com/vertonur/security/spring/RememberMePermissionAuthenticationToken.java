package com.vertonur.security.spring;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.vertonur.pojo.security.Permission;

public class RememberMePermissionAuthenticationToken extends
		RememberMeAuthenticationToken implements PermissionAuthenticationToken {

	private static final long serialVersionUID = -5160100872996790097L;
	private Set<Permission> permissions;
	private boolean isGuest;

	public RememberMePermissionAuthenticationToken(String key,
			Object principal,
			Collection<? extends GrantedAuthority> authorities,
			Set<Permission> permissions) {
		super(key, principal, authorities);
		this.permissions = permissions;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void addPermission(Permission permission) {
		permissions.add(permission);
	}

	/**
	 * @return the isGuest
	 */
	public boolean isGuest() {
		return isGuest;
	}

	/**
	 * @param isGuest
	 *            the isGuest to set
	 */
	public void setGuest(boolean isGuest) {
		this.isGuest = isGuest;
	}
}
