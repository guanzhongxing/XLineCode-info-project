package com.vertonur.security.spring.aop.datasource;

import com.vertonur.dao.api.CommentDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.security.Permission;

public class CommentIdPermissionDataSource extends PermissionDataSource {

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		CommentDAO dao = PojoUtil.getDAOManager().getCommentDAO();
		Comment cmt = dao.getCommentById((Integer) param);
		setProprietaryMark(permissionExample, cmt);
	}
}
