package com.vertonur.security.spring.aop.datasource;

import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.security.Permission;

public class InfoIdPermissionDataSource extends PermissionDataSource {

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		InfoDAO dao = PojoUtil.getDAOManager().getInfoDAO();
		Info info = dao.getInfoById((Integer) param);
		setProprietaryMark(permissionExample, info);
	}
}
