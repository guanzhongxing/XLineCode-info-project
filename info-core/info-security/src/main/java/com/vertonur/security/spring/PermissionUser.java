package com.vertonur.security.spring;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.vertonur.pojo.security.Permission;

public class PermissionUser extends User {

	private static final long serialVersionUID = -974415009466625040L;
	private Set<Permission> permissions;

	public PermissionUser(int userId, String password,
			Collection<? extends GrantedAuthority> authorities,
			Set<Permission> permissions) {
		super(String.valueOf(userId), password, true, true, true, true,
				authorities);
		this.permissions = permissions;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}
}
