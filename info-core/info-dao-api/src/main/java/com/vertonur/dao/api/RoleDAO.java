package com.vertonur.dao.api;

import java.util.Set;

import com.vertonur.pojo.security.Role;

public interface RoleDAO extends GenericDAO<Role, Integer> {

	public Integer saveRole(Role role);
	public void updateRole(Role role);
	public void deleteRole(Role role);
	
	public Role getRoleById(int id);
	public Set<Role> getRoles();
}
