package com.vertonur.dms;

import java.util.Set;

import com.vertonur.dao.api.RoleDAO;
import com.vertonur.pojo.security.Role;

public class RoleService extends GenericService{

	private RoleDAO dao;
	
	protected RoleService(){
		dao=manager.getRoleDAO();
	}
	
	public Integer saveRole(Role role){
		return dao.saveRole(role);
	}
	public void updateRole(Role role){
		dao.updateRole(role);
	}
	public void deleteRole(Role role){
		dao.deleteRole(role);
	}
	
	public Role getRoleById(int id){
		return dao.getRoleById(id);
	}
	public Set<Role> getRoles(){
		return dao.getRoles();
	}
}
