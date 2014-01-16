package com.vertonur.security.spring.aop.datasource;

import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.security.Permission;

public class CategoryIdPermissionDataSource extends PermissionDataSource {

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		Integer categoryId = (Integer) param;
		CategoryDAO dao = PojoUtil.getDAOManager().getCategoryDAO();
		Category category = dao.getCategoryById(categoryId);
		setProprietaryMark(permissionExample, category);
	}
}
