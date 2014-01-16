package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.Admin;

public interface AdminDAO extends GenericDAO<Admin, Integer> {
	Integer saveAdmin(Admin admin);

	Admin getAdminById(int adminId);

	Admin getAdminByName(String name);

	List<Admin> getAdmins();
	
	long getAdminNum();
}
