package com.vertonur.security.spring;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.User;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;
import com.vertonur.pojo.security.Role;

public class DaoUserDetailsService implements UserDetailsService {

	/**
	 * @param userName
	 *            the value userName carry should be an user id in info system
	 */
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {

		User user = PojoUtil.getDAOManager().getUserDAO()
				.getUserById(Integer.parseInt(userName));
		return assembleData(user);
	}

	private UserDetails assembleData(User user) {

		Set<Group> groups = user.getGroups();
		Set<Role> roles = new HashSet<Role>();
		Set<Permission> permissions = new HashSet<Permission>();
		for (Group group : groups) {
			roles.addAll(group.getRoles());
			permissions.addAll(group.getPermissions());
			permissions.addAll(group.getBackendPermissions());
		}

		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (Role role : roles) {
			GrantedAuthority authority = new SimpleGrantedAuthority(
					role.getName());
			authorities.add(authority);
		}
		PermissionUser permissionUser = new PermissionUser(user.getId(),
				user.getPassword(), authorities, permissions);
		return permissionUser;
	}
}
